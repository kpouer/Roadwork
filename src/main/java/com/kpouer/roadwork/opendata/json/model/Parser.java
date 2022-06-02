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
