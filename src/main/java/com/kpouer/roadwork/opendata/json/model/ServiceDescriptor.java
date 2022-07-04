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

public class ServiceDescriptor {
    private Metadata metadata;
    private String id;
    private String latitude;
    private String longitude;
    private String road;
    private String description;
    private String locationDetails;
    private String impactCirculationDetail;
    private DateParser from;
    private DateParser to;
    private String roadworkArray;
    private String url;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getImpactCirculationDetail() {
        return impactCirculationDetail;
    }

    public void setImpactCirculationDetail(String impactCirculationDetail) {
        this.impactCirculationDetail = impactCirculationDetail;
    }

    public DateParser getFrom() {
        return from;
    }

    public void setFrom(DateParser from) {
        this.from = from;
    }

    public DateParser getTo() {
        return to;
    }

    public void setTo(DateParser to) {
        this.to = to;
    }


    public String getRoadworkArray() {
        return roadworkArray;
    }

    public void setRoadworkArray(String roadworkArray) {
        this.roadworkArray = roadworkArray;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ServiceDescriptor{" +
                "country='" + metadata.getCountry() + '\'' +
                ", name='" + metadata.getName() + '\'' +
                '}';
    }
}
