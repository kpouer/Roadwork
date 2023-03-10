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
package com.kpouer.roadwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kpouer.mapview.marker.Circle;
import com.kpouer.mapview.marker.Marker;
import com.kpouer.mapview.marker.PolygonMarker;
import com.kpouer.roadwork.model.sync.Status;
import com.kpouer.roadwork.model.sync.SyncData;
import com.kpouer.wkt.shape.Polygon;
import lombok.*;
import org.springframework.lang.NonNull;

import java.awt.*;

/**
 * @author Matthieu Casanova
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Roadwork {
    @NonNull
    private String id;
    private double latitude;
    private double longitude;
    private Polygon[] polygons;
    private long start;
    private long end;
    private String road;
    private String locationDetails;
    private String impactCirculationDetail;
    private String description;
    @JsonIgnore
    private Marker[] markers;
    private SyncData syncData;
    private String url;

    public void updateStatus(Status status) {
        syncData.updateStatus(status);
        updateMarker();
    }

    public void updateMarker() {
        if (markers != null) {
            for (Marker marker : markers) {
                Color color = getColor();
                if (marker instanceof PolygonMarker) {
                    color = new Color(color.getRed() / 255.0f,
                                      color.getGreen() / 255.0f,
                                      color.getBlue() / 255.0f,
                                      0.5f);
                }
                marker.setColor(color);
            }
        }
    }

    @JsonIgnore
    public Marker[] getMarker() {
        if (markers == null) {
            if (polygons != null) {
                markers = new Marker[polygons.length];
                for (int i = 0; i < polygons.length; i++) {
                    Polygon polygon = polygons[i];
                    Color color = getColor();
                    markers[i] = new PolygonMarker(polygon, 2, color, true);
                }
                updateMarker();
            } else {
                markers = new Marker[1];
                markers[0] = new Circle(latitude, longitude, 5, getColor());
            }
        }
        return markers;
    }

    @JsonIgnore
    private Color getColor() {
        switch (getSyncData().getStatus()) {
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

    @Override
    public String toString() {
        return "Roadwork[" + id + ']';
    }
}
