package net.oldervoll.flightschedule.model;


import com.google.common.base.MoreObjects;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "flightStatuses")
public class FlightStatuses {

    @ElementList(name = "list", inline = true)
    private List<FlightStatus> list;

    public FlightStatuses(@ElementList(name = "list", inline = true) final List<FlightStatus> list) {
        this.list = list;
    }

    public List<FlightStatus> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list == null || list.size() == 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("list", list)
                .toString();
    }
}
