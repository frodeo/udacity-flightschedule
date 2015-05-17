package net.oldervoll.flightschedule.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

public class UriMatcherTest extends AndroidTestCase {
    private static final Uri TEST_AIRLINE_URI = AirlineEntry.CONTENT_URI;
    private static final Uri TEST_AIRPORT_URI = AirportEntry.CONTENT_URI;
    private static final Uri TEST_STATUS_URI = StatusEntry.CONTENT_URI;
    private static final Uri TEST_FLIGHT_URI = FlightEntry.CONTENT_URI;
    private static final Uri TEST_FLIGHT_FOR_AIRPORT_URI =
        FlightEntry.buildUriForAirport("BGO");
    private static final Uri TEST_FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID_URI=
            FlightEntry.buildUriForAirportAndFlightId("BGO", "SK123");

    public void testUriMatcher() {
        UriMatcher testMatcher = FlightProvider.buildUriMatcher();

        assertEquals(testMatcher.match(TEST_AIRLINE_URI), FlightProvider.AIRLINE);
        assertEquals(testMatcher.match(TEST_AIRPORT_URI), FlightProvider.AIRPORT);
        assertEquals(testMatcher.match(TEST_STATUS_URI), FlightProvider.STATUS);
        assertEquals(testMatcher.match(TEST_FLIGHT_URI), FlightProvider.FLIGHT);
        assertEquals(testMatcher.match(TEST_FLIGHT_FOR_AIRPORT_URI),
            FlightProvider.FLIGHT_FOR_AIRPORT);
        assertEquals(testMatcher.match(TEST_FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID_URI),
            FlightProvider.FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID);
    }
}
