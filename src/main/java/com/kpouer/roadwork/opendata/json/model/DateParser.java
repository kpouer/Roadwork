/*
 * Copyright 2022 Matthieu Casanova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kpouer.roadwork.opendata.json.model;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public DateResult parse(@NotNull String value, Locale locale) throws ParseException {
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
        var simpleDateFormat = new SimpleDateFormat(pattern, locale);
        var date = simpleDateFormat.parse(value);
        return date.getTime();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParsers(List<Parser> parsers) {
        this.parsers = parsers;
    }
}