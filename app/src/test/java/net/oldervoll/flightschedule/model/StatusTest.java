package net.oldervoll.flightschedule.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StatusTest {

    private static final String CODE = "A";
    private static final DateTime TIME = DateTime.parse("2014-10-29T00:01:00Z");

    private Status status;

    @Before
    public void setUp() throws Exception {
        status = new Status(CODE, TIME);
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals(CODE, status.getCode());
    }

    @Test
    public void testGetTime() throws Exception {
        assertTrue(status.getTime().isPresent());
        assertEquals(TIME, status.getTime().get());
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(status.hashCode() != 0);
    }

    @Test
    public void testEquals() throws Exception {
        Status another = new Status(CODE, TIME);
        assertTrue(status.equals(another));

        another = new Status("C", TIME);
        assertFalse(status.equals(another));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(status.toString());
    }
}