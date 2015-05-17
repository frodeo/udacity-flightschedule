package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AirlineNameTest {

    private static final String CODE = "AA";
    private static final String NAME = "American Airlines";

    private AirlineName airlineName;

    @Before
    public void setUp() throws Exception {
        airlineName = new AirlineName(CODE, NAME);
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals(CODE, airlineName.getCode());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(NAME, airlineName.getName());
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(airlineName.hashCode() != 0);

        AirlineName another = new AirlineName(CODE, NAME);
        assertEquals(another.hashCode(), airlineName.hashCode());

        another = new AirlineName(CODE, "A different name");
        assertEquals(another.hashCode(), airlineName.hashCode());

        another = new AirlineName("AC", NAME);
        assertNotEquals(another.hashCode(), airlineName.hashCode());

        another = new AirlineName("AC", "A different name");
        assertNotEquals(another.hashCode(), airlineName.hashCode());

    }

    @Test
    public void testEquals() throws Exception {
        AirlineName another = new AirlineName(CODE, NAME);
        assertTrue(airlineName.equals(another));

        another = new AirlineName(CODE, "A different name");
        assertTrue(airlineName.equals(another));

        another = new AirlineName("AC", NAME);
        assertFalse(airlineName.equals(another));

        another = new AirlineName("AC", "A different name");
        assertFalse(airlineName.equals(another));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(airlineName.toString());
    }
}