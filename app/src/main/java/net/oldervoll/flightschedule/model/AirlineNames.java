package net.oldervoll.flightschedule.model;


import com.google.common.base.MoreObjects;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "airlineNames")
public class AirlineNames {

    @ElementList(name = "list", inline = true)
    private final List<AirlineName> list;

    public AirlineNames(@ElementList(name = "list", inline = true) final List<AirlineName> list) {
        this.list = list;
    }

    public List<AirlineName> getList() {
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
