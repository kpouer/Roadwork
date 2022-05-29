package com.kpouer.roadwork.opendata.json.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateParser {
    private String path;
    private List<Parser> parsers;
    public String getPath() {
        return path;
    }

    public List<Parser> getParsers(){
        return parsers;
    }

    public DateResult parse(String value, Locale locale) throws ParseException {
        for (var parser : parsers) {
            var match = parser.match(value);
            if (match.matches()) {
                var str = match.groupCount() == 1 ? match.group(1) : match.group(0);
                long timestamp = parseDate(parser.getFormat(), str, locale);
                return new DateResult(timestamp, parser);
            }
        }
        throw new ParseException("Unable to parse date " + value, 0);
    }

    private long parseDate(String pattern, String value, Locale locale) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
        Date date = simpleDateFormat.parse(value);
        return date.getTime();
    }
}