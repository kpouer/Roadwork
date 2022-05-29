package com.kpouer.roadwork.opendata.json.model;

public class DateResult {
    private long date;
    private final Parser parser;

    public DateResult(long date, Parser parser) {
        this.date = date;
        this.parser = parser;
    }

    public long getDate() {
        return date;
    }

    public Parser getParser() {
        return parser;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
