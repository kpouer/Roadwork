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

import com.kpouer.mapview.LatLng;

import java.util.Locale;

public class ServiceDescriptor {
    private String country;
    private double[] center;
    private String source_url;
    private String url;
    private String name;
    private String producer;
    private String licenceName;
    private String licenceUrl;
    private String id;
    private String latitude;
    private String longitude;
    private String road;
    private String description;
    private String locationDetails;
    private String impactCirculationDetail;
    private Locale locale;
    private DateParser from;
    private DateParser to;
    private String roadworkArray;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public LatLng getCenter() {
        return new LatLng(center[0], center[1]);
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getLicenceName() {
        return licenceName;
    }

    public void setLicenceName(String licenceName) {
        this.licenceName = licenceName;
    }

    public String getLicenceUrl() {
        return licenceUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRoadworkArray() {
        return roadworkArray;
    }

    public void setRoadworkArray(String roadworkArray) {
        this.roadworkArray = roadworkArray;
    }

    @Override
    public String toString() {
        return "ServiceDescriptor{" +
                "country='" + country + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
