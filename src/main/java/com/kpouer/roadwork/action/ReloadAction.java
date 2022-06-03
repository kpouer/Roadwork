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
package com.kpouer.roadwork.action;

import com.kpouer.roadwork.configuration.Config;
import com.kpouer.roadwork.event.OpendataServiceUpdated;
import com.kpouer.roadwork.service.OpendataServiceManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 */
@Component
public class ReloadAction extends AbstractAction {

    private final Config config;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OpendataServiceManager opendataServiceManager;

    public ReloadAction(Config config,
                        ApplicationEventPublisher applicationEventPublisher,
                        OpendataServiceManager opendataServiceManager) {
        super("Reload");
        this.config = config;
        this.applicationEventPublisher = applicationEventPublisher;
        this.opendataServiceManager = opendataServiceManager;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent e) {
        opendataServiceManager.deleteCache();
        OpendataServiceUpdated event = new OpendataServiceUpdated(this, config.getOpendataService(), config.getOpendataService());
        applicationEventPublisher.publishEvent(event);
    }
}
