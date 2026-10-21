package com.bettercontent.betteroc2rwirelessmessaging.diagnostics;

/** Pure projection of values already observed by the owning native decision. */
public final class FeatureObservation {
    private FeatureObservation() {}
    public static String fields(String operation, int bytes, int energy) { String id = "UNKNOWN"; try { var parsed = java.util.UUID.fromString(operation); if (parsed.toString().equals(operation)) id = operation; } catch (IllegalArgumentException | NullPointerException ignored) {} return PlaytestLog.fields("operation", id, "bytes", bytes, "energy_charged", energy, "durability", "not_observed"); }
    
}
