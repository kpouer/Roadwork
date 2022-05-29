package com.kpouer.roadwork.opendata.json.model;

import com.kpouer.mapview.LatLng;

import java.util.Locale;

public class ServiceDescriptor {
    private String country;
    private double[] center;
    private String url;
    private String name;
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
