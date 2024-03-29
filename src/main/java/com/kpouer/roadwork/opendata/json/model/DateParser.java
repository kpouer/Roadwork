/*
 * Copyright 2022-2023 Matthieu Casanova
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

import lombok.Getter;
import lombok.Setter;
import jakarta.annotation.Nonnull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@Setter
public class DateParser {
    private String path;
    private List<Parser> parsers;

    public DateResult parse(@Nonnull String value, Locale locale) throws ParseException {
        for (var parser : parsers) {
            var match = parser.match(value);
            if (match.matches()) {
                var str = match.groupCount() == 1 ? match.group(1) : match.group(0);
                var format = parser.getFormat();
                long timestamp;
                if (format == null) {
                    // format is null then it must be a timestamp in seconds or ms
                    timestamp = Long.parseLong(str);
                    if (timestamp < 1000000000000L) {
                        // the timestamp was in second
                        timestamp *= 1000L;
                    }
                } else {
                    timestamp = parseDate(format, str, locale);
                }
                return new DateResult(timestamp, parser);
            }
        }
        throw new ParseException("Unable to parse date '" + value + "' with parsers :" + toStringParsers(), 0);
    }

    private String toStringParsers() {
        return parsers.stream()
                .map(Parser::getFormat)
                .collect(Collectors.joining("|"));
    }

    private long parseDate(String pattern, String value, Locale locale) throws ParseException {
        var simpleDateFormat = new SimpleDateFormat(pattern, locale);
        var date = simpleDateFormat.parse(value);
        return date.getTime();
    }
}