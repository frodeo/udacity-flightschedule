package net.oldervoll.flightschedule.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FlightsTest {

    private Flights flights;
    private DateTime lastUpdate;
    private List<Flight> flightLists;

    @Before
    public void setUp() throws Exception {
        flightLists = Arrays.asList(
                new Flight("123", null, null, null, null, null, null, null, null, null, null, null,
                        null),
                new Flight("456", null, null, null, null, null, null, null, null, null, null, null,
                        null));
        lastUpdate = DateTime.now();
        flights = new Flights(lastUpdate, flightLists);
    }

    @Test
    public void testGetList() throws Exception {
        assertNotNull(flights.getList());
        assertEquals(flightLists.size(), flights.getList().size());
        assertEquals(flightLists, flights.getList());
    }

    @Test
    public void testGetLastUpdate() throws Exception {
        assertEquals(lastUpdate, flights.getLastUpdate());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(flights.toString());
    }
}