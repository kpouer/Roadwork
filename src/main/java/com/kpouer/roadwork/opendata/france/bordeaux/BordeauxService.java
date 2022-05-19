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
package com.kpouer.roadwork.opendata.france.bordeaux;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.bordeaux.model.BordeauxOpendataResponse;
import com.kpouer.roadwork.opendata.france.bordeaux.model.Fields;
import com.kpouer.roadwork.opendata.france.bordeaux.model.Geometry;
import com.kpouer.roadwork.opendata.france.bordeaux.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Matthieu Casanova
 */
@Service("BordeauxService")
public class BordeauxService extends AbstractOpendataService<Record, BordeauxOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(BordeauxService.class);
    private static final String URL = "https://opendata.bordeaux-metropole.fr/api/records/1.0/search/?dataset=ci_chantier&q=&rows=1000&facet=alias_nature_n1&facet=alias_nature_n2&facet=geo_shape_type";

    public BordeauxService() {
        super(new LatLng(44.84492, -0.57352), URL, BordeauxOpendataResponse.class);
    }

    @Override
    protected Roadwork getRoadwork(Record record) {
        Fields fields = record.getFields();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long dateStart = 0;
        try {
            dateStart = format.parse(fields.getDate_debut()).getTime();
        } catch (ParseException e) {
        }
        long dateEnd = 0;
        try {
            dateEnd = format.parse(fields.getDate_fin()).getTime() + 85399000;

        } catch (ParseException e) {
        }
        Geometry geometry = record.getGeometry();
        return RoadworkBuilder
                .aRoadwork()
                .withId(record.getRecordid())
                .withLatitude(geometry.getCoordinates()[1])
                .withLongitude(geometry.getCoordinates()[0])
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(fields.getLocalisation())
                .withDescription(fields.getLocalisation_emprise())
                .withImpactCirculationDetail(fields.getLibelle())
                .build();
    }
}
