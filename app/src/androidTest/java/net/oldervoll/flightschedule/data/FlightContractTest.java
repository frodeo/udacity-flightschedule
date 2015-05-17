package net.oldervoll.flightschedule.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import static net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import static net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import static net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

public class FlightContractTest extends AndroidTestCase {

    public void testAirlineBuildUri() {
        Uri uri = AirlineEntry.buildUri(123L);
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/airline/123", uri.toString());
    }

    public void testAirportBuildUri() {
        Uri uri = AirportEntry.buildUri(123L);
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/airport/123", uri.toString());
    }

    public void testStatusBuildUri() {
        System.out.println(StatusEntry.TABLE_NAME);
        Uri uri = StatusEntry.buildUri(123L);
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/status/123", uri.toString());
    }

    public void testFlightBuildUri() {
        Uri uri = FlightEntry.buildUri(123L);
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/flight/123", uri.toString());
    }

    public void testFlightBuildUriForAirport() {
        Uri uri = FlightEntry.buildUriForAirport("BGO");
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/flight/BGO",
            uri.toString());
    }

    public void testFlightBuildUriForAirportAndArrDep() {
        Uri uri = FlightEntry.buildUriForAirportArrDep("BGO", "A");
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/flight/BGO?arr_dep=A",
            uri.toString());
        uri = FlightEntry.buildUriForAirportArrDep("BGO", "D");
        assertNotNull(uri);
        assertEquals("content://net.oldervoll.flightschedule/flight/BGO?arr_dep=D",
            uri.toString());
    }

    public void testFlightGetAirportFromUri() {
        Uri uri = FlightEntry.buildUriForAirport("BGO");
        assertNotNull(uri);
        assertEquals("BGO", FlightEntry.getAirportFromUri(uri));
    }

    public void testFlightGetArrDepFromUri() {
        Uri uri = FlightEntry.buildUriForAirportArrDep("BGO", "A");
        assertNotNull(uri);
        assertEquals("BGO", FlightEntry.getAirportFromUri(uri));
        assertEquals("A", FlightEntry.getArrDepFromUri(uri));

        uri = FlightEntry.buildUriForAirportArrDep("BGO", "D");
        assertNotNull(uri);
        assertEquals("BGO", FlightEntry.getAirportFromUri(uri));
        assertEquals("D", FlightEntry.getArrDepFromUri(uri));
    }
}