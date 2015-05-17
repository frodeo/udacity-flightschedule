package net.oldervoll.flightschedule.model;

import com.google.common.base.Optional;

import java.util.List;

public class GeocodeResponse {

    public Optional<Double> getLat() {
        if (results != null && results.size() > 0) {
            Result result = results.get(0);
            if (result != null && result.geometry != null && result.geometry.location != null) {
                return Optional.of(result.geometry.location.lat);
            }
        }
        return Optional.absent();
    }

    public Optional<Double> getLng() {
        if (results != null && results.size() > 0) {
            Result result = results.get(0);
            if (result != null && result.geometry != null && result.geometry.location != null) {
                return Optional.of(result.geometry.location.lng);
            }
        }
        return Optional.absent();
    }

    private List<Result> results;
    private class Result {
        private Geometry geometry;
        private class Geometry {
            private Location location;
            private class Location {
                private double lat;
                private double lng;
            }
        }
    }
}
