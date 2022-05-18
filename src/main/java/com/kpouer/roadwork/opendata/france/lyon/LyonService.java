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
package com.kpouer.roadwork.opendata.france.lyon;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.lyon.model.Features;
import com.kpouer.roadwork.opendata.france.lyon.model.LyonOpendataResponse;
import com.kpouer.roadwork.opendata.france.lyon.model.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
@Service("LyonService")
public class LyonService extends AbstractOpendataService<LyonOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(LyonService.class);
    private static final String URL = "https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&request=GetFeature&typename=pvo_patrimoine_voirie.pvochantierperturbant&outputFormat=application/json; subtype=geojson&SRSNAME=EPSG:4171&startIndex=0&count=1000";

    public LyonService() {
        super(new LatLng(45.75627, 4.85115), URL, LyonOpendataResponse.class);
    }

    @Override
    protected RoadworkData getRoadworkData(LyonOpendataResponse opendataResponse) {
        logger.info("getRoadworkData {}", opendataResponse);
        List<Roadwork> roadworks = opendataResponse.getFeatures()
                .stream()
                .map(this::getRoadwork)
                .toList();
        return new RoadworkData(getClass().getSimpleName(), roadworks);
    }

    private Roadwork getRoadwork(Features record) {
        Properties fields = record.getProperties();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        long dateStart = 0;
        try {
            dateStart = format.parse(fields.getDebutchantier()).getTime();
        } catch (ParseException e) {
        }
        long dateEnd = 0;
        try {
            dateEnd = format.parse(fields.getFinchantier()).getTime() + 85399000;

        } catch (ParseException e) {
        }
        return RoadworkBuilder
                .aRoadwork()
                .withId(Integer.toString(fields.getIdentifiant()))
                .withLatitude(record.getGeometry().getCoordinates()[0][0][1])
                .withLongitude(record.getGeometry().getCoordinates()[0][0][0])
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(fields.getNom())
                .withLocationDetails(fields.getPrecisionlocalisation())
                .withImpactCirculationDetail(fields.getTypeperturbation())
                .withDescription(fields.getNomchantier())
                .build();
    }
}
