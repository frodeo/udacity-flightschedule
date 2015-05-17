package net.oldervoll.flightschedule.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "flight")
public class Flight {

    @Attribute(name = "uniqueID")
    private final String uniqueID;

    @Element(name = "airline")
    private final String airline;

    @Element(name = "flight_id")
    private final String flight_id;

    @Element(name = "dom_int")
    private final String dom_int;

    @Element(name = "schedule_time")
    private final DateTime schedule_time;

    @Element(name = "arr_dep")
    private final String arr_dep;

    @Element(name = "airport")
    private final String airport;

    @Element(name = "via_airport", required = false)
    private final String via_airport;

    @Element(name = "check_in", required = false)
    private final String check_in;

    @Element(name = "gate", required = false)
    private final String gate;

    @Element(name = "status", required = false)
    private final Status status;

    @Element(name = "belt", required = false)
    private final String belt;

    @Element(name = "delayed", required = false)
    private final String delayed;

    public Flight(@Attribute(name = "uniqueID") final String uniqueID,
                  @Element(name = "airline") final String airline,
                  @Element(name = "flight_id") final String flight_id,
                  @Element(name = "dom_int") final String dom_int,
                  @Element(name = "schedule_time") final DateTime schedule_time,
                  @Element(name = "arr_dep") final String arr_dep,
                  @Element(name = "airport") final String airport,
                  @Element(name = "via_airport") final String via_airport,
                  @Element(name = "check_in") final String check_in,
                  @Element(name = "gate") final String gate,
                  @Element(name = "status") final Status status,
                  @Element(name = "belt") final String belt,
                  @Element(name = "delayed") final String delayed) {
        this.uniqueID = uniqueID;
        this.airline = airline;
        this.flight_id = flight_id;
        this.dom_int = dom_int;
        this.schedule_time = schedule_time;
        this.arr_dep = arr_dep;
        this.airport = airport;
        this.via_airport = via_airport;
        this.check_in = check_in;
        this.gate = gate;
        this.status = status;
        this.belt = belt;
        this.delayed = delayed;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getAirline() {
        return airline;
    }

    public String getFlight_id() {
        return flight_id;
    }

    public String getDom_int() {
        return dom_int;
    }

    public DateTime getSchedule_time() {
        return schedule_time;
    }

    public String getArr_dep() {
        return arr_dep;
    }

    public String getAirport() {
        return airport;
    }

    public Optional<String> getVia_airport() {
        return Optional.fromNullable(via_airport);
    }

    public Optional<String> getCheck_in() {
        return Optional.fromNullable(check_in);
    }

    public Optional<String> getGate() {
        return Optional.fromNullable(gate);
    }

    public Optional<Status> getStatus() {
        return Optional.fromNullable(status);
    }

    public Optional<String> getBelt() {
        return Optional.fromNullable(belt);
    }

    public Optional<String> getDelayed() {
        return Optional.fromNullable(delayed);
    }

    public boolean isDeparture() {
        return arr_dep.equalsIgnoreCase("D");
    }

    @SuppressWarnings("unused")
    public boolean isDomestic() {
        return dom_int.equalsIgnoreCase("D");
    }

    public boolean isCancelled() {
        return status != null && status.getCode().equalsIgnoreCase("C");
    }

    public DateTime getEffectiveScheduleTime() {
        return status != null &&
               status.getCode().equalsIgnoreCase("E") &&
               status.getTime().isPresent() ? status.getTime().get() : schedule_time;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Flight other = (Flight) obj;
        return Objects.equal(this.uniqueID, other.uniqueID);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uniqueID", uniqueID)
                .add("airline", airline)
                .add("flight_id", flight_id)
                .add("dom_int", dom_int)
                .add("schedule_time", schedule_time)
                .add("arr_dep", arr_dep)
                .add("airport", airport)
                .add("via_airport", via_airport)
                .add("check_in", check_in)
                .add("gate", gate)
                .add("status", status)
                .add("belt", belt)
                .add("delayed", delayed)
                .toString();
    }
}
