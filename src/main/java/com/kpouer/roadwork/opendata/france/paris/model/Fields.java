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
package com.kpouer.roadwork.opendata.france.paris.model;

/**
 * @author Matthieu Casanova
 */
public class Fields {
    private String date_debut;
    private String date_fin;
    private Perturbation niveau_perturbation;
    private String voie;
    private Statut statut;
    private String identifiant;
    private String impact_circulation_detail;
    private String impact_circulation;
    private String precision_localisation;
    private String description;
    private double[] geo_point_2d;

    public String getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(String date_debut) {
        this.date_debut = date_debut;
    }

    public String getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(String date_fin) {
        this.date_fin = date_fin;
    }

    public Perturbation getNiveau_perturbation() {
        return niveau_perturbation;
    }

    public void setNiveau_perturbation(Perturbation niveau_perturbation) {
        this.niveau_perturbation = niveau_perturbation;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getImpact_circulation_detail() {
        return impact_circulation_detail;
    }

    public void setImpact_circulation_detail(String impact_circulation_detail) {
        this.impact_circulation_detail = impact_circulation_detail;
    }

    public String getImpact_circulation() {
        return impact_circulation;
    }

    public void setImpact_circulation(String impact_circulation) {
        this.impact_circulation = impact_circulation;
    }

    public String getPrecision_localisation() {
        return precision_localisation;
    }

    public void setPrecision_localisation(String precision_localisation) {
        this.precision_localisation = precision_localisation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getGeo_point_2d() {
        return geo_point_2d;
    }

    public void setGeo_point_2d(double[] geo_point_2d) {
        this.geo_point_2d = geo_point_2d;
    }
}
