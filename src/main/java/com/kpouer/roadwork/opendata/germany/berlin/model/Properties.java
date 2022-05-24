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
package com.kpouer.roadwork.opendata.germany.berlin.model;

/**
 * @author Matthieu Casanova
 */
public class Properties {
    private String id;
    private String street;
    private String section;
    private Validity validity;
    private String content;
    private String direction;

    public String getStreet() {
        return street;
    }

    public String getSection() {
        return section;
    }

    public String getId() {
        return id;
    }

    public Validity getValidity() {
        return validity;
    }

    public String getContent() {
        return content;
    }

    public String getDirection() {
        return direction;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setValidity(Validity validity) {
        this.validity = validity;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "id='" + id + '\'' +
                ", street='" + street + '\'' +
                ", section='" + section + '\'' +
                ", validity=" + validity +
                ", content='" + content + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
