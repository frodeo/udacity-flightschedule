package net.oldervoll.flightschedule.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Defines table and column names for the flight database.
 */
public class FlightContract {

    public static final String CONTENT_AUTHORITY = "net.oldervoll.flightschedule";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_AIRLINE = "airline";
    public static final String PATH_AIRPORT = "airport";
    public static final String PATH_STATUS = "status";
    public static final String PATH_FLIGHT = "flight";

    public static final class AirlineEntry implements BaseColumns {
        public static final String TABLE_NAME = "airline";

        /** String of two or three letters (IATA code) uniquely identifying an airline */
        public static final String COLUMN_CODE = "code";
        /** String containing name of airline */
        public static final String COLUMN_AIRLINE_NAME = "airline_name";

        public static final String[] ALL_COLUMNS = {
            _ID,
            COLUMN_CODE,
            COLUMN_AIRLINE_NAME
        };

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AIRLINE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_AIRLINE;
        @SuppressWarnings("unused")
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_AIRLINE;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class AirportEntry implements BaseColumns {
        public static final String TABLE_NAME = "airport";

        /** String of three letters (IATA code) uniquely identifying an airport */
        public static final String COLUMN_CODE = "code";
        /** String containing name of airport */
        public static final String COLUMN_AIRPORT_NAME = "airport_name";

        public static final String[] ALL_COLUMNS = {
            _ID,
            COLUMN_CODE,
            COLUMN_AIRPORT_NAME
        };

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AIRPORT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_AIRPORT;
        @SuppressWarnings("unused")
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_AIRPORT;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StatusEntry implements BaseColumns {
        public static final String TABLE_NAME = "status";

        /** String of one letter uniquely identifying a flight status */
        public static final String COLUMN_CODE = "code";
        /** String containing description of flight status in english */
        public static final String COLUMN_STATUSTEXT_EN = "statusTextEn";
        /** String containing description of flight status in norwegian */
        public static final String COLUMN_STATUSTEXT_NO = "statusTextNo";

        public static final String[] ALL_COLUMNS = {
            _ID,
            COLUMN_CODE,
            COLUMN_STATUSTEXT_EN,
            COLUMN_STATUSTEXT_NO
        };

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATUS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_STATUS;
        @SuppressWarnings("unused")
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_STATUS;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FlightEntry implements BaseColumns {
        public static final String TABLE_NAME = "flight";

        /** String representing the IATA code for the home airport (where data is queried for) */
        public static final String COLUMN_MY_AIRPORT = "my_airport";
        /** String of up to 12 letters uniquely identifying a flight */
        public static final String COLUMN_UNIQUE_ID = "uniqueID";
        /** String representing the IATA code for the airline */
        public static final String COLUMN_AIRLINE = "airline";
        /** String representing a user friendly flight id (like "SK4167") */
        public static final String COLUMN_FLIGHT_ID = "flight_id";
        /** String representing the type of flight: D: domestic, I: international, S: Schengen */
        public static final String COLUMN_DOM_INT = "dom_int";
        /** String representing scheduled time as ISO-8601 time in UTC timezone */
        public static final String COLUMN_SCHEDULE_TIME = "schedule_time";
        /** String of one letter representing arrival (A) or departure (D) */
        public static final String COLUMN_ARR_DEP = "arr_dep";
        /** String representing the IATA code for the airport */
        public static final String COLUMN_AIRPORT = "airport";
        /** Comma-separated string of up to six IATA codes representing indirect airports */
        public static final String COLUMN_VIA_AIRPORT = "via_airport";
        /** String representing checkin-in area */
        public static final String COLUMN_CHECK_IN = "check_in";
        /** String of both letters and numbers representing the gate (like "37B") */
        public static final String COLUMN_GATE = "gate";
        /** String representing the flight status code from StatusEntry.COLUMN_CODE */
        public static final String COLUMN_STATUS_CODE = "status_code";
        /** String representing the status update time as ISO-8601 time in UTC timezone */
        public static final String COLUMN_STATUS_TIME = "status_time";
        /** String representing the luggage belt */
        public static final String COLUMN_BELT = "belt";
        /** String telling whether the flight is delayed or not (Y: yes, N: no) */
        public static final String COLUMN_DELAYED = "delayed";

        public static final String[] ALL_COLUMNS = {
            FlightEntry.TABLE_NAME + "." + _ID,
            COLUMN_MY_AIRPORT,
            COLUMN_UNIQUE_ID,
            COLUMN_AIRLINE,
            COLUMN_FLIGHT_ID,
            COLUMN_DOM_INT,
            COLUMN_SCHEDULE_TIME,
            COLUMN_ARR_DEP,
            COLUMN_AIRPORT,
            COLUMN_VIA_AIRPORT,
            COLUMN_CHECK_IN,
            COLUMN_GATE,
            COLUMN_STATUS_CODE,
            COLUMN_STATUS_TIME,
            COLUMN_BELT,
            COLUMN_DELAYED,
            AirlineEntry.COLUMN_AIRLINE_NAME,
            AirportEntry.COLUMN_AIRPORT_NAME,
            StatusEntry.COLUMN_STATUSTEXT_EN,
            StatusEntry.COLUMN_STATUSTEXT_NO
        };

        public static final String ARRDEP_A = "A";
        public static final String ARRDEP_D = "D";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLIGHT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_FLIGHT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_FLIGHT;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriForAirport(String airport) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(airport)
                    .build();
        }

        public static Uri buildUriForAirportArrDep(String airport, String arr_dep) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(airport)
                    .appendQueryParameter(COLUMN_ARR_DEP, arr_dep)
                    .build();
        }

        public static Uri buildUriForAirportAndFlightId(String airport, String flightId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(airport)
                    .appendPath(flightId)
                    .build();
        }

        public static String getAirportFromUri(Uri uri) {
            if (uri == null) {
                return null;
            }
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 ? pathSegments.get(1) : null;
        }

        public static String getFlightIdFromUri(Uri uri) {
            if (uri == null) {
                return null;
            }
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 3 ? pathSegments.get(2) : null;
        }

        public static String getArrDepFromUri(Uri uri) {
            return uri != null ? uri.getQueryParameter(COLUMN_ARR_DEP) : null;
        }
    }
}
