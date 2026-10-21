package com.bettercontent.betteroc2rwirelessmessaging.diagnostics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public final class FeatureObservationTest {
    @Test public void nativeScalarsHaveExplicitStage() { var fields = FeatureObservation.fields("00000000-0000-0000-0000-000000000001", 33, 6); assertTrue(fields.contains("bytes=33")); assertTrue(fields.contains("energy_charged=6")); assertTrue(fields.contains("durability=not_observed")); }
    @Test public void uncertaintyAndPrivacyStayExplicit() { var fields = FeatureObservation.fields("private_topic_or_payload", 0, 0); assertFalse(fields.contains("private")); assertFalse(fields.contains("topic")); assertFalse(fields.contains("payload")); assertTrue(fields.contains("operation=UNKNOWN")); }
}
