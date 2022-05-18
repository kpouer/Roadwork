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
package com.kpouer.roadwork.model.v1;

import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.model.sync.SyncData;

import static com.kpouer.roadwork.model.sync.Status.New;

/**
 * @author Matthieu Casanova
 */
public class RoadworkV1 {
    private String id;
    private double latitude;
    private double longitude;
    private long start;
    private long end;
    private String road;
    private String locationDetails;
    private String postCode;
    private String impactCirculationDetail;
    private String description;

    private Status status = New;

    /**
     * Time of the last update, given by the server
     */
    private long lastUpdate;

    public RoadworkV1() {
    }

    public RoadworkV1(String id,
                      double latitude,
                      double longitude,
                      long start,
                      long end,
                      String road,
                      String locationDetails,
                      String postCode,
                      String impactCirculationDetail,
                      String description) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.start = start;
        this.end = end;
        this.road = road;
        this.locationDetails = locationDetails;
        this.postCode = postCode;
        this.impactCirculationDetail = impactCirculationDetail;
        this.description = description;
    }

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

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public Roadwork toRoadwork() {
        Roadwork roadwork = RoadworkBuilder.aRoadwork()
                .withLatitude(latitude)
                .withLongitude(longitude)
                .withStart(start)
                .withEnd(end)
                .withLocationDetails(locationDetails)
                .withImpactCirculationDetail(impactCirculationDetail)
                .withDescription(description)
                .build();
        SyncData syncData = roadwork.getSyncData();
        syncData.setStatus(status);
        syncData.setLocalUpdateTime(lastUpdate);
        return roadwork;
    }
}
