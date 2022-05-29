package com.kpouer.roadwork.opendata.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kpouer.mapview.LatLng;
import com.kpouer.roadwork.model.DateRange;
import com.kpouer.roadwork.model.Roadwork;
import com.kpouer.roadwork.model.RoadworkBuilder;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.OpendataService;
import com.kpouer.roadwork.opendata.json.model.DateParser;
import com.kpouer.roadwork.opendata.json.model.DateResult;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
        ObjectNode json = restTemplate.getForObject(serviceDescriptor.getUrl(), ObjectNode.class);
        Optional<ArrayNode> roadworkArray = getRoadworkArray(json);
        return roadworkArray.map(this::getRoadworkData);
    }

    private Optional<ArrayNode> getRoadworkArray(ObjectNode json) {
        String roadworkArrayPath = serviceDescriptor.getRoadworkArray();
        return getNode(json, roadworkArrayPath);
    }

    private <T extends JsonNode> Optional<T> getNode(JsonNode json, String path) {
        String[] tokens = path.split("\\.");
        JsonNode objectNode = json;
        for (String token : tokens) {
            objectNode = objectNode.get(token);
            if (objectNode == null) {
                return Optional.empty();
            }
        }
        return Optional.of((T) objectNode);
    }

    @Nullable
    private String getNodeAsString(JsonNode jsonNode, String path) {
        if (path == null) {
            return null;
        }
        Optional<JsonNode> node = getNode(jsonNode, path);
        return node.map(JsonNode::asText).orElse(null);
    }

    private double getNodeAsDouble(JsonNode jsonNode, String path) {
        Optional<JsonNode> node = getNode(jsonNode, path);
        return node.map(JsonNode::asDouble).orElse(0.0);
    }

    private RoadworkData getRoadworkData(ArrayNode array) {
        List<Roadwork> roadworks = new ArrayList<>(array.size());
        for (JsonNode jsonNode : array) {
            Roadwork roadwork = buildRoadwork(jsonNode);
            if (roadwork == null ||
                    roadwork.getId() == null ||
                    roadwork.getLatitude() == 0 || roadwork.getLongitude() == 0) {
                logger.warn("Invalid roadwork {}", array);
            } else {
                roadworks.add(roadwork);
            }
        }
        return new RoadworkData(serviceDescriptor.getName(), roadworks);
    }

    private DateRange getDateRange(JsonNode jsonNode) throws ParseException {
        DateParser startDateParser = serviceDescriptor.getFrom();
        DateParser endDateParser = serviceDescriptor.getTo();
        DateResult start = startDateParser.parse(getNodeAsString(jsonNode, startDateParser.getPath()), serviceDescriptor.getLocale());
        DateResult end = startDateParser.parse(getNodeAsString(jsonNode, endDateParser.getPath()), serviceDescriptor.getLocale());
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

    private Roadwork buildRoadwork(JsonNode jsonNode) {
        try {
            RoadworkBuilder roadworkBuilder = RoadworkBuilder.aRoadwork();
            roadworkBuilder.withId(getNodeAsString(jsonNode, serviceDescriptor.getId()));
            roadworkBuilder.withLatitude(getNodeAsDouble(jsonNode, serviceDescriptor.getLatitude()));
            roadworkBuilder.withLongitude(getNodeAsDouble(jsonNode, serviceDescriptor.getLongitude()));
            roadworkBuilder.withRoad(getNodeAsString(jsonNode, serviceDescriptor.getRoad()));
            roadworkBuilder.withDescription(getNodeAsString(jsonNode, serviceDescriptor.getDescription()));
            roadworkBuilder.withLocationDetails(getNodeAsString(jsonNode, serviceDescriptor.getLocationDetails()));
            DateRange dateRange = getDateRange(jsonNode);
            roadworkBuilder.withStart(dateRange.getFrom());
            roadworkBuilder.withEnd(dateRange.getTo());
            roadworkBuilder.withImpactCirculationDetail(getNodeAsString(jsonNode, serviceDescriptor.getImpactCirculationDetail()));
            roadworkBuilder.withLocationDetails(getNodeAsString(jsonNode, serviceDescriptor.getLocationDetails()));
            roadworkBuilder.withUrl(getNodeAsString(jsonNode, serviceDescriptor.getUrl()));
            return roadworkBuilder.build();
        } catch (ParseException e) {
            logger.error("Unable to parse " + jsonNode, e);
            return null;
        }
    }

    @Override
    public LatLng getCenter() {
        return serviceDescriptor.getCenter();
    }
}
