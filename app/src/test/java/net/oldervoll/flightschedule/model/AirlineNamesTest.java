package net.oldervoll.flightschedule.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AirlineNamesTest {

    private AirlineNames airlineNames;
    private List<AirlineName> airlineNameList;

    @Before
    public void setUp() throws Exception {
        airlineNameList = Arrays.asList(
                new AirlineName("AA", "American Airlines"),
                new AirlineName("AC", "Air Canada"));
        airlineNames = new AirlineNames(airlineNameList);
    }

    @Test
    public void testGetList() throws Exception {
        assertNotNull(airlineNames.getList());
        assertEquals(airlineNameList.size(), airlineNames.getList().size());
        assertEquals(airlineNameList, airlineNames.getList());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(airlineNames.toString());
    }
}