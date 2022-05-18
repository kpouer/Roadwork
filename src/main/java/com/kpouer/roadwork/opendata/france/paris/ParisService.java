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
package com.kpouer.roadwork.opendata.france.paris;

import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.AbstractOpendataService;
import com.kpouer.roadwork.opendata.france.paris.model.Fields;
import com.kpouer.roadwork.opendata.france.paris.model.Geometry;
import com.kpouer.roadwork.opendata.france.paris.model.ParisOpendataResponse;
import com.kpouer.roadwork.opendata.france.paris.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Matthieu Casanova
 */
@Service("ParisService")
public class ParisService extends AbstractOpendataService<ParisOpendataResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ParisService.class);
    private static final String URL = "https://opendata.paris.fr/api/records/1.0/search/?dataset=chantiers-perturbants&q=&rows=1000&facet=cp_arrondissement&facet=typologie&facet=maitre_ouvrage&facet=objet&facet=impact_circulation&facet=niveau_perturbation&facet=statut&exclude.statut=5";

    public ParisService() {
        super(new LatLng(48.85337, 2.34847), URL, ParisOpendataResponse.class);
    }

    @Override
    protected RoadworkData getRoadworkData(ParisOpendataResponse opendataResponse) {
        logger.info("getRoadworkData {}", opendataResponse);
        List<Roadwork> roadworks = Arrays.stream(opendataResponse.getRecords())
                .filter(record -> record.getGeometry() != null)
                .map(this::getRoadwork)
                .toList();
        return new RoadworkData(getClass().getSimpleName(), roadworks);
    }

    private Roadwork getRoadwork(Record record) {
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
        Objects.requireNonNull(geometry, "Unable to create roadwork " + record);
        return RoadworkBuilder.aRoadwork()
                .withId(record.getRecordid())
                .withLatitude(geometry.getCoordinates()[1])
                .withLongitude(geometry.getCoordinates()[0])
                .withStart(dateStart)
                .withEnd(dateEnd)
                .withRoad(fields.getVoie())
                .withLocationDetails(fields.getPrecision_localisation())
                .withImpactCirculationDetail(fields.getImpact_circulation_detail())
                .withDescription(fields.getDescription())
                .build();
    }
}
