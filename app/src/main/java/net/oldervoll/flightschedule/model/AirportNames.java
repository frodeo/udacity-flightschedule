package net.oldervoll.flightschedule.model;


import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "airportNames")
public class AirportNames {

    @ElementList(name = "list", inline = true)
    private final List<AirportName> list;

    public AirportNames(@ElementList(name = "list", inline = true) final List<AirportName> list) {
        this.list = list;
    }

    public List<AirportName> getList() {
        return list;
    }

    public Optional<AirportName> get(String code) {
        for (AirportName name : list) {
            if (name.getCode().equalsIgnoreCase(code)) {
                return Optional.of(name);
            }
        }
        return Optional.absent();
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
