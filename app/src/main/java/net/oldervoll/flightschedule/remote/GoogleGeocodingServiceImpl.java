package net.oldervoll.flightschedule.remote;

import android.util.Log;

import net.oldervoll.flightschedule.model.GeocodeResponse;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.Query;

public class GoogleGeocodingServiceImpl implements GoogleGeocodingService {

    public final String LOG_TAG = GoogleGeocodingServiceImpl.class.getSimpleName();

    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api";
    private GoogleGeocodingService service;

    public GoogleGeocodingServiceImpl() {
        service = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build()
                .create(GoogleGeocodingService.class);
    }

    @Override
    public GeocodeResponse getGeocodeAddress(
            @Query(QUERY_ADDRESS) String address, @Query(QUERY_KEY) String apiKey) {
        try {
            return service.getGeocodeAddress(address, apiKey);
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to geokode address (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }

    @Override
    public GeocodeResponse getGeocodeAddress(
            @Query(QUERY_ADDRESS) String address) {
        try {
            return service.getGeocodeAddress(address);
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to geokode address (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }
}
