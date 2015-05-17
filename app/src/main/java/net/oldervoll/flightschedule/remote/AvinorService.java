package net.oldervoll.flightschedule.remote;

import net.oldervoll.flightschedule.model.AirlineNames;
import net.oldervoll.flightschedule.model.Airport;
import net.oldervoll.flightschedule.model.AirportNames;
import net.oldervoll.flightschedule.model.FlightStatuses;

import hugo.weaving.DebugLog;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AvinorService {

    String PATH_FEED = "/XmlFeed.asp";
    String PATH_FLIGHT_STATUSES = "/flightStatuses.asp";
    String PATH_AIRPORT_NAMES = "/airportNames.asp";
    String PATH_AIRLINE_NAMES = "/airlineNames.asp";
    String QUERY_AIRPORT = "airport";

    @GET(PATH_FEED)
    @DebugLog
    Airport getFlights(@Query(QUERY_AIRPORT) String airport);

    @GET(PATH_FLIGHT_STATUSES)
    @DebugLog
    FlightStatuses getFlightStatuses();

    @GET(PATH_AIRPORT_NAMES)
    @DebugLog
    AirportNames getAirportNames();

    @GET(PATH_AIRLINE_NAMES)
    @DebugLog
    AirlineNames getAirlineNames();
}
