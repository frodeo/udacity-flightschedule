package net.oldervoll.flightschedule.data;

import android.content.ContentValues;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;
import net.oldervoll.flightschedule.model.AirlineName;
import net.oldervoll.flightschedule.model.AirlineNames;
import net.oldervoll.flightschedule.model.Airport;
import net.oldervoll.flightschedule.model.AirportName;
import net.oldervoll.flightschedule.model.AirportNames;
import net.oldervoll.flightschedule.model.Flight;
import net.oldervoll.flightschedule.model.FlightStatus;
import net.oldervoll.flightschedule.model.FlightStatuses;
import net.oldervoll.flightschedule.model.Status;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class ContentValuesMapper {
    
    public static ContentValues[] map(AirlineNames airlineNames) {
        if (airlineNames == null || airlineNames.getList() == null) {
            return new ContentValues[0];
        }
        List<AirlineName> airlineNameList = airlineNames.getList();
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (AirlineName airlineName : airlineNameList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AirlineEntry.COLUMN_CODE, airlineName.getCode());
            contentValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, airlineName.getName());
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }
    
    public static ContentValues[] map(AirportNames airportNames) {
        if (airportNames == null || airportNames.getList() == null) {
            return new ContentValues[0];
        }
        List<AirportName> airportNameList = airportNames.getList();
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (AirportName airportName : airportNameList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AirportEntry.COLUMN_CODE, airportName.getCode());
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, airportName.getName());
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }
    
    public static ContentValues[] map(FlightStatuses flightStatuses) {
        if (flightStatuses == null || flightStatuses.getList() == null) {
            return new ContentValues[0];
        }
        List<FlightStatus> flightStatusList = flightStatuses.getList();
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (FlightStatus flightStatus : flightStatusList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StatusEntry.COLUMN_CODE, flightStatus.getCode());
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, flightStatus.getStatusTextEn());
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_NO, flightStatus.getStatusTextNo());
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }
    
    public static ContentValues[] map(String myAirport, Airport flights) {
        if (flights == null || flights.getList() == null) {
            return new ContentValues[0];
        }
        List<Flight> flightList = flights.getList();
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (Flight flight : flightList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FlightEntry.COLUMN_MY_AIRPORT, myAirport);
            contentValues.put(FlightEntry.COLUMN_UNIQUE_ID, flight.getUniqueID());
            contentValues.put(FlightEntry.COLUMN_AIRLINE, flight.getAirline());
            contentValues.put(FlightEntry.COLUMN_FLIGHT_ID, flight.getFlight_id());
            contentValues.put(FlightEntry.COLUMN_DOM_INT, flight.getDom_int());
            contentValues.put(FlightEntry.COLUMN_SCHEDULE_TIME, flight.getSchedule_time().getMillis());
            contentValues.put(FlightEntry.COLUMN_ARR_DEP, flight.getArr_dep());
            contentValues.put(FlightEntry.COLUMN_AIRPORT, flight.getAirport());
            contentValues.put(FlightEntry.COLUMN_VIA_AIRPORT, flight.getVia_airport().orNull());
            contentValues.put(FlightEntry.COLUMN_CHECK_IN, flight.getCheck_in().orNull());
            contentValues.put(FlightEntry.COLUMN_GATE, flight.getGate().orNull());
            contentValues.put(FlightEntry.COLUMN_BELT, flight.getBelt().orNull());
            contentValues.put(FlightEntry.COLUMN_DELAYED, flight.getDelayed().orNull());
            Optional<Status> optionalStatus = flight.getStatus();
            if (optionalStatus.isPresent()) {
                contentValues.put(FlightEntry.COLUMN_STATUS_CODE, optionalStatus.get().getCode());
                Optional<DateTime> optionalDateTime = optionalStatus.get().getTime();
                if (optionalDateTime.isPresent()) {
                    contentValues.put(FlightEntry.COLUMN_STATUS_TIME, optionalDateTime.get().getMillis());
                } else {
                    contentValues.putNull(FlightEntry.COLUMN_STATUS_TIME);
                }
            } else {
                contentValues.putNull(FlightEntry.COLUMN_STATUS_CODE);
                contentValues.putNull(FlightEntry.COLUMN_STATUS_TIME);
            }
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }
}
