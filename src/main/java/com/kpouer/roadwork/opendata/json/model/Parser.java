package com.kpouer.roadwork.opendata.json.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String format;
    private String matcher;
    private boolean addYear;
    private boolean resetHour = true;
    private Pattern pattern;

    public String getFormat(){
        return format;
    }

    public boolean isAddYear() {
        return addYear;
    }

    public String getMatcher(){
        return matcher;
    }

    public boolean isResetHour() {
        return resetHour;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public void setAddYear(boolean addYear) {
        this.addYear = addYear;
    }

    public void setResetHour(boolean resetHour) {
        this.resetHour = resetHour;
    }

    public Matcher match(String value) {
        if (pattern == null) {
            pattern = Pattern.compile(matcher);
        }
        return pattern.matcher(value);
    }
}
