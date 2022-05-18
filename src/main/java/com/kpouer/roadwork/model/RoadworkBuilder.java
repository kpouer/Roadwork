package com.kpouer.roadwork.model;

import com.kpouer.roadwork.model.sync.SyncData;

public final class RoadworkBuilder {
    private String id;
    private double latitude;
    private double longitude;
    private long start;
    private long end;
    private String road;
    private String locationDetails;
    private String impactCirculationDetail;
    private String description;
    private SyncData syncData;
    private String shortUrl;

    private RoadworkBuilder() {
        syncData = new SyncData();
    }

    public static RoadworkBuilder aRoadwork() {
        return new RoadworkBuilder();
    }

    public RoadworkBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public RoadworkBuilder withLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public RoadworkBuilder withLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public RoadworkBuilder withStart(long start) {
        this.start = start;
        return this;
    }

    public RoadworkBuilder withEnd(long end) {
        this.end = end;
        return this;
    }

    public RoadworkBuilder withRoad(String road) {
        this.road = road;
        return this;
    }

    public RoadworkBuilder withLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
        return this;
    }

    public RoadworkBuilder withImpactCirculationDetail(String impactCirculationDetail) {
        this.impactCirculationDetail = impactCirculationDetail;
        return this;
    }

    public RoadworkBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public RoadworkBuilder withSyncData(SyncData syncData) {
        this.syncData = syncData;
        return this;
    }

    public RoadworkBuilder withUrl(String shorturl) {
        this.shortUrl = shorturl;
        return this;
    }

    public Roadwork build() {
        Roadwork roadwork = new Roadwork();
        roadwork.setId(id);
        roadwork.setLatitude(latitude);
        roadwork.setLongitude(longitude);
        roadwork.setStart(start);
        roadwork.setEnd(end);
        roadwork.setRoad(road);
        roadwork.setLocationDetails(locationDetails);
        roadwork.setImpactCirculationDetail(impactCirculationDetail);
        roadwork.setDescription(description);
        roadwork.setSyncData(syncData);
        roadwork.setShortUrl(shortUrl);
        return roadwork;
    }
}
