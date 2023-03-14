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
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import com.kpouer.themis.Component;

import static javax.swing.JComponent.getDefaultLocale;

/**
 * @author Matthieu Casanova
 */
@Component
@Slf4j
public class LocalizationService {
    private final MessageSource resourceBundle;

    public LocalizationService() {
        var resourceBundle = new ResourceBundleMessageSource();
        resourceBundle.setBasenames("messages");
        resourceBundle.setUseCodeAsDefaultMessage(true);
        this.resourceBundle = resourceBundle;
    }

    public String getMessage(String code, @Nullable Object[] args) {
        try {
            return resourceBundle.getMessage(code, args, getDefaultLocale());
        } catch (NoSuchMessageException e) {
            logger.error("Unable to get message {}", code);
            return code;
        }
    }

    public String getMessage(String code) {
        try {
            return resourceBundle.getMessage(code, null, getDefaultLocale());
        } catch (NoSuchMessageException e) {
            logger.error("Unable to get message {}", code);
            return code;
        }
    }
}
