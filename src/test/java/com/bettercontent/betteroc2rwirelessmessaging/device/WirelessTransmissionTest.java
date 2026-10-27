package com.bettercontent.betteroc2rwirelessmessaging.device;

import com.bettercontent.betteroc2rwirelessmessaging.data.WirelessNetworkSavedData;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WirelessTransmissionTest {
    @Test void utf8RoundingAndSenderOnlyDebit() {
        var broker = new WirelessNetworkSavedData();
        var sender = new AtomicInteger(100);
        var debitCalls = new AtomicInteger();
        String[] payloads = {"", "a", "a".repeat(31), "a".repeat(32), "a".repeat(33), "é".repeat(16), "😀".repeat(8) + "a"};
        int[] costs = {4, 5, 5, 5, 6, 5, 6};
        int[] bytes = {0, 1, 31, 32, 33, 32, 33};
        for (int i = 0; i < payloads.length; i++) {
            int before = sender.get();
            var origin = WirelessTransmission.send(broker, "test", payloads[i], UUID.randomUUID(), "sender", cost -> {
                assertEquals(before, sender.get());
                debitCalls.incrementAndGet();
                sender.addAndGet(-cost);
                return true;
            });
            assertEquals(costs[i], WirelessTransmission.energyCost(payloads[i]));
            assertEquals(costs[i], origin.energy());
            assertEquals(bytes[i], origin.bytes());
            assertEquals(before - costs[i], sender.get());
            var delivered = broker.pollDelivered("test", "receiver", 1);
            assertEquals(1, delivered.size());
            assertEquals(payloads[i], delivered.get(0).payload());
            assertEquals(origin, delivered.get(0).origin());
            assertEquals(i + 1, debitCalls.get(), "receiving must not invoke sender debit again");
            assertEquals(before - costs[i], sender.get());
        }
    }

    @Test void rejectedAdmissionNeverDebitsOrPublishes() {
        var broker = new WirelessNetworkSavedData();
        var debitCalls = new AtomicInteger();
        for (String payload : new String[]{null, "é".repeat(2049)}) {
            assertThrows(IllegalArgumentException.class, () -> WirelessTransmission.send(broker, "test", payload, null, "sender", cost -> {
                debitCalls.incrementAndGet(); return true;
            }));
        }
        assertThrows(IllegalArgumentException.class, () -> WirelessTransmission.send(broker, " ", "ok", null, "sender", cost -> {
            debitCalls.incrementAndGet(); return true;
        }));
        for (int i = 0; i < WirelessNetworkSavedData.MAX_TOPICS; i++) broker.publish("topic" + i, "seed");
        assertThrows(IllegalArgumentException.class, () -> WirelessTransmission.send(broker, "overflow", "ok", null, "sender", cost -> {
            debitCalls.incrementAndGet(); return true;
        }));
        assertEquals(0, debitCalls.get());
        assertEquals(0, broker.topicDepth("test"));
        assertEquals(0, broker.topicDepth("overflow"));
    }

    @Test void insufficientEnergyDoesNotPublishAndDebitPrecedesPublication() {
        var broker = new WirelessNetworkSavedData();
        assertThrows(IllegalStateException.class, () -> WirelessTransmission.send(broker, "test", "a", null, "sender", cost -> {
            assertEquals(5, cost);
            assertEquals(0, broker.topicDepth("test"));
            return false;
        }));
        assertTrue(broker.listTopics().isEmpty());
        WirelessTransmission.send(broker, "test", "a", null, "sender", cost -> {
            assertEquals(0, broker.topicDepth("test")); return true;
        });
        assertEquals(1, broker.topicDepth("test"));
    }
}
