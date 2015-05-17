package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AirportNamesTest {

    private AirportNames airportNames;
    private List<AirportName> airportNameList;

    @Before
    public void setUp() throws Exception {
        airportNameList = Arrays.asList(
                new AirportName("BGO", "Bergen"),
                new AirportName("OSL", "Oslo"));
        airportNames = new AirportNames(airportNameList);
    }

    @Test
    public void testGetList() throws Exception {
        assertNotNull(airportNames.getList());
        assertEquals(airportNameList.size(), airportNames.getList().size());
        assertEquals(airportNameList, airportNames.getList());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(airportNames.toString());
    }
}