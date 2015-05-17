package net.oldervoll.flightschedule.remote;

import net.oldervoll.flightschedule.model.AirlineName;
import net.oldervoll.flightschedule.model.AirlineNames;
import net.oldervoll.flightschedule.model.Airport;
import net.oldervoll.flightschedule.model.AirportName;
import net.oldervoll.flightschedule.model.AirportNames;
import net.oldervoll.flightschedule.model.FlightStatus;
import net.oldervoll.flightschedule.model.Flight;
import net.oldervoll.flightschedule.model.FlightStatuses;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AvinorServiceImplTest {

    private AvinorService service;

    @Before
    public void setUp() throws Exception {
        service = new AvinorServiceImpl();
    }

    @Test
    public void testGetFlights() throws Exception {
        Airport airport = service.getFlights("BGO");
        assertNotNull(airport);
        assertNotNull(airport.getFlights());
        assertNotNull(airport.getFlights().getLastUpdate());
        assertNotNull(airport.getFlights().getList());
        assertTrue(airport.getFlights().getList().size() > 0);
        for (Flight flight : airport.getFlights().getList()) {
            assertNotNull(flight);
            assertNotNull(flight.getUniqueID());
            assertNotNull(flight.getAirline());
            assertNotNull(flight.getFlight_id());
            assertNotNull(flight.getDom_int());
            assertNotNull(flight.getSchedule_time());
            assertNotNull(flight.getArr_dep());
            assertNotNull(flight.getAirport());
            assertNotNull(flight.getVia_airport());
            assertNotNull(flight.getCheck_in());
            assertNotNull(flight.getGate());
            assertNotNull(flight.getStatus());
            if (flight.getStatus().isPresent()) {
                assertNotNull(flight.getStatus().get().getCode());
                assertNotNull(flight.getStatus().get().getTime());
            }
            assertNotNull(flight.getBelt());
            assertNotNull(flight.getDelayed());
        }
    }

    @Test
    public void testGetFlightStatuses() throws Exception {
        FlightStatuses flighStatuses = service.getFlightStatuses();
        assertNotNull(flighStatuses);
        assertNotNull(flighStatuses.getList());
        assertEquals(5, flighStatuses.getList().size());
        assertNotNull(flighStatuses.getList().get(0));
        assertNotNull(flighStatuses.getList().get(1));
        assertNotNull(flighStatuses.getList().get(2));
        assertNotNull(flighStatuses.getList().get(3));
        assertNotNull(flighStatuses.getList().get(4));
        assertEquals("N", flighStatuses.getList().get(0).getCode());
        assertEquals("E", flighStatuses.getList().get(1).getCode());
        assertEquals("D", flighStatuses.getList().get(2).getCode());
        assertEquals("A", flighStatuses.getList().get(3).getCode());
        assertEquals("C", flighStatuses.getList().get(4).getCode());
        for (FlightStatus flightStatus : flighStatuses.getList()) {
            assertNotNull(flightStatus);
            assertNotNull(flightStatus.getCode());
            assertEquals(1, flightStatus.getCode().length());
            assertNotNull(flightStatus.getStatusTextNo());
            assertTrue(flightStatus.getStatusTextNo().length() > 0);
            assertNotNull(flightStatus.getStatusTextEn());
            assertTrue(flightStatus.getStatusTextEn().length() > 0);
        }
    }

    @Test
    public void testGetAirportNames() throws Exception {
        AirportNames airportNames = service.getAirportNames();
        assertNotNull(airportNames);
        assertNotNull(airportNames.getList());
        assertTrue(airportNames.getList().size() > 1900);
        for (AirportName airportName : airportNames.getList()) {
            assertNotNull(airportName);
            assertNotNull(airportName.getCode());
            assertEquals(3, airportName.getCode().length());
            assertNotNull(airportName.getName());
            assertTrue(airportName.getName().length() > 0);
        }
    }

    @Test
    public void testGetAirlineNames() throws Exception {
        AirlineNames airlineNames = service.getAirlineNames();
        assertNotNull(airlineNames);
        assertNotNull(airlineNames.getList());
        assertTrue(airlineNames.getList().size() > 700);
        for (AirlineName airlineName : airlineNames.getList()) {
            assertNotNull(airlineName);
            assertNotNull(airlineName.getCode());
            assertTrue(airlineName.getCode().length() >= 2);
            assertNotNull(airlineName.getName());
            assertTrue(airlineName.getName().length() > 0);
        }
    }
}