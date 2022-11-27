package com.example.nearblood;

import android.location.Location;

import java.util.Comparator;

public class sortByLocation implements Comparator<Details> {
    Location location1,location2,currentLocation;

    public sortByLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public int compare(Details o1, Details o2) {
        location1 = new Location("");
        location2 = new Location("");
        location1.setLatitude(o1.getLatitude());
        location1.setLongitude(o1.getLongitude());

        location2.setLatitude(o2.getLatitude());
        location2.setLongitude(o2.getLongitude());
        return (int) (location1.distanceTo(currentLocation)-location2.distanceTo(currentLocation));
    }
}
