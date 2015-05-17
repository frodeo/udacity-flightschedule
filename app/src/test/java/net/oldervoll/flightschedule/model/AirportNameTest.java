package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AirportNameTest {

    private static final String CODE = "BGO";
    private static final String NAME = "Bergen";

    private AirportName airportName;

    @Before
    public void setUp() throws Exception {
        airportName = new AirportName(CODE, NAME);
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals(CODE, airportName.getCode());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(NAME, airportName.getName());
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(airportName.hashCode() != 0);

        AirportName another = new AirportName(CODE, NAME);
        assertEquals(another.hashCode(), airportName.hashCode());

        another = new AirportName(CODE, "A different name");
        assertEquals(another.hashCode(), airportName.hashCode());

        another = new AirportName("OSL", NAME);
        assertNotEquals(another.hashCode(), airportName.hashCode());

        another = new AirportName("OSL", "A different name");
        assertNotEquals(another.hashCode(), airportName.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        AirportName another = new AirportName(CODE, NAME);
        assertTrue(airportName.equals(another));

        another = new AirportName(CODE, "A different name");
        assertTrue(airportName.equals(another));

        another = new AirportName("OSL", NAME);
        assertFalse(airportName.equals(another));

        another = new AirportName("OSL", "A different name");
        assertFalse(airportName.equals(another));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(airportName.toString());
    }
}