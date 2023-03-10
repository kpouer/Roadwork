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
package com.kpouer.roadwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

@Slf4j
@Service
public class ResourceService {
    public static final String OPENDATA_JSON = "opendata/json/";
    public static final String THIRDPARTY = "thirdparty";

    List<String> listFilesFromClasspath(String path) throws URISyntaxException, IOException {
        logger.debug("listFiles {}", path);
        var dirURL = getClass().getClassLoader().getResource(path);

        if (dirURL == null) {
            return Collections.emptyList();
        }

        if (dirURL.getProtocol().equals("file")) {
            return List.of(new File(dirURL.toURI()).list());
        }

        if (dirURL.getProtocol().equals("jar")) {
            var jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            try (var jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));Stream<JarEntry> stream = jar.stream()) {
                return stream
                        .map(ZipEntry::getName)
                        .filter(entryName -> entryName.startsWith(path))
                        .filter(entryName -> entryName.lastIndexOf('/') <= path.length())
                        .toList();
            }
        }

        logger.error("Unable to list files under path {}", path);
        return Collections.emptyList();
    }

    /**
     * Will return the URL of a resource
     *
     * @param filename the filename
     * @return the url of a resource
     */
    public Optional<URL> getResource(String filename) {
        logger.info("getResource {}", filename);
        var thirdParty = getFile(THIRDPARTY, filename);
        if (thirdParty.isPresent()) {
            return thirdParty;
        }
        var file = getFile(OPENDATA_JSON, filename);
        if (file.isPresent()) {
            return file;
        }

        logger.warn("Resource not found");

        return Optional.empty();
    }

    private Optional<URL> getFile(String parentPath, String filename) {
        try {
            var path = Path.of(parentPath, filename);
            if (Files.exists(path)) {
                logger.info("Found resource with url");
                var url = path.toUri().toURL();
                return Optional.of(url);
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
