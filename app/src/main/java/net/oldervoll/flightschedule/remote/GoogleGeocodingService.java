package net.oldervoll.flightschedule.remote;

import net.oldervoll.flightschedule.model.GeocodeResponse;

import hugo.weaving.DebugLog;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleGeocodingService {

    String PATH_GEOCODE = "/geocode/json";
    String QUERY_ADDRESS = "address";
    String QUERY_KEY = "key";

    @GET(PATH_GEOCODE)
    @DebugLog
    GeocodeResponse getGeocodeAddress(
            @Query(QUERY_ADDRESS) String address, @Query(QUERY_KEY) String apiKey);

    @GET(PATH_GEOCODE)
    @DebugLog
    GeocodeResponse getGeocodeAddress(
            @Query(QUERY_ADDRESS) String address);
}
