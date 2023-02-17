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
package com.kpouer.roadwork.opendata.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kpouer.roadwork.model.DateRange;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.model.sync.SyncData;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.model.DateParser;
import com.kpouer.roadwork.opendata.json.model.DateResult;
import com.kpouer.roadwork.opendata.json.model.Metadata;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import com.kpouer.roadwork.service.HttpService;
import com.kpouer.wkt.shape.Polygon;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DefaultJsonService implements OpendataService {
    private final String serviceName;
    private final HttpService httpService;
    private final ServiceDescriptor serviceDescriptor;

    public DefaultJsonService(String serviceName, HttpService httpService, ServiceDescriptor serviceDescriptor) {
        this.serviceName = serviceName;
        this.httpService = httpService;
        this.serviceDescriptor = serviceDescriptor;
    }

    @Override
    public Metadata getMetadata() {
        return serviceDescriptor.getMetadata();
    }

    @Override
    public Optional<RoadworkData> getData() throws RestClientException {
        var url = buildUrl();
        log.info("getData {}", url);
        var json = httpService.getUrl(url);
        var document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        List<?> roadworkArray = JsonPath.read(document, serviceDescriptor.getRoadworkArray());
        var roadworks = roadworkArray
                .parallelStream()
                .map(node -> buildRoadwork(serviceDescriptor, node))
                .filter(DefaultJsonService::isValid)
                .toList();
        return Optional.of(new RoadworkData(serviceName, roadworks));
    }

    private String buildUrl() {
        var metadata = serviceDescriptor.getMetadata();
        var urlParams = metadata.getUrlParams();
        var url = metadata.getUrl();
        if (urlParams == null) {
            return url;
        }
        var queryString = urlParams
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + '=' + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        url += url.indexOf('?') != -1 ? '&' + queryString : '?' + queryString;
        return url;
    }

    public static boolean isValid(Roadwork roadwork) {
        if (roadwork.getLongitude() == 0 && roadwork.getLatitude() == 0) {
            log.warn("{} is invalid because it has no location", roadwork);
            return false;
        }
//        if (roadwork.getStart() == 0) {
//            log.warn("{} is invalid because it's start date is 0", roadwork);
//            return false;
//        }
//        if (roadwork.getEnd() == 0) {
//            log.warn("{} is invalid because it's end date is 0", roadwork);
//            return false;
//        }
        return true;
    }

    private static Optional<DateResult> parseDate(Object node, DateParser dateParser, ServiceDescriptor serviceDescriptor) throws ParseException {
        if (dateParser == null) {
            log.debug("Cannot parse date as dateParse is null");
            return Optional.empty();
        }
        var calendar = Calendar.getInstance();
        var currentYear = calendar.get(Calendar.YEAR);
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
        var currentYear = calendar.get(Calendar.YEAR);
        long startTime;
        try {
            var start = parseDate(node, serviceDescriptor.getFrom(), serviceDescriptor);
            startTime = start.map(DateResult::getDate).orElse(0L);
        } catch (Exception e) {
            log.error("Error parsing start date", e);
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
            log.error("Error parsing end date", e);
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
        var roadworkBuilder = Roadwork.builder();
        roadworkBuilder.syncData(new SyncData());
        roadworkBuilder.id(getPath(node, serviceDescriptor.getId()));
        try {
            var latitudePath = serviceDescriptor.getLatitude();
            if (latitudePath == null || latitudePath.isEmpty()) {
                log.warn("Unable to get latitude as it's path is empty");
            } else {
                roadworkBuilder.latitude(getPathAsDouble(node, latitudePath));
            }
        } catch (Exception e) {
            log.warn("Unable to get latitude from {}, {}", node, e.getMessage());
        }
        try {
            var longitudePath = serviceDescriptor.getLongitude();
            if (longitudePath == null || longitudePath.isEmpty()) {
                log.warn("Unable to get longitude as it's path is empty");
            } else {
                roadworkBuilder.longitude(getPathAsDouble(node, longitudePath));
            }
        } catch (Exception e) {
            log.warn("Unable to get longitude from {}, {}", node, e.getMessage());
        }
        var polygonPath = serviceDescriptor.getPolygon();
        if (polygonPath != null && !polygonPath.isEmpty()) {
            roadworkBuilder.polygons(getPathAsPolygons(node, polygonPath));
        }
        if (serviceDescriptor.getRoad() != null) {
            roadworkBuilder.road(getPath(node, serviceDescriptor.getRoad()));
        }
        if (serviceDescriptor.getDescription() != null) {
            roadworkBuilder.description(getPath(node, serviceDescriptor.getDescription()));
        }
        if (serviceDescriptor.getLocationDetails() != null) {
            roadworkBuilder.locationDetails(getPath(node, serviceDescriptor.getLocationDetails()));
        }
        try {
            var dateRange = getDateRange(node, serviceDescriptor);
            roadworkBuilder.start(dateRange.getFrom());
            roadworkBuilder.end(dateRange.getTo());
        } catch (ParseException e) {
            log.error("Unable to parse date", e);
        }
        if (serviceDescriptor.getImpactCirculationDetail() != null) {
            roadworkBuilder.impactCirculationDetail(getPath(node, serviceDescriptor.getImpactCirculationDetail()));
        }
        if (serviceDescriptor.getLocationDetails() != null) {
            roadworkBuilder.locationDetails(getPath(node, serviceDescriptor.getLocationDetails()));
        }
        if (serviceDescriptor.getUrl() != null) {
            roadworkBuilder.url(getPath(node, serviceDescriptor.getUrl()));
        }
        return roadworkBuilder.build();
    }

    private static Polygon[] getPathAsPolygons(Object node, String path) {
        try {
            var value = (JSONArray) JsonPath.read(node, path);
            if (value != null) {
                if (isMultiPolygon(value)) {
                    var polygons = new Polygon[value.size()];
                    for (var i = 0; i < value.size(); i++) {
                        var polygonArray = (JSONArray) value.get(i);
                        polygons[i] = getPolygon((JSONArray) polygonArray.get(0));
                    }
                    return polygons;
                } else {
                    var polygonArray = (JSONArray) value.get(0);
                    var polygon = getPolygon(polygonArray);
                    return new Polygon[] {polygon};
                }
            }
        } catch (Exception exception) {
            log.error("Error parsing polygon", exception);
        }
        return null;
    }

    private static boolean isMultiPolygon(JSONArray value) {
        var firstLevel = (JSONArray) value.get(0);
        var secondLevel = (JSONArray) firstLevel.get(0);
        return secondLevel.get(0) instanceof JSONArray;
    }

    @NotNull
    private static Polygon getPolygon(JSONArray polygonArray) {
        var xpoints = new double[polygonArray.size()];
        var ypoints = new double[polygonArray.size()];
        for (var i = 0; i < polygonArray.size(); i++) {
            var point = (JSONArray) polygonArray.get(i);
            xpoints[i] = (double) point.get(0);
            ypoints[i] = (double) point.get(1);
        }
        var polygon = new Polygon(xpoints, ypoints);
        return polygon;
    }

    private static String getPath(Object node, String path) {
        try {
            var value = JsonPath.read(node, path);
            if (value != null) {
                return String.valueOf(value);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static double getPathAsDouble(@NotNull Object node, @NotNull String path) throws ParseException {
        try {
            Objects.requireNonNull(path);
            var value = JsonPath.read(node, path);

            if (value instanceof Double) {
                return (double) value;
            }
            return Double.parseDouble(String.valueOf(value).replace(',', '.'));
        } catch (Exception e) {
            log.error("Error parsing double", e);
        }
        throw new ParseException("Unable to parse", 0);
    }
}
