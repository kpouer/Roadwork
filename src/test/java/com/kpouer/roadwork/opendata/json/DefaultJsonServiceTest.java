package com.kpouer.roadwork.opendata.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kpouer.roadwork.model.RoadworkData;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DefaultJsonServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        Mockito.reset(restTemplate);
    }

    @Test
    void test() throws IOException {

        try (InputStream stream = DefaultJsonServiceTest.class.getResourceAsStream("/sample/france/montpellier.json")) {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(stream, "UTF-8");
            List<Object> read = JsonPath.read(document, "$.features");
            System.out.println();
        }
    }

    @Test
    void testAvignon() throws IOException, URISyntaxException {
        DefaultJsonService serviceDescriptor = getServiceDescriptor("opendata/json/France-Avignon.json", "/sample/france/avignon.json");
        Optional<RoadworkData> data = serviceDescriptor.getData();
    }

    private DefaultJsonService getServiceDescriptor(String pathname, String samplePath) throws IOException, URISyntaxException {
        String sample = Files.readString(Paths.get(getClass().getResource(samplePath).toURI()), StandardCharsets.UTF_8);
        Mockito.when(restTemplate.getForObject(ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(Class.class).capture())).thenReturn(sample);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ServiceDescriptor serviceDescriptor = objectMapper.readValue(new File(pathname), ServiceDescriptor.class);
        return new DefaultJsonService(restTemplate, serviceDescriptor);
    }
}