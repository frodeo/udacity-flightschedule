package net.oldervoll.flightschedule.remote;

import android.util.Log;

import net.oldervoll.flightschedule.converter.simplexml.SerializerFactory;
import net.oldervoll.flightschedule.model.AirlineNames;
import net.oldervoll.flightschedule.model.Airport;
import net.oldervoll.flightschedule.model.AirportNames;
import net.oldervoll.flightschedule.model.FlightStatuses;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.SimpleXMLConverter;

public class AvinorServiceImpl implements AvinorService {

    public final String LOG_TAG = AvinorServiceImpl.class.getSimpleName();

    private static final String ENDPOINT = "http://flydata.avinor.no";
    private AvinorService service;

    public AvinorServiceImpl() {
        service = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new SimpleXMLConverter(SerializerFactory.create()))
                .build()
                .create(AvinorService.class);
    }

    @Override
    public Airport getFlights(String airport) {
        try {
            return service.getFlights(airport);
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to load flights (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }

    @Override
    public FlightStatuses getFlightStatuses() {
        try {
            return service.getFlightStatuses();
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to load statuses (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }

    @Override
    public AirportNames getAirportNames() {
        try {
            return service.getAirportNames();
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to load airport names (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }

    @Override
    public AirlineNames getAirlineNames() {
        try {
            return service.getAirlineNames();
        } catch (RetrofitError e) {
            Log.w(LOG_TAG,
                    "Failed to load airline names (kind: " + e.getKind() + "): " + e.getMessage());
            return null;
        }
    }
}
