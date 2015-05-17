package net.oldervoll.flightschedule.model;


import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "airport")
public class Airport {

    @Element(name = "flights")
    private final Flights flights;

    public Airport(@Element(name = "flights") final Flights flights) {
        this.flights = flights;
    }

    public Flights getFlights() {
        return flights;
    }

    public List<Flight> getList() {
        return flights != null ? flights.getList() : null;
    }

    public boolean isEmpty() {
        return flights == null || flights.isEmpty();
    }

    public Optional<Flight> getFlight(String flightId, Optional<DateTime> earliestScheculeTime) {
        List<Flight> flightList = getList();
        if (flightId != null && flightList != null) {
            for (Flight flight : flightList) {
                if (flight.getFlight_id().equalsIgnoreCase(flightId)) {
                    return earliestScheculeTime.isPresent() &&
                            earliestScheculeTime.get().isBefore(flight.getEffectiveScheduleTime())
                            ? Optional.of(flight) : Optional.<Flight>absent();
                }
            }
        }
        return Optional.absent();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flights", flights)
                .toString();
    }
}
