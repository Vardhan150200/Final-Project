package com.example.nearblood;

import com.google.android.gms.maps.model.LatLng;

public class AccesVar {
    public AccesVar(Double currentUserLat, Double currentUserLon, LatLng origion, LatLng dest) {
        this.currentUserLat = currentUserLat;
        this.currentUserLon = currentUserLon;
        this.origion = origion;
        this.dest = dest;
    }

    private Double currentUserLat,currentUserLon;

    public Double getCurrentUserLat() {
        return currentUserLat;
    }

    public void setCurrentUserLat(Double currentUserLat) {
        this.currentUserLat = currentUserLat;
    }

    public Double getCurrentUserLon() {
        return currentUserLon;
    }

    public void setCurrentUserLon(Double currentUserLon) {
        this.currentUserLon = currentUserLon;
    }

    public LatLng getOrigion() {
        return origion;
    }

    public void setOrigion(LatLng origion) {
        this.origion = origion;
    }

    public LatLng getDest() {
        return dest;
    }

    public void setDest(LatLng dest) {
        this.dest = dest;
    }

    public LatLng origion,dest;
}
