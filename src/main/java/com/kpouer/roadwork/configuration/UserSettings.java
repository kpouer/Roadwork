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
package com.kpouer.roadwork.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Matthieu Casanova
 */
@Getter
@Setter
public class UserSettings {
    private String opendataService = Config.DEFAULT_OPENDATA_SERVICE;
    private String synchronizationUrl;
    private String synchronizationTeam;
    private int frameX;
    private int frameY;
    private int frameWidth;
    private int frameHeight;
    private boolean synchronizationEnabled;
    private String synchronizationLogin;
    private char[] synchronizationPassword;
    private boolean hideExpired;
}
