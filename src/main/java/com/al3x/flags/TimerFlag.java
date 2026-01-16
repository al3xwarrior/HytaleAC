package com.al3x.flags;

public class TimerFlag extends Flag {

    private final long msDifference;
    private final SourceType source;
    private final int maxFlags;

    public TimerFlag(long msDifference, SourceType source, int maxFlags) {
        this.msDifference = msDifference;
        this.source = source;
        this.maxFlags = maxFlags;
    }

    @Override
    public int getMaxFlags() {
        return maxFlags;
    }

    @Override
    public String getSource() {
        return source.toString();
    }

    @Override
    public String getReason() {
        return "moving too quickly (Timer)";
    }

    @Override
    public String getDetails() {
        return "MS Diff: " + msDifference;
    }

    @Override
    public String toString() {
        return "TimerFlag{msDifference=" + msDifference + '}';
    }

    public enum SourceType {
        INTERACTION_PACKET,
        MOVEMENT_PACKET
    }
}
