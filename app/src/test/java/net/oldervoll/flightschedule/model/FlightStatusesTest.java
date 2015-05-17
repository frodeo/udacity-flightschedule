package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FlightStatusesTest {

    private FlightStatuses flightStatuses;
    private List<FlightStatus> flightStatusList;

    @Before
    public void setUp() throws Exception {
        flightStatusList = Arrays.asList(
                new FlightStatus("A", "Some text", "Some text"),
                new FlightStatus("D", "Another text", "Another text"));
        flightStatuses = new FlightStatuses(flightStatusList);
    }

    @Test
    public void testGetList() throws Exception {
        assertNotNull(flightStatuses.getList());
        assertEquals(flightStatusList.size(), flightStatuses.getList().size());
        assertEquals(flightStatusList, flightStatuses.getList());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(flightStatuses.toString());
    }
}