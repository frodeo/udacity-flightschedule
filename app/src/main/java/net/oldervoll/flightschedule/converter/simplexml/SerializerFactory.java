package net.oldervoll.flightschedule.converter.simplexml;

import org.joda.time.DateTime;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

public class SerializerFactory {
    public static Serializer create() {
        RegistryMatcher matchers = new RegistryMatcher();
        matchers.bind(DateTime.class , JodaDateTimeTransform.class);
        return new Persister(new AnnotationStrategy() , matchers);
    }
}
