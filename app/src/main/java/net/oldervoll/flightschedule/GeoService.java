package net.oldervoll.flightschedule;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.model.GeocodeResponse;
import net.oldervoll.flightschedule.remote.GoogleGeocodingService;
import net.oldervoll.flightschedule.remote.GoogleGeocodingServiceImpl;

public class GeoService extends IntentService {

    private static final String LOG_TAG = GeoService.class.getSimpleName();
    private GoogleGeocodingService geocodingService = new GoogleGeocodingServiceImpl();

    public GeoService() {
        super(GeoService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String fromAirport = intent.getStringExtra(Constants.EXTRA_FROM_AIRPORT);
        String toAirport = intent.getStringExtra(Constants.EXTRA_TO_AIRPORT);
        String apiKey = getResources().getString(R.string.geocoding_api_key);
        GeocodeResponse fromResponse = geocodingService.getGeocodeAddress(fromAirport, apiKey);
        GeocodeResponse toResponse = geocodingService.getGeocodeAddress(toAirport, apiKey);
        Optional<Double> optionalDegrees = getDegrees(fromResponse, toResponse);
        double degrees = optionalDegrees.or(-1.0);
        Log.d(LOG_TAG, "Direction between " + fromAirport + " and " + toAirport + " is " + degrees);
        Intent localIntent = new Intent(Constants.BROADCAST_GEO_ACTION)
                .putExtra(Constants.EXTRA_DEGREES_RESULT, degrees);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    private Optional<Double> getDegrees(GeocodeResponse from, GeocodeResponse to) {

        if (from != null && from.getLat() != null && from.getLng() != null &&
                from.getLat().isPresent() && from.getLng().isPresent() &&
                to != null && to.getLat() != null && to.getLng() != null &&
                to.getLat().isPresent() && to.getLng().isPresent()) {

            double deltaLng = deg2rad(to.getLng().get() - from.getLng().get());
            double fromLat = deg2rad(from.getLat().get());
            double toLat = deg2rad(to.getLat().get());

            double y = Math.sin(deltaLng) * Math.cos(toLat);
            double x = Math.cos(fromLat) * Math.sin(toLat) -
                    Math.sin(fromLat) * Math.cos(toLat) * Math.cos(deltaLng);
            double degrees = rad2deg(Math.atan2(y, x));

            if (degrees < 0) {
                degrees = 360 - Math.abs(degrees);
            }

            return Optional.of(degrees);

        } else {
            return Optional.absent();
        }

    }

    private double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    private double rad2deg(double rad) {
        return rad * 180.0 / Math.PI;
    }
}
