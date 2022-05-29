package com.kpouer.roadwork.model;

public class DateRange {
    private final long from;
    private final long to;

    public DateRange(long from, long to) {
        this.from = from;
        this.to = to;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "DateRange{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
