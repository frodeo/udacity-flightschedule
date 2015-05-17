package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlighStatusTest {

    private static final String CODE = "A";
    private static final String STATUSTEXT_EN = "Arrived";
    private static final String STATUSTEXT_NO = "Landet";

    private FlightStatus flightStatus;


    @Before
    public void setUp() throws Exception {
        flightStatus = new FlightStatus(CODE, STATUSTEXT_EN, STATUSTEXT_NO);
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals(CODE, flightStatus.getCode());
    }

    @Test
    public void testGetStatusTextEn() throws Exception {
        assertEquals(STATUSTEXT_EN, flightStatus.getStatusTextEn());
    }

    @Test
    public void testGetStatusTextNo() throws Exception {
        assertEquals(STATUSTEXT_NO, flightStatus.getStatusTextNo());
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(flightStatus.hashCode() != 0);

        FlightStatus another = new FlightStatus(CODE, STATUSTEXT_EN, STATUSTEXT_NO);
        assertEquals(another.hashCode(), flightStatus.hashCode());

        another = new FlightStatus(CODE, "A different text", "A different text");
        assertEquals(another.hashCode(), flightStatus.hashCode());

        another = new FlightStatus("D", STATUSTEXT_EN, STATUSTEXT_NO);
        assertNotEquals(another.hashCode(), flightStatus.hashCode());

        another = new FlightStatus("D", "A different text", "A different text");
        assertNotEquals(another.hashCode(), flightStatus.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        FlightStatus another = new FlightStatus(CODE, STATUSTEXT_EN, STATUSTEXT_NO);
        assertTrue(flightStatus.equals(another));

        another = new FlightStatus(CODE, "A different text", "A different text");
        assertTrue(flightStatus.equals(another));

        another = new FlightStatus("D", STATUSTEXT_EN, STATUSTEXT_NO);
        assertFalse(flightStatus.equals(another));

        another = new FlightStatus("D", "A different text", "A different text");
        assertFalse(flightStatus.equals(another));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(flightStatus.toString());
    }
}