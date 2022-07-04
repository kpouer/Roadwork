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
package com.kpouer.roadwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kpouer.mapview.marker.Circle;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.model.sync.SyncData;

import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public class Roadwork {
    private String id;
    private double latitude;
    private double longitude;
    private long start;
    private long end;
    private String road;
    private String locationDetails;
    private String impactCirculationDetail;
    private String description;
    @JsonIgnore
    private Circle marker;
    private SyncData syncData;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void updateStatus(Status status) {
        syncData.updateStatus(status);
        updateMarker();
    }

    public void updateMarker() {
        if (marker != null) {
            marker.setColor(getColor());
        }
    }

    public Circle getMarker() {
        if (marker == null) {
            marker = new Circle(latitude, longitude, 5, getColor());
        }
        return marker;
    }

    private Color getColor() {
        switch (syncData.getStatus()) {
            case New -> {
                return Color.RED;
            }
            case Treated -> {
                return Color.GREEN;
            }
            case Ignored -> {
                return Color.YELLOW;
            }
            case Later -> {
                return Color.BLUE;
            }
            case Finished -> {
                return Color.GRAY;
            }
        }
        throw new RuntimeException("Unexpected status " + start);
    }

    public boolean isExpired() {
        return end < System.currentTimeMillis();
    }

    public SyncData getSyncData() {
        return syncData;
    }

    public void setSyncData(SyncData syncData) {
        this.syncData = syncData;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
