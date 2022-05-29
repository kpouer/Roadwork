package com.kpouer.roadwork.opendata.json.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ServiceDescriptorTest {
    @Test
    void deserialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ServiceDescriptor serviceDescriptor = objectMapper.readValue(new File("opendata/Issy-les-Moulineaux.json"), ServiceDescriptor.class);
        assertEquals("France", serviceDescriptor.getCountry());
    }
}