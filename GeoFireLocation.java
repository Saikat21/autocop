package com.example.mahfuj.autocop;

import java.util.List;

public class GeoFireLocation {

    String g;
    List<Double> l;

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public List<Double> getL() {
        return l;
    }

    public void setL(List<Double> l) {
        this.l = l;
    }

    public GeoFireLocation(String g, List<Double> l) {

        this.g = g;
        this.l = l;
    }

    public GeoFireLocation() {

    }
}
