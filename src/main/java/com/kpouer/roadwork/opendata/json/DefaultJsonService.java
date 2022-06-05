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
import com.kpouer.roadwork.model.DateRange;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.model.DateParser;
import com.kpouer.roadwork.opendata.json.model.DateResult;
import com.kpouer.roadwork.opendata.json.model.Metadata;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import org.jetbrains.annotations.NotNull;
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

    private final String serviceName;
    private final RestTemplate restTemplate;
    private final ServiceDescriptor serviceDescriptor;

    public DefaultJsonService(String serviceName, RestTemplate restTemplate, ServiceDescriptor serviceDescriptor) {
        this.serviceName = serviceName;
        this.restTemplate = restTemplate;
        this.serviceDescriptor = serviceDescriptor;
    }

    @Override
    public Metadata getMetadata() {
        return serviceDescriptor.getMetadata();
    }

    @Override
    public Optional<RoadworkData> getData() throws RestClientException {
        String json = restTemplate.getForObject(serviceDescriptor.getMetadata().getUrl(), String.class);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        List<?> roadworkArray = JsonPath.read(document, serviceDescriptor.getRoadworkArray());
        List<Roadwork> roadworks = roadworkArray
                .parallelStream()
                .map(node -> buildRoadwork(serviceDescriptor, node))
                .filter(DefaultJsonService::isValid)
                .toList();
        return Optional.of(new RoadworkData(serviceName, roadworks));
    }

    public static boolean isValid(Roadwork roadwork) {
        if (roadwork.getLongitude() == 0 && roadwork.getLatitude() == 0) {
            logger.warn("{} is invalid because it has no location", roadwork);
            return false;
        }
        if (roadwork.getStart() == 0) {
            logger.warn("{} is invalid because it's start date is 0", roadwork);
            return false;
        }
        if (roadwork.getEnd() == 0) {
            logger.warn("{} is invalid because it's end date is 0", roadwork);
            return false;
        }
        return true;
    }

    private static Optional<DateResult> parseDate(Object node, DateParser dateParser, ServiceDescriptor serviceDescriptor) throws ParseException {
        if (dateParser == null) {
            logger.debug("Cannot parse date as dateParse is null");
            return Optional.empty();
        }
        var calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        var value = getPath(node, dateParser.getPath());
        if (value == null) {
            return Optional.empty();
        }
        var result = dateParser.parse(value, serviceDescriptor.getMetadata().getLocale());
        if (result.getParser().isResetHour()) {
            result.setDate(fixTime(calendar, result.getDate()));
        }
        if (result.getParser().isAddYear()) {
            result.setDate(addYear(calendar, currentYear, result.getDate()));
        }
        return Optional.of(result);
    }

    private static DateRange getDateRange(Object node, ServiceDescriptor serviceDescriptor) throws ParseException {
        var calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        long startTime;
        try {
            Optional<DateResult> start = parseDate(node, serviceDescriptor.getFrom(), serviceDescriptor);
            startTime = start.map(DateResult::getDate).orElse(0L);
        } catch (Exception e) {
            logger.error("Error parsing start date", e);
            startTime = 0L;
        }
        long endDate;
        try {
            var endOptional = parseDate(node, serviceDescriptor.getTo(), serviceDescriptor);
            if (endOptional.isPresent()) {
                var end = endOptional.get();
                endDate = end.getDate();
                if (end.getParser().isAddYear()) {
                    endDate = addYear(calendar, currentYear, endDate);
                    if (startTime > endDate) {
                        endDate = addYear(calendar, currentYear + 1, endDate);
                    }
                }
            } else {
                endDate = 0L;
            }
        } catch (Exception e) {
            logger.error("Error parsing end date", e);
            endDate = 0L;
        }
        return new DateRange(startTime, endDate);
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

    @NotNull
    public static Roadwork buildRoadwork(ServiceDescriptor serviceDescriptor, @NotNull Object node) {
        RoadworkBuilder roadworkBuilder = RoadworkBuilder.aRoadwork();
        roadworkBuilder.withId(getPath(node, serviceDescriptor.getId()));
        try {
            String latitudePath = serviceDescriptor.getLatitude();
            if (latitudePath == null || latitudePath.isEmpty()) {
                logger.warn("Unable to get latitude as it's path is empty");
            } else {
                roadworkBuilder.withLatitude(getPathAsDouble(node, latitudePath));
            }
        } catch (Exception e) {
            logger.warn("Unable to get latitude from {}, {}", node, e.getMessage());
        }
        try {
            String longitudePath = serviceDescriptor.getLongitude();
            if (longitudePath == null || longitudePath.isEmpty()) {
                logger.warn("Unable to get longitude as it's path is empty");
            } else {
                roadworkBuilder.withLongitude(getPathAsDouble(node, longitudePath));
            }
        } catch (Exception e) {
            logger.warn("Unable to get longitude from {}, {}", node, e.getMessage());
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
        try {
            DateRange dateRange = getDateRange(node, serviceDescriptor);
            roadworkBuilder.withStart(dateRange.getFrom());
            roadworkBuilder.withEnd(dateRange.getTo());
        } catch (ParseException e) {
            logger.error("Unable to parse date", e);
        }
        if (serviceDescriptor.getImpactCirculationDetail() != null) {
            roadworkBuilder.withImpactCirculationDetail(getPath(node, serviceDescriptor.getImpactCirculationDetail()));
        }
        if (serviceDescriptor.getLocationDetails() != null) {
            roadworkBuilder.withLocationDetails(getPath(node, serviceDescriptor.getLocationDetails()));
        }
        if (serviceDescriptor.getMetadata().getUrl() != null) {
            roadworkBuilder.withUrl(getPath(node, serviceDescriptor.getMetadata().getUrl()));
        }
        return roadworkBuilder.build();
    }

    private static String getPath(Object node, String path) {
        try {
            Object value = JsonPath.read(node, path);
            if (value != null) {
                return String.valueOf(value);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static double getPathAsDouble(@NotNull Object node, @NotNull String path) throws ParseException {
        try {
            Objects.requireNonNull(path);
            Object value = JsonPath.read(node, path);

            if (value instanceof Double) {
                return (double) value;
            }
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            logger.error("Error parsing double", e);
        }
        throw new ParseException("Unable to parse", 0);
    }
}
