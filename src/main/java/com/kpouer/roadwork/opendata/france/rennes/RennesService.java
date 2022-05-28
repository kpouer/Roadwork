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
package com.kpouer.roadwork.opendata.france.rennes;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.rennes.model.FeaturesItem;
import com.kpouer.roadwork.opendata.france.rennes.model.Geometry;
import com.kpouer.roadwork.opendata.france.rennes.model.Properties;
import com.kpouer.roadwork.opendata.france.rennes.model.RennesOpendataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Matthieu Casanova
 */
@Service("RennesService")
public class RennesService extends AbstractOpendataService<FeaturesItem, RennesOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RennesService.class);

    private static final String SOURCE_URL = "http://travaux.data.rennesmetropole.fr/#roadwork-info";
    private static final String URL = "http://travaux.data.rennesmetropole.fr/api/roadworks?epsg=4326";

    public RennesService(RestTemplate restTemplate) {
        super(new LatLng(48.10881, -1.67018), URL, RennesOpendataResponse.class, restTemplate);
    }

    @Override
    protected Roadwork getRoadwork(FeaturesItem featuresItem) {
        Properties properties = featuresItem.getProperties();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        long dateStart = 0;
        try {
            dateStart = format.parse(properties.getDate_deb()).getTime();
        } catch (ParseException e) {
        }
        long dateEnd = 0;
        try {
            dateEnd = format.parse(properties.getDate_fin()).getTime() + 85399000;

        } catch (ParseException e) {
        }
        Geometry geometry = featuresItem.getGeometry();
        return RoadworkBuilder
                .aRoadwork()
                .withId(Integer.toString(properties.getId()))
                .withLatitude(geometry.getCoordinates().get(0)[1])
                .withLongitude(geometry.getCoordinates().get(0)[0])
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(properties.getLocalisation())
                .withDescription(properties.getLibelle())
                .withImpactCirculationDetail(properties.getType())
                .build();
    }
}
