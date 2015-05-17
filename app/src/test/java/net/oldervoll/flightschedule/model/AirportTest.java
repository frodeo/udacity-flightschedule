package net.oldervoll.flightschedule.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class AirportTest {

    private Airport airport;

    @Before
    public void setUp() throws Exception {
        List<Flight>  flightLists = Arrays.asList(
                new Flight("123", null, null, null, null, null, null, null, null, null, null, null,
                        null),
                new Flight("456", null, null, null, null, null, null, null, null, null, null, null,
                        null));
        DateTime lastUpdate = DateTime.now();
        Flights flights = new Flights(lastUpdate, flightLists);
        airport = new Airport(flights);
    }

    @Test
    public void testGetFlights() throws Exception {
        assertNotNull(airport.getFlights());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(airport.toString());
    }
}