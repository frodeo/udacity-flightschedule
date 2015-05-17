package net.oldervoll.flightschedule.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import static net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import static net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static final String AIRLINE_SK = "SK";
    static final String AIRLINE_DY = "DY";

    static final String AIRPORT_BGO = "BGO";
    static final String AIRPORT_OSL = "OSL";
    static final String AIRPORT_CPH = "CPH";
    static final String AIRPORT_SVG = "SVG";

    static final String STATUS_A = "A";
    static final String STATUS_D = "D";
    static final String STATUS_E = "E";

    static final String FLIGHT_SK257 = "SK257";
    static final String FLIGHT_DY653 = "DY653";

    public static final String LOG_TAG = TestUtilities.class.getSimpleName();

    static void validateCursorAndClose(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        validateCursor(valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
            Log.d(LOG_TAG, "\t" + columnName + " = " + valueCursor.getString(idx));
        }
    }

    static List<ContentValues> createAirlineValues(final String airline) {
        final List<ContentValues> contentValuesList = new ArrayList<>();

        if (airline == null || AIRLINE_SK.equals(airline)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AirlineEntry.COLUMN_CODE, "SK");
            contentValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, "SAS");
            contentValuesList.add(contentValues);
        }

        if (airline == null || AIRLINE_DY.equals(airline)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AirlineEntry.COLUMN_CODE, "DY");
            contentValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, "Norwegian");
            contentValuesList.add(contentValues);
        }

        return contentValuesList;
    }

    static List<ContentValues> createAirportValues(final String airport) {
        final List<ContentValues> contentValuesList = new ArrayList<>();

        if (airport == null || AIRPORT_OSL.equals(airport)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AirportEntry.COLUMN_CODE, "OSL");
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "Oslo");
            contentValuesList.add(contentValues);
        }

        if (airport == null || AIRPORT_CPH.equals(airport)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AirportEntry.COLUMN_CODE, "CPH");
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "København");
            contentValuesList.add(contentValues);
        }

        if (airport == null || AIRPORT_SVG.equals(airport)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(AirportEntry.COLUMN_CODE, "SVG");
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "Stavanger");
            contentValuesList.add(contentValues);
        }

        return contentValuesList;
    }

    static List<ContentValues> createStatusValues(final String status) {
        final List<ContentValues> contentValuesList = new ArrayList<>();

        if (status == null || STATUS_A.equals(status)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(StatusEntry.COLUMN_CODE, "A");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, "Arrived");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_NO, "Landet");
            contentValuesList.add(contentValues);
        }

        if (status == null || STATUS_D.equals(status)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(StatusEntry.COLUMN_CODE, "D");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, "Departed");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_NO, "Avreist");
            contentValuesList.add(contentValues);
        }

        if (status == null || STATUS_E.equals(status)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(StatusEntry.COLUMN_CODE, "E");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, "New time");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_NO, "Ny tid");
            contentValuesList.add(contentValues);
        }

        return contentValuesList;
    }

    static List<ContentValues> createFlightValues(final String flight) {
        final List<ContentValues> contentValuesList = new ArrayList<>();

        if (flight == null || FLIGHT_SK257.equals(flight)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(FlightEntry.COLUMN_MY_AIRPORT, AIRPORT_BGO);
            contentValues.put(FlightEntry.COLUMN_UNIQUE_ID, "4957517");
            contentValues.put(FlightEntry.COLUMN_AIRLINE, "SK");
            contentValues.put(FlightEntry.COLUMN_FLIGHT_ID, "SK257");
            contentValues.put(FlightEntry.COLUMN_DOM_INT, "D");
            contentValues.put(FlightEntry.COLUMN_SCHEDULE_TIME, "2015-04-05T08:50:00Z");
            contentValues.put(FlightEntry.COLUMN_ARR_DEP, "A");
            contentValues.put(FlightEntry.COLUMN_AIRPORT, "OSL");
            contentValues.put(FlightEntry.COLUMN_VIA_AIRPORT, "CPH,SVG");
            contentValues.put(FlightEntry.COLUMN_CHECK_IN, "A");
            contentValues.put(FlightEntry.COLUMN_GATE, "16B");
            contentValues.put(FlightEntry.COLUMN_STATUS_CODE, "E");
            contentValues.put(FlightEntry.COLUMN_STATUS_TIME, "2015-04-05T10:00:00Z");
            contentValues.put(FlightEntry.COLUMN_BELT, "2");
            contentValues.put(FlightEntry.COLUMN_DELAYED, "Y");
            contentValuesList.add(contentValues);
        }

        if (flight == null || FLIGHT_DY653.equals(flight)) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(FlightEntry.COLUMN_MY_AIRPORT, AIRPORT_BGO);
            contentValues.put(FlightEntry.COLUMN_UNIQUE_ID, "4957295");
            contentValues.put(FlightEntry.COLUMN_AIRLINE, "DY");
            contentValues.put(FlightEntry.COLUMN_FLIGHT_ID, "DY653");
            contentValues.put(FlightEntry.COLUMN_DOM_INT, "D");
            contentValues.put(FlightEntry.COLUMN_SCHEDULE_TIME, "2015-04-05T18:30:00Z");
            contentValues.put(FlightEntry.COLUMN_ARR_DEP, "D");
            contentValues.put(FlightEntry.COLUMN_AIRPORT, "OSL");
            contentValuesList.add(contentValues);
        }

        return contentValuesList;
    }

    static ContentValues addFlightJoinedValues(final ContentValues contentValues) {
        String flight = contentValues.getAsString(FlightEntry.COLUMN_FLIGHT_ID);
        if (FLIGHT_SK257.equals(flight)) {
            contentValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, "SAS");
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "Oslo");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, "New time");
            contentValues.put(StatusEntry.COLUMN_STATUSTEXT_NO, "Ny tid");
        } else if (FLIGHT_DY653.equals(flight)) {
            contentValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, "Norwegian");
            contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "Oslo");
        }
        return contentValues;
    }

    // Note that this only tests that the onChange function is called; it does not test that the
    // correct Uri is returned.
    static class TestContentObserver extends ContentObserver {
        final HandlerThread handlerThread;
        boolean contentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            handlerThread = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {

            contentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return contentChanged;
                }
            }.run();
            handlerThread.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
