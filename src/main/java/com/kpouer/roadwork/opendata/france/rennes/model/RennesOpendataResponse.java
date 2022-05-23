package com.kpouer.roadwork.opendata.france.rennes.model;

import com.kpouer.roadwork.opendata.OpendataResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class RennesOpendataResponse implements OpendataResponse<FeaturesItem> {
    private List<FeaturesItem> features;

    public void setFeatures(List<FeaturesItem> features){
        this.features = features;
    }

    public List<FeaturesItem> getFeatures(){
        return features;
    }

    @NotNull
    @Override
    public Iterator<FeaturesItem> iterator() {
        return features.iterator();
    }
}