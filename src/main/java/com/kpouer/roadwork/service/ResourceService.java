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
package com.kpouer.roadwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

@Slf4j
@Service
public class ResourceService {
    List<String> listFilesFromClasspath(String path) throws URISyntaxException, IOException {
        log.debug("listFiles {}", path);
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

        log.error("Unable to list files under path {}", path);
        return Collections.emptyList();
    }
}
