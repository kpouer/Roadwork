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
package com.kpouer.roadwork.opendata.json.model;

public class MetadataBuilder {
    private String country;
    private double[] center;
    private String source_url;
    private String url;
    private String name;
    private String producer;
    private String licenceName;
    private String licenceUrl;

    private MetadataBuilder() {
    }

    public static MetadataBuilder aMetadata() {
        return new MetadataBuilder();
    }

    public MetadataBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public MetadataBuilder withCenter(double latitude, double longitude) {
        this.center = new double[] {latitude, longitude};
        return this;
    }

    public MetadataBuilder withSource_url(String source_url) {
        this.source_url = source_url;
        return this;
    }

    public MetadataBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public MetadataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MetadataBuilder withProducer(String producer) {
        this.producer = producer;
        return this;
    }

    public MetadataBuilder withLicenceName(String licenceName) {
        this.licenceName = licenceName;
        return this;
    }

    public MetadataBuilder withLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
        return this;
    }

    public Metadata build() {
        Metadata metadata = new Metadata();
        metadata.setCountry(country);
        metadata.setCenter(center);
        metadata.setSource_url(source_url);
        metadata.setUrl(url);
        metadata.setName(name);
        metadata.setProducer(producer);
        metadata.setLicenceName(licenceName);
        metadata.setLicenceUrl(licenceUrl);
        return metadata;
    }
}
