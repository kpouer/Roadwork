package com.kpouer.roadwork.opendata.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.kpouer.roadwork.opendata.json.model.ServiceDescriptor;
import com.kpouer.roadwork.service.HttpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DefaultJsonServiceTest {
    @Mock
    private HttpService httpService;

    @BeforeEach
    void setUp() {
        Mockito.reset(httpService);
    }

    @Test
    void test() throws IOException {
        try (var stream = DefaultJsonServiceTest.class.getResourceAsStream("/sample/france/montpellier.json")) {
            var document = Configuration.defaultConfiguration().jsonProvider().parse(stream, "UTF-8");
            List<Object> read = JsonPath.read(document, "$.features");
            System.out.println();
        }
    }

    @Test
    void testAvignon() throws IOException, URISyntaxException {
        var serviceDescriptor = getServiceDescriptor("opendata/json/France-Avignon.json", "/sample/france/avignon.json");
        var data = serviceDescriptor.getData();
        var roadworkData = data.get();
        var roadwork = roadworkData.getRoadworks().get("850041398");
        assertEquals("850041398", roadwork.getId());
    }

    private DefaultJsonService getServiceDescriptor(String pathname, String samplePath) throws IOException, URISyntaxException {
        Mockito.when(httpService.getUrl(ArgumentCaptor.forClass(String.class).capture())).thenReturn(getSample(samplePath));
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var file = new File(pathname);
        var serviceDescriptor = objectMapper.readValue(file, ServiceDescriptor.class);
        return new DefaultJsonService(pathname, httpService, serviceDescriptor);
    }

    private String getSample(String samplePath) throws URISyntaxException, IOException {
        var uri = getClass().getResource(samplePath).toURI();
        var path = Paths.get(uri);
        var sample = Files.readString(path, StandardCharsets.UTF_8);
        return sample;
    }
}