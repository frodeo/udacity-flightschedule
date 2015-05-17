package net.oldervoll.flightschedule.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.lang.Exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlightTest {

    private static final String UNIQUE_ID = "4622539";
    private static final String AIRLINE = "SK";
    private static final String FLIGHT_ID = "SK9121";
    private static final String DOM_INT = "D";
    private static final String SCHEDULE_TIME = "2014-10-29T00:01:00Z";
    private static final String ARR_DEP = "D";
    private static final String AIRPORT = "OSL";

    private Flight flight;

    @Before
    public void setUp() throws Exception {
        flight = new Flight(
                UNIQUE_ID,
                AIRLINE,
                FLIGHT_ID,
                DOM_INT,
                DateTime.parse(SCHEDULE_TIME),
                ARR_DEP,
                AIRPORT,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Test
    public void testGetUniqueID() throws Exception {
        assertEquals(UNIQUE_ID, flight.getUniqueID());
    }

    @Test
    public void testGetAirline() throws Exception {
        assertEquals(AIRLINE, flight.getAirline());
    }

    @Test
    public void testGetFlight_id() throws Exception {
        assertEquals(FLIGHT_ID, flight.getFlight_id());
    }

    @Test
    public void testGetDom_int() throws Exception {
        assertEquals(DOM_INT, flight.getDom_int());
    }

    @Test
    public void testGetSchedule_time() throws Exception {
        assertEquals(DateTime.parse(SCHEDULE_TIME), flight.getSchedule_time());
    }

    @Test
    public void testGetArr_dep() throws Exception {
        assertEquals(ARR_DEP, flight.getArr_dep());
    }

    @Test
    public void testGetAirport() throws Exception {
        assertEquals(AIRPORT, flight.getAirport());
    }

    @Test
    public void testGetVia_airport() throws Exception {
        assertFalse(flight.getVia_airport().isPresent());
    }

    @Test
    public void testGetCheck_in() throws Exception {
        assertFalse(flight.getCheck_in().isPresent());
    }

    @Test
    public void testGetGate() throws Exception {
        assertFalse(flight.getGate().isPresent());
    }

    @Test
    public void testGetStatus() throws Exception {
        assertFalse(flight.getStatus().isPresent());
    }

    @Test
    public void testGetBelt() throws Exception {
        assertFalse(flight.getBelt().isPresent());
    }

    @Test
    public void testGetDelayed() throws Exception {
        assertFalse(flight.getDelayed().isPresent());
    }

    public void testIsDomestic() throws Exception {
        assertTrue(flight.isDomestic());
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(flight.hashCode() != 0);
    }

    @Test
    public void testEquals() throws Exception {
        Flight another = new Flight(
                UNIQUE_ID, null, null, null, null, null, null, null, null, null, null, null, null);
        assertTrue(flight.equals(another));

        another = new Flight(
                "123", null, null, null, null, null, null, null, null, null, null, null, null);
        assertFalse(flight.equals(another));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(flight.toString());
    }
}