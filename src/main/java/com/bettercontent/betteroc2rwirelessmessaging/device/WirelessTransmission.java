package com.bettercontent.betteroc2rwirelessmessaging.device;

import com.bettercontent.betteroc2rwirelessmessaging.data.WirelessNetworkSavedData;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.IntPredicate;

/** Server-thread send boundary: admission before sender debit, then publication. */
public final class WirelessTransmission {
    private WirelessTransmission() {}

    public static int energyCost(String payload) {
        int bytes = payload.getBytes(StandardCharsets.UTF_8).length;
        return 4 + (bytes + 31) / 32;
    }

    public static WirelessNetworkSavedData.Origin send(WirelessNetworkSavedData broker, String topic,
            String payload, UUID owner, String host, IntPredicate debitSender) {
        if (!broker.canPublish(topic, payload)) {
            throw new IllegalArgumentException("wireless topic, payload, or storage limit rejected publication");
        }
        int bytes = payload.getBytes(StandardCharsets.UTF_8).length;
        int cost = energyCost(payload);
        if (!debitSender.test(cost)) throw new IllegalStateException("Source relay has insufficient energy.");
        var origin = new WirelessNetworkSavedData.Origin(owner, host, UUID.randomUUID().toString(), cost, bytes);
        // Callers run on the server thread. Preflight and publish cannot interleave with another RPC.
        // Unexpected publication exceptions are not claimed to refund a successful debit.
        broker.publish(topic, payload, origin);
        return origin;
    }
}
