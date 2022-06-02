package com.kpouer.roadwork.opendata.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

class DefaultJsonServiceTest {
    @Test
    void test() throws IOException {

        try (InputStream stream = DefaultJsonServiceTest.class.getResourceAsStream("/sample/france/montpellier.json")) {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(stream, "UTF-8");
            List<Object> read = JsonPath.read(document, "$.features");
            System.out.println();
        }
    }
}