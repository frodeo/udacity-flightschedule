package net.oldervoll.flightschedule.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "status")
public class Status {

    @Attribute(name = "code")
    private final String code;

    @Attribute(name = "time", required = false)
    private final DateTime time;

    public Status(@Attribute(name = "code") final String code,
                  @Attribute(name = "time", required = false) final DateTime time) {
        this.code = code;
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public Optional<DateTime> getTime() {
        return Optional.fromNullable(time);
    }

    public String getKey() {
        return code + (time != null ? time.getMillis() : "");
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
        final Status other = (Status) obj;
        return Objects.equal(this.code, other.code);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("time", time)
                .toString();
    }
}
