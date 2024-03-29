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
import jakarta.annotation.Nullable;
import com.kpouer.themis.annotation.Component;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Matthieu Casanova
 */
@Component
@Slf4j
public class LocalizationService {
    private final ResourceBundle resourceBundle;

    public LocalizationService() throws IOException {
        resourceBundle = new PropertyResourceBundle(getClass().getResourceAsStream("/messages.properties"));
    }

    public String getMessage(String key, @Nullable Object... args) {
        try {
            var format = resourceBundle.getString(key);
            if (args != null && args.length > 0)
                return String.format(format, args);
            return format;
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
