package com.kpouer.roadwork.service;

import com.kpouer.roadwork.opendata.json.DefaultJsonService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class HttpService {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);

    public String getUrl(String url) throws RestClientException {
        logger.info("getUrl {}", url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpUriRequest getMethod = new HttpGet(url);

            HttpResponse response = httpClient.execute(getMethod);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RestClientException("Error retrieving " + url, e);
        }
    }
}
