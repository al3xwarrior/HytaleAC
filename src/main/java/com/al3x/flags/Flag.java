package com.al3x.flags;

public abstract class Flag {
    private final long timestamp;

    protected Flag() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract int getMaxFlags(); // Maximum flags before action is taken
    public abstract String getSource(); // Source of the flag (eg Speed Packet, Interaction Packet)
    public abstract String getReason(); // Short reason for the flag (eg moved too quickly)
    public abstract String getDetails(); // Detailed information (how fast they moved, etc)

    @Override
    public abstract String toString();
}

