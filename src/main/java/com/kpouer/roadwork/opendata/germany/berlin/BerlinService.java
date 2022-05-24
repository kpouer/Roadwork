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
package com.kpouer.roadwork.opendata.germany.berlin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.germany.berlin.model.*;
import com.kpouer.roadwork.opendata.model.GeometryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
@Service("BerlinService")
public class BerlinService extends AbstractOpendataService<Feature, BerlinOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(BerlinService.class);
    private static final String SOURCE_URL = "https://daten.berlin.de/datensaetze/baustellen-sperrungen-und-sonstige-st%C3%B6rungen-von-besonderem-verkehrlichem-interesse";
    private static final String URL = "https://api.viz.berlin.de/daten/baustellen_sperrungen.json";

    public BerlinService() {
        super(new LatLng(52.51935, 13.41156), URL, BerlinOpendataResponse.class);
    }

    @Override
    protected Roadwork getRoadwork(Feature feature) {
        Properties properties = feature.getProperties();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        long dateStart = 0;
        Validity validity = properties.getValidity();
        try {
            dateStart = format.parse(validity.getFrom()).getTime();
        } catch (Exception e) {
            logger.error("Unable to parse from date {} of feature {}", validity.getFrom(), feature);
        }
        long dateEnd = 0;
        try {
            if (validity.getTo() != null) {
                dateEnd = format.parse(validity.getTo()).getTime() + 85399000;
            }
        } catch (Exception e) {
            logger.error("Unable to parse to date {} of feature {}", validity.getTo(), feature);
        }
        Geometry geometry = feature.getGeometry();
        double[] coordinates = geometry.getCoordinates();
        double latitude;
        double longitude;
        if (coordinates != null) {
            latitude = coordinates[1];
            longitude = coordinates[0];
        } else {
            List<GeometrysItem> geometries = geometry.getGeometries();
            Optional<GeometrysItem> pointOptional = geometries
                    .stream()
                    .filter(geometrysItem -> geometrysItem.getType() == GeometryType.Point)
                    .findFirst();
            if (pointOptional.isPresent()) {
                GeometrysItem geometrysItem = pointOptional.get();
                ArrayNode jsonNodeCoordinates = (ArrayNode) geometrysItem.getCoordinates();
                latitude = jsonNodeCoordinates.get(1).asDouble();
                longitude = jsonNodeCoordinates.get(0).asDouble();
            } else {
                logger.error("Unable to get location for {}", feature);
                return null;
            }
        }

        String road = properties.getStreet();
        return RoadworkBuilder.aRoadwork()
                .withId(properties.getId())
                .withLatitude(latitude)
                .withLongitude(longitude)
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(road)
                .withLocationDetails(properties.getSection())
                .withImpactCirculationDetail(properties.getContent())
                .build();
    }
}
