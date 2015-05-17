package net.oldervoll.flightschedule.model;

import com.google.common.base.MoreObjects;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "flights")
public class Flights {

    @Attribute(name = "lastUpdate")
    private final DateTime lastUpdate;

    @ElementList(name = "list", inline = true)
    private final List<Flight> list;

    public Flights(@Attribute(name = "lastUpdate") final DateTime lastUpdate,
                   @ElementList(name = "list", inline = true) final List<Flight> list) {
        this.lastUpdate = lastUpdate;
        this.list = list;
    }

    public DateTime getLastUpdate() {
        return lastUpdate;
    }

    public List<Flight> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list == null || list.size() == 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lastUpdate", lastUpdate)
                .add("list", list)
                .toString();
    }
}
