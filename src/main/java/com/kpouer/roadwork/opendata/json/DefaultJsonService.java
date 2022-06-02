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
package com.kpouer.roadwork.opendata.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.DateRange;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.model.DateParser;
import com.kpouer.roadwork.opendata.json.model.DateResult;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultJsonService implements OpendataService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJsonService.class);

    private final RestTemplate restTemplate;
    private final ServiceDescriptor serviceDescriptor;

    public DefaultJsonService(RestTemplate restTemplate, ServiceDescriptor serviceDescriptor) {
        this.restTemplate = restTemplate;
        this.serviceDescriptor = serviceDescriptor;
    }

    @Override
    public Optional<RoadworkData> getData() throws RestClientException {
        String json = restTemplate.getForObject(serviceDescriptor.getUrl(), String.class);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        List<?> roadworkArray = JsonPath.read(document, serviceDescriptor.getRoadworkArray());
        List<Roadwork> roadworks = roadworkArray
                .parallelStream()
                .map(this::buildRoadwork)
                .filter(Objects::nonNull)
                .filter(DefaultJsonService::isValid)
                .toList();
        return Optional.of(new RoadworkData(serviceDescriptor.getName(), roadworks));
    }

    private static boolean isValid(Roadwork roadwork) {
        if (roadwork.getLongitude() == 0 && roadwork.getLatitude() == 0) {
            logger.warn("{} is invalid because it has no location", roadwork);
            return false;
        }
        return true;
    }

    private DateRange getDateRange(Object node) throws ParseException {
        DateParser startDateParser = serviceDescriptor.getFrom();
        DateParser endDateParser = serviceDescriptor.getTo();
        DateResult start = startDateParser.parse(getPath(node, startDateParser.getPath()), serviceDescriptor.getLocale());
        DateResult end = startDateParser.parse(getPath(node, endDateParser.getPath()), serviceDescriptor.getLocale());
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (start.getParser().isResetHour()) {
            start.setDate(fixTime(calendar, start.getDate()));
        }
        if (end.getParser().isResetHour()) {
            end.setDate(fixTime(calendar, end.getDate()));
        }
        if (start.getParser().isAddYear()) {
            start.setDate(addYear(calendar, currentYear, start.getDate()));
        }
        if (end.getParser().isAddYear()) {
            end.setDate(addYear(calendar, currentYear, end.getDate()));
            if (start.getDate() > end.getDate()) {
                end.setDate(addYear(calendar, currentYear + 1, end.getDate()));
            }
        }
        return new DateRange(start.getDate(), end.getDate());
    }

    private static long addYear(Calendar calendar, int year, long date) {
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTimeInMillis();
    }

    private static long fixTime(Calendar calendar, long date) {
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private Roadwork buildRoadwork(Object node) {
        try {
            RoadworkBuilder roadworkBuilder = RoadworkBuilder.aRoadwork();
            roadworkBuilder.withId(getPath(node, serviceDescriptor.getId()));
            try {
                roadworkBuilder.withLatitude(getPathAsDouble(node, serviceDescriptor.getLatitude()));
            } catch (ParseException e) {
                logger.error("Unable to get latitude from {}", node);
            }
            try {
                roadworkBuilder.withLongitude(getPathAsDouble(node, serviceDescriptor.getLongitude()));
            } catch (ParseException e) {
                logger.error("Unable to get longitude from {}", node);
            }
            if (serviceDescriptor.getRoad() != null) {
                roadworkBuilder.withRoad(getPath(node, serviceDescriptor.getRoad()));
            }
            if (serviceDescriptor.getDescription() != null) {
                roadworkBuilder.withDescription(getPath(node, serviceDescriptor.getDescription()));
            }
            if (serviceDescriptor.getLocationDetails() != null) {
                roadworkBuilder.withLocationDetails(getPath(node, serviceDescriptor.getLocationDetails()));
            }
            DateRange dateRange = getDateRange(node);
            roadworkBuilder.withStart(dateRange.getFrom());
            roadworkBuilder.withEnd(dateRange.getTo());
            if (serviceDescriptor.getImpactCirculationDetail() != null) {
                roadworkBuilder.withImpactCirculationDetail(getPath(node, serviceDescriptor.getImpactCirculationDetail()));
            }
            if (serviceDescriptor.getLocationDetails() != null) {
                roadworkBuilder.withLocationDetails(getPath(node, serviceDescriptor.getLocationDetails()));
            }
            if (serviceDescriptor.getUrl() != null) {
                roadworkBuilder.withUrl(getPath(node, serviceDescriptor.getUrl()));
            }
            return roadworkBuilder.build();
        } catch (ParseException e) {
            logger.error("Unable to parse " + node, e);
            return null;
        }
    }

    private static String getPath(Object node, String serviceDescriptor) {
        try {
            return JsonPath.read(node, serviceDescriptor);
        } catch (Exception e) {
            return null;
        }
    }

    private static double getPathAsDouble(Object node, String serviceDescriptor) throws ParseException {
        try {
            String read = JsonPath.read(node, serviceDescriptor);
            if (read != null) {
                return Double.parseDouble(read);
            }
        } catch (Exception e) {
            logger.error("Error parsing double");
        }
        throw new ParseException("Unable to parse", 0);
    }

    @Override
    public LatLng getCenter() {
        return serviceDescriptor.getCenter();
    }
}
