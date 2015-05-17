package net.oldervoll.flightschedule.converter.simplexml;

import org.joda.time.DateTime;
import org.simpleframework.xml.transform.Transform;

public class JodaDateTimeTransform implements Transform<DateTime> {
    @Override
    public DateTime read(String input) throws Exception {
        return DateTime.parse(input);
    }

    @Override
    public String write(DateTime dateTime) throws Exception {
        return dateTime.toString();
    }
}
