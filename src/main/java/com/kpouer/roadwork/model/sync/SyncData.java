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
package com.kpouer.roadwork.model.sync;

/**
 * @author Matthieu Casanova
 */
public class SyncData {
    private long localUpdateTime;
    /**
     * Timestamp of the last server change
     */
    private long serverUpdateTime;
    private Status status = Status.New;
    private boolean dirty;

    public long getLocalUpdateTime() {
        return localUpdateTime;
    }

    public void setLocalUpdateTime(long localUpdateTime) {
        this.localUpdateTime = localUpdateTime;
    }

    public long getServerUpdateTime() {
        return serverUpdateTime;
    }

    public void setServerUpdateTime(long serverUpdateTime) {
        this.serverUpdateTime = serverUpdateTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void updateStatus(Status status) {
        this.status = status;
        localUpdateTime = System.currentTimeMillis();
        dirty = true;
    }

    public void copy(SyncData syncData) {
        status = syncData.status;
        serverUpdateTime = syncData.serverUpdateTime;
        localUpdateTime = syncData.localUpdateTime;
        dirty = syncData.dirty;
    }

    @Override
    public String toString() {
        return "SyncData{localUpdateTime=" + localUpdateTime + ", serverUpdateTime=" + serverUpdateTime + ", status=" + status + ", dirty=" + dirty + '}';
    }
}
