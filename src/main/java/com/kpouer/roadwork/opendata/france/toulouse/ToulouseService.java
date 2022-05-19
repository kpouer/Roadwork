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
package com.kpouer.roadwork.opendata.france.toulouse;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.toulouse.model.Fields;
import com.kpouer.roadwork.opendata.france.toulouse.model.Geometry;
import com.kpouer.roadwork.opendata.france.toulouse.model.Record;
import com.kpouer.roadwork.opendata.france.toulouse.model.ToulouseOpendataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Matthieu Casanova
 */
@Service("ToulouseService")
public class ToulouseService extends AbstractOpendataService<Record, ToulouseOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ToulouseService.class);
    private static final String URL = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=chantiers-en-cours&q=&rows=1000&facet=voie&facet=commune&facet=pole&facet=declarant&facet=entreprise&facet=datedebut&facet=datefin";

    public ToulouseService() {
        super(new LatLng(43.60072, 1.44118), URL, ToulouseOpendataResponse.class);
    }

    @Override
    protected Roadwork getRoadwork(Record record) {
        Fields fields = record.getFields();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long dateStart = 0;
        try {
            dateStart = format.parse(fields.getDatedebut()).getTime();
        } catch (ParseException e) {
        }
        long dateEnd = 0;
        try {
            dateEnd = format.parse(fields.getDatefin()).getTime() + 85399000;

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
                .withRoad(fields.getVoie())
                .withLocationDetails(fields.getLibelle())
                .withImpactCirculationDetail(fields.getCirculation())
                .build();
    }
}
