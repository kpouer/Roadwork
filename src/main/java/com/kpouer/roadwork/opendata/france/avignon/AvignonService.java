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
package com.kpouer.roadwork.opendata.france.avignon;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.avignon.model.AvignonOpendataResponse;
import com.kpouer.roadwork.opendata.france.avignon.model.Feature;
import com.kpouer.roadwork.opendata.france.avignon.model.Geometry;
import com.kpouer.roadwork.opendata.france.avignon.model.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Matthieu Casanova
 */
@Service("AvignonService")
public class AvignonService extends AbstractOpendataService<Feature, AvignonOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(AvignonService.class);

    public static final String SOURCE_URL = "https://trouver.datasud.fr/dataset/avignon-arretes-travaux-avec-impact-circulation";
    private static final String URL = "https://trouver.datasud.fr/dataset/d3055f04-fd52-4e30-b04a-4d485a75355b/resource/72ae99d6-3b1e-4598-b8cb-d3ff463b03d0/download/84007-travauxarretes.geojson";

    public AvignonService(RestTemplate restTemplate) {
        super(new LatLng(43.94566, 4.80955), URL, AvignonOpendataResponse.class, restTemplate);
    }

    @Override
    protected Roadwork getRoadwork(Feature feature) {
        Properties properties = feature.getProperties();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        long dateStart = 0;
        try {
            dateStart = format.parse(properties.getArretedebut()).getTime();
        } catch (ParseException e) {
        }
        long dateEnd = 0;
        try {
            dateEnd = format.parse(properties.getArretefin()).getTime() + 85399000;

        } catch (ParseException e) {
        }
        Geometry geometry = feature.getGeometry();
        return RoadworkBuilder
                .aRoadwork()
                .withId(properties.getIdarrete())
                .withLatitude(geometry.getCoordinates()[0][0][0][1])
                .withLongitude(geometry.getCoordinates()[0][0][0][0])
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(properties.getLocalisations())
                .withDescription(properties.getMesures())
                .withUrl(properties.getShorturl())
                .build();
    }
}
