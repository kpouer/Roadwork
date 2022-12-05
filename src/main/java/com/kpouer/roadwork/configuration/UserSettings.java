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

/**
 * @author Matthieu Casanova
 */
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
    private String synchronizationPassword;
    private boolean hideExpired;

    public String getOpendataService() {
        return opendataService;
    }

    public void setOpendataService(String opendataService) {
        this.opendataService = opendataService;
    }

    public void setSynchronizationUrl(String synchronizationUrl) {
        this.synchronizationUrl = synchronizationUrl;
    }

    public String getSynchronizationUrl() {
        return synchronizationUrl;
    }

    public void setSynchronizationTeam(String synchronizationTeam) {
        this.synchronizationTeam = synchronizationTeam;
    }

    public String getSynchronizationTeam() {
        return synchronizationTeam;
    }

    public int getFrameX() {
        return frameX;
    }

    public void setFrameX(int frameX) {
        this.frameX = frameX;
    }

    public int getFrameY() {
        return frameY;
    }

    public void setFrameY(int frameY) {
        this.frameY = frameY;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public void setSynchronizationEnabled(boolean synchronizationEnabled) {
        this.synchronizationEnabled = synchronizationEnabled;
    }

    public boolean isSynchronizationEnabled() {
        return synchronizationEnabled;
    }

    public void setSynchronizationLogin(String synchronizationLogin) {
        this.synchronizationLogin = synchronizationLogin;
    }

    public String getSynchronizationLogin() {
        return synchronizationLogin;
    }

    public void setSynchronizationPassword(String synchronizationPassword) {
        this.synchronizationPassword = synchronizationPassword;
    }

    public String getSynchronizationPassword() {
        return synchronizationPassword;
    }

    public void setHideExpired(boolean hideExpired) {
        this.hideExpired = hideExpired;
    }

    public boolean isHideExpired() {
        return hideExpired;
    }
}
