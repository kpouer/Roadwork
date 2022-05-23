package com.kpouer.roadwork.opendata.france.rennes.model;

public class Properties{
    private String name;
    private String date_deb;
    private String commune;
    private String quartier;
    private String localisation;
    private String libelle;
    private String date_fin;
    private int id;
    private String type;
    private String niv_perturbation;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setDate_deb(String date_deb){
        this.date_deb = date_deb;
    }

    public String getDate_deb(){
        return date_deb;
    }

    public void setCommune(String commune){
        this.commune = commune;
    }

    public String getCommune(){
        return commune;
    }

    public void setQuartier(String quartier){
        this.quartier = quartier;
    }

    public String getQuartier(){
        return quartier;
    }

    public void setLocalisation(String localisation){
        this.localisation = localisation;
    }

    public String getLocalisation(){
        return localisation;
    }

    public void setLibelle(String libelle){
        this.libelle = libelle;
    }

    public String getLibelle(){
        return libelle;
    }

    public void setDate_fin(String date_fin){
        this.date_fin = date_fin;
    }

    public String getDate_fin(){
        return date_fin;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setNiv_perturbation(String niv_perturbation){
        this.niv_perturbation = niv_perturbation;
    }

    public String getNiv_perturbation(){
        return niv_perturbation;
    }
}
