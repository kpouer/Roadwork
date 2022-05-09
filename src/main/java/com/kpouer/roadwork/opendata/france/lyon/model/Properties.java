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
package com.kpouer.roadwork.opendata.france.lyon.model;

/**
 * @author Matthieu Casanova
 */
public class Properties {
    private String nom;
    private String nomchantier;
    private String precisionlocalisation;
    private String debutchantier;
    private String finchantier;
    private String typeperturbation;
    private int identifiant;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNomchantier() {
        return nomchantier;
    }

    public void setNomchantier(String nomchantier) {
        this.nomchantier = nomchantier;
    }

    public String getPrecisionlocalisation() {
        return precisionlocalisation;
    }

    public void setPrecisionlocalisation(String precisionlocalisation) {
        this.precisionlocalisation = precisionlocalisation;
    }

    public String getDebutchantier() {
        return debutchantier;
    }

    public void setDebutchantier(String debutchantier) {
        this.debutchantier = debutchantier;
    }

    public String getFinchantier() {
        return finchantier;
    }

    public void setFinchantier(String finchantier) {
        this.finchantier = finchantier;
    }

    public String getTypeperturbation() {
        return typeperturbation;
    }

    public void setTypeperturbation(String typeperturbation) {
        this.typeperturbation = typeperturbation;
    }

    public int getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(int identifiant) {
        this.identifiant = identifiant;
    }
}
