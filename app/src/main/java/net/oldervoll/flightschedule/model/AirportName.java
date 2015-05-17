package net.oldervoll.flightschedule.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "airportName")
public class AirportName {

    @Attribute(name = "code")
    private final String code;

    @Attribute(name = "name")
    private final String name;

    public AirportName(@Attribute(name = "code") final String code,
                       @Attribute(name = "name") final String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AirportName other = (AirportName) obj;
        return Objects.equal(this.code, other.code);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("name", name)
                .toString();
    }
}
