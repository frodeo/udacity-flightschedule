package net.oldervoll.flightschedule.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "flightStatus")
public class FlightStatus {

    @Attribute(name = "code")
    private final String code;

    @Attribute(name = "statusTextEn")
    private final String statusTextEn;

    @Attribute(name = "statusTextNo")
    private final String statusTextNo;

    public FlightStatus(@Attribute(name = "code") final String code,
                        @Attribute(name = "statusTextEn") final String statusTextEn,
                        @Attribute(name = "statusTextNo") final String statusTextNo) {
        this.code = code;
        this.statusTextEn = statusTextEn;
        this.statusTextNo = statusTextNo;
    }

    public String getCode() {
        return code;
    }

    public String getStatusTextEn() {
        return statusTextEn;
    }

    public String getStatusTextNo() {
        return statusTextNo;
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
        final FlightStatus other = (FlightStatus) obj;
        return Objects.equal(this.code, other.code);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("statusTextEn", statusTextEn)
                .add("statusTextNo", statusTextNo)
                .toString();
    }
}
