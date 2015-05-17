package net.oldervoll.flightschedule.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import static net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import static net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import static net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

public class FlightProviderTest extends AndroidTestCase {
    public static final String LOG_TAG = FlightProviderTest.class.getSimpleName();

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(FlightEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(AirlineEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(AirportEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(StatusEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver()
            .query(FlightEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver()
            .query(AirlineEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver()
                .query(AirportEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver()
                .query(StatusEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

    }

    @SuppressWarnings("unused")
    private void deleteAllRecordsFromDB() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(FlightEntry.TABLE_NAME, null, null);
        db.delete(AirlineEntry.TABLE_NAME, null, null);
        db.delete(AirportEntry.TABLE_NAME, null, null);
        db.delete(StatusEntry.TABLE_NAME, null, null);
        db.close();
    }

    private void deleteAllRecords() {
        deleteAllRecordsFromProvider();
        //deleteAllRecordsFromDB();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        try {
            ComponentName componentName = new ComponentName(mContext.getPackageName(),
                    FlightProvider.class.getName());

            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals(providerInfo.authority, FlightContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            fail("FlightProvider not registered at " + mContext.getPackageName());
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(AirlineEntry.CONTENT_URI);
        assertEquals(AirlineEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(AirportEntry.CONTENT_URI);
        assertEquals(AirportEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(StatusEntry.CONTENT_URI);
        assertEquals(StatusEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FlightEntry.CONTENT_URI);
        assertEquals(FlightEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
            FlightEntry.buildUriForAirport("BGO"));
        assertEquals(FlightEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
            FlightEntry.buildUriForAirportArrDep("BGO", "A"));
        assertEquals(FlightEntry.CONTENT_TYPE, type);
    }

    public void testBasicAirlineQueries() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<ContentValues> testValuesList = TestUtilities.createAirlineValues(null);
        for (ContentValues testValues : testValuesList) {
            long rowId = db.insert(AirlineEntry.TABLE_NAME, null, testValues);
            assertTrue(rowId != -1);

            // Test the basic content provider query
            Cursor cursor = mContext.getContentResolver().query(
                    AirlineEntry.CONTENT_URI,
                    null,
                    FlightProvider.AIRLINE_SELECTION,
                    new String[] { testValues.getAsString(AirlineEntry.COLUMN_CODE) },
                    null
            );

            // Has the NotificationUri been set correctly? We can only test this easily against API
            // level 19 or greater because getNotificationUri was added in API level 19.
            if ( Build.VERSION.SDK_INT >= 19 ) {
                assertEquals(cursor.getNotificationUri(), AirlineEntry.CONTENT_URI);
            }

            // Make sure we get the correct cursor out of the database
            TestUtilities.validateCursorAndClose(cursor, testValues);

        }

        Cursor cursor = mContext.getContentResolver().query(
                AirlineEntry.CONTENT_URI,
                AirlineEntry.ALL_COLUMNS,
                null,
                null,
                null
        );
        assertEquals(testValuesList.size(), cursor.getCount());
        cursor.close();
    }

    public void testBasicAirportQueries() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<ContentValues> testValuesList = TestUtilities.createAirportValues(null);
        for (ContentValues testValues : testValuesList) {
            long rowId = db.insert(AirportEntry.TABLE_NAME, null, testValues);
            assertTrue(rowId != -1);

            // Test the basic content provider query
            Cursor cursor = mContext.getContentResolver().query(
                    AirportEntry.CONTENT_URI,
                    null,
                    FlightProvider.AIRPORT_SELECTION,
                    new String[] { testValues.getAsString(AirlineEntry.COLUMN_CODE) },
                    null
            );

            // Has the NotificationUri been set correctly? We can only test this easily against API
            // level 19 or greater because getNotificationUri was added in API level 19.
            if ( Build.VERSION.SDK_INT >= 19 ) {
                assertEquals(cursor.getNotificationUri(), AirportEntry.CONTENT_URI);
            }

            // Make sure we get the correct cursor out of the database
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        Cursor cursor = mContext.getContentResolver().query(
                AirportEntry.CONTENT_URI,
                AirportEntry.ALL_COLUMNS,
                null,
                null,
                null
        );
        assertEquals(testValuesList.size(), cursor.getCount());
        cursor.close();
    }

    public void testBasicStatusQueries() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<ContentValues> testValuesList = TestUtilities.createStatusValues(null);
        for (ContentValues testValues : testValuesList) {
            long rowId = db.insert(StatusEntry.TABLE_NAME, null, testValues);
            assertTrue(rowId != -1);

            // Test the basic content provider query
            Cursor cursor = mContext.getContentResolver().query(
                    StatusEntry.CONTENT_URI,
                    null,
                    FlightProvider.STATUS_SELECTION,
                    new String[] { testValues.getAsString(StatusEntry.COLUMN_CODE) },
                    null
            );

            // Has the NotificationUri been set correctly? We can only test this easily against API
            // level 19 or greater because getNotificationUri was added in API level 19.
            if ( Build.VERSION.SDK_INT >= 19 ) {
                assertEquals(cursor.getNotificationUri(), StatusEntry.CONTENT_URI);
            }

            // Make sure we get the correct cursor out of the database
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        Cursor cursor = mContext.getContentResolver().query(
                StatusEntry.CONTENT_URI,
                StatusEntry.ALL_COLUMNS,
                null,
                null,
                null
        );
        assertEquals(testValuesList.size(), cursor.getCount());
        cursor.close();
    }

    public void testBasicFlightQueries() {
        // Required in order to satisfy foreign key constraints
        testBasicAirlineQueries();
        testBasicAirportQueries();
        testBasicStatusQueries();

        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<ContentValues> testValuesList = TestUtilities.createFlightValues(null);
        boolean nullStatusTested = false;
        for (ContentValues testValues : testValuesList) {
            long rowId = db.insert(FlightEntry.TABLE_NAME, null, testValues);
            assertTrue(rowId != -1);

            // Test the basic content provider query
            Cursor cursor = mContext.getContentResolver().query(
                    FlightEntry.CONTENT_URI,
                    FlightEntry.ALL_COLUMNS,
                    FlightProvider.FLIGHT_SELECTION,
                    new String[] { testValues.getAsString(FlightEntry.COLUMN_FLIGHT_ID) },
                    null
            );

            // First check a flight where status is not set
            assertTrue(cursor.moveToFirst());
            int ix = cursor.getColumnIndex(FlightEntry.COLUMN_FLIGHT_ID);
            if (cursor.getString(ix).equals(TestUtilities.FLIGHT_DY653)) {
                nullStatusTested = true;
                ix = cursor.getColumnIndex(FlightEntry.COLUMN_STATUS_CODE);
                String value = cursor.getString(ix);
                assertNull(value);
                ix = cursor.getColumnIndex(FlightEntry.COLUMN_STATUS_TIME);
                value = cursor.getString(ix);
                assertNull(value);
                long longValue = cursor.getLong(ix);
                assertEquals(0, longValue);
            }

            // Has the NotificationUri been set correctly? We can only test this easily against API
            // level 19 or greater because getNotificationUri was added in API level 19.
            if ( Build.VERSION.SDK_INT >= 19 ) {
                assertEquals(cursor.getNotificationUri(), FlightEntry.CONTENT_URI);
            }

            // Make sure we get the correct cursor out of the database
            TestUtilities.validateCursorAndClose(cursor,
                    TestUtilities.addFlightJoinedValues(testValues));
        }
        assertTrue(nullStatusTested);

        Cursor cursor = mContext.getContentResolver().query(
                FlightEntry.CONTENT_URI,
                FlightEntry.ALL_COLUMNS,
                null,
                null,
                null
        );
        assertEquals(testValuesList.size(), cursor.getCount());
        cursor.close();
    }

    public void testUpdateAirline() {
        List<ContentValues> valuesList = TestUtilities.createAirlineValues(null);
        for (ContentValues values : valuesList) {

            Uri uri = mContext.getContentResolver()
                .insert(AirlineEntry.CONTENT_URI, values);
            long rowId = ContentUris.parseId(uri);

            assertTrue(rowId != -1);
            Log.d(LOG_TAG, "New row id: " + rowId);

            ContentValues updatedValues = new ContentValues(values);
            updatedValues.put(AirlineEntry._ID, rowId);
            updatedValues.put(AirlineEntry.COLUMN_AIRLINE_NAME, "Updated");

            Cursor cursor = mContext.getContentResolver()
                .query(AirlineEntry.CONTENT_URI, null, null, null, null);

            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            cursor.registerContentObserver(tco);

            int count = mContext.getContentResolver().update(
                    AirlineEntry.CONTENT_URI, updatedValues, AirlineEntry._ID + "= ?",
                    new String[]{Long.toString(rowId)});
            assertEquals(count, 1);

            tco.waitForNotificationOrFail();

            cursor.unregisterContentObserver(tco);
            cursor.close();

            cursor = mContext.getContentResolver().query(
                    AirlineEntry.CONTENT_URI,
                    null,
                    AirlineEntry._ID + " = " + rowId,
                    null,
                    null);

            TestUtilities.validateCursorAndClose(cursor, updatedValues);

            cursor.close();
        }
    }

    public void testUpdateAirport() {
        List<ContentValues> valuesList = TestUtilities.createAirportValues(null);
        for (ContentValues values : valuesList) {

            Uri uri = mContext.getContentResolver()
                    .insert(AirportEntry.CONTENT_URI, values);
            long rowId = ContentUris.parseId(uri);

            assertTrue(rowId != -1);
            Log.d(LOG_TAG, "New row id: " + rowId);

            ContentValues updatedValues = new ContentValues(values);
            updatedValues.put(AirportEntry._ID, rowId);
            updatedValues.put(AirportEntry.COLUMN_AIRPORT_NAME, "Updated");

            Cursor cursor = mContext.getContentResolver()
                    .query(AirportEntry.CONTENT_URI, null, null, null, null);

            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            cursor.registerContentObserver(tco);

            int count = mContext.getContentResolver().update(
                    AirportEntry.CONTENT_URI, updatedValues, AirportEntry._ID + "= ?",
                    new String[]{Long.toString(rowId)});
            assertEquals(count, 1);

            tco.waitForNotificationOrFail();

            cursor.unregisterContentObserver(tco);
            cursor.close();

            cursor = mContext.getContentResolver().query(
                    AirportEntry.CONTENT_URI,
                    null,
                    AirportEntry._ID + " = " + rowId,
                    null,
                    null);

            TestUtilities.validateCursorAndClose(cursor, updatedValues);

            cursor.close();
        }
    }

    public void testUpdateStatus() {
        List<ContentValues> valuesList = TestUtilities.createStatusValues(null);
        for (ContentValues values : valuesList) {

            Uri uri = mContext.getContentResolver()
                    .insert(StatusEntry.CONTENT_URI, values);
            long rowId = ContentUris.parseId(uri);

            assertTrue(rowId != -1);
            Log.d(LOG_TAG, "New row id: " + rowId);

            ContentValues updatedValues = new ContentValues(values);
            updatedValues.put(StatusEntry._ID, rowId);
            updatedValues.put(StatusEntry.COLUMN_STATUSTEXT_EN, "Updated");

            Cursor cursor = mContext.getContentResolver()
                    .query(StatusEntry.CONTENT_URI, null, null, null, null);

            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            cursor.registerContentObserver(tco);

            int count = mContext.getContentResolver().update(
                    StatusEntry.CONTENT_URI, updatedValues, StatusEntry._ID + "= ?",
                    new String[]{Long.toString(rowId)});
            assertEquals(count, 1);

            tco.waitForNotificationOrFail();

            cursor.unregisterContentObserver(tco);
            cursor.close();

            cursor = mContext.getContentResolver().query(
                    StatusEntry.CONTENT_URI,
                    null,
                    StatusEntry._ID + " = " + rowId,
                    null,
                    null);

            TestUtilities.validateCursorAndClose(cursor, updatedValues);

            cursor.close();
        }
    }

    public void testUpdateFlight() {
        // Required in order to satisfy foreign key constraints
        testUpdateAirline();
        testUpdateAirport();
        testUpdateStatus();

        List<ContentValues> valuesList = TestUtilities.createFlightValues(null);
        for (ContentValues values : valuesList) {

            Uri uri = mContext.getContentResolver()
                    .insert(FlightEntry.CONTENT_URI, values);
            long rowId = ContentUris.parseId(uri);

            assertTrue(rowId != -1);
            Log.d(LOG_TAG, "New row id: " + rowId);

            ContentValues updatedValues = new ContentValues(values);
            updatedValues.put(FlightEntry._ID, rowId);
            updatedValues.put(FlightEntry.COLUMN_GATE, "Updated");

            Cursor cursor = mContext.getContentResolver()
                    .query(FlightEntry.CONTENT_URI, null, null, null, null);

            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            cursor.registerContentObserver(tco);

            int count = mContext.getContentResolver().update(
                    FlightEntry.CONTENT_URI, updatedValues, FlightEntry._ID + "= ?",
                    new String[]{Long.toString(rowId)});
            assertEquals(count, 1);

            tco.waitForNotificationOrFail();

            cursor.unregisterContentObserver(tco);
            cursor.close();

            cursor = mContext.getContentResolver().query(
                    FlightEntry.CONTENT_URI,
                    FlightEntry.ALL_COLUMNS,
                    FlightEntry.TABLE_NAME + "." + FlightEntry._ID + " = " + rowId,
                    null,
                    null);

            TestUtilities.validateCursorAndClose(cursor, updatedValues);

            cursor.close();
        }
    }

    public void testInsertReadProvider() {
        for (ContentValues testValues : TestUtilities.createAirlineValues(null)) {
            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            mContext.getContentResolver().registerContentObserver(AirlineEntry.CONTENT_URI, true, tco);
            Uri uri = mContext.getContentResolver().insert(AirlineEntry.CONTENT_URI, testValues);
            assertNotNull(uri);
            tco.waitForNotificationOrFail();
            mContext.getContentResolver().unregisterContentObserver(tco);
            long id = ContentUris.parseId(uri);
            assertTrue(id != -1);

            Cursor cursor = mContext.getContentResolver().query(
                    AirlineEntry.CONTENT_URI,
                    null,
                    AirlineEntry._ID + " = " + id,
                    null,
                    null
            );
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        for (ContentValues testValues : TestUtilities.createAirportValues(null)) {
            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            mContext.getContentResolver().registerContentObserver(AirportEntry.CONTENT_URI, true, tco);
            Uri uri = mContext.getContentResolver().insert(AirportEntry.CONTENT_URI, testValues);
            assertNotNull(uri);
            tco.waitForNotificationOrFail();
            mContext.getContentResolver().unregisterContentObserver(tco);
            long id = ContentUris.parseId(uri);
            assertTrue(id != -1);

            Cursor cursor = mContext.getContentResolver().query(
                    AirportEntry.CONTENT_URI,
                    null,
                    AirportEntry._ID + " = " + id,
                    null,
                    null
            );
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        for (ContentValues testValues : TestUtilities.createStatusValues(null)) {
            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            mContext.getContentResolver().registerContentObserver(StatusEntry.CONTENT_URI, true, tco);
            Uri uri = mContext.getContentResolver().insert(StatusEntry.CONTENT_URI, testValues);
            assertNotNull(uri);
            tco.waitForNotificationOrFail();
            mContext.getContentResolver().unregisterContentObserver(tco);
            long id = ContentUris.parseId(uri);
            assertTrue(id != -1);

            Cursor cursor = mContext.getContentResolver().query(
                    StatusEntry.CONTENT_URI,
                    null,
                    StatusEntry._ID + " = " + id,
                    null,
                    null
            );
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        for (ContentValues testValues : TestUtilities.createFlightValues(null)) {
            TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
            mContext.getContentResolver().registerContentObserver(FlightEntry.CONTENT_URI, true, tco);
            Uri uri = mContext.getContentResolver().insert(FlightEntry.CONTENT_URI, testValues);
            assertNotNull(uri);
            tco.waitForNotificationOrFail();
            mContext.getContentResolver().unregisterContentObserver(tco);
            long id = ContentUris.parseId(uri);
            assertTrue(id != -1);

            Cursor cursor = mContext.getContentResolver().query(
                    FlightEntry.CONTENT_URI,
                    FlightEntry.ALL_COLUMNS,
                    FlightEntry.TABLE_NAME + "." + FlightEntry._ID + " = " + id,
                    null,
                    null
            );
            TestUtilities.validateCursorAndClose(cursor, testValues);
        }

        Cursor cursor = mContext.getContentResolver().query(
                FlightEntry.buildUriForAirport("OSL"),
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        assertFalse(cursor.moveToNext());

        cursor = mContext.getContentResolver().query(
                FlightEntry.buildUriForAirport("BGO"),
                null,
                null,
                null,
                null
        );
        assertEquals(2, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertTrue(cursor.moveToNext());
        assertFalse(cursor.moveToNext());

        cursor = mContext.getContentResolver().query(
                FlightEntry.buildUriForAirportArrDep("BGO", "A"),
                null,
                null,
                null,
                null
        );
        assertTrue(cursor.moveToNext());
        assertFalse(cursor.moveToNext());

        cursor.close();
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver airlineObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AirlineEntry.CONTENT_URI, true, airlineObserver);

        TestUtilities.TestContentObserver airportObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AirportEntry.CONTENT_URI, true, airportObserver);

        TestUtilities.TestContentObserver statusObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StatusEntry.CONTENT_URI, true, statusObserver);


        TestUtilities.TestContentObserver flightObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FlightEntry.CONTENT_URI, true, flightObserver);

        deleteAllRecordsFromProvider();

        airlineObserver.waitForNotificationOrFail();
        airportObserver.waitForNotificationOrFail();
        statusObserver.waitForNotificationOrFail();
        flightObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(airlineObserver);
        mContext.getContentResolver().unregisterContentObserver(airportObserver);
        mContext.getContentResolver().unregisterContentObserver(statusObserver);
        mContext.getContentResolver().unregisterContentObserver(flightObserver);
    }

    public void testBulkInsertAirline() {
        List<ContentValues> contentValuesList = TestUtilities.createAirlineValues(null);

        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver()
            .registerContentObserver(AirlineEntry.CONTENT_URI, true, contentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(AirlineEntry.CONTENT_URI,
                contentValuesList.toArray(new ContentValues[contentValuesList.size()]));

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        assertEquals(insertCount, contentValuesList.size());

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                AirlineEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // We should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), contentValuesList.size());

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (ContentValues contentValues : contentValuesList) {
            TestUtilities.validateCursor(cursor, contentValues);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void testBulkInsertAirport() {
        List<ContentValues> contentValuesList = TestUtilities.createAirportValues(null);

        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(AirportEntry.CONTENT_URI, true, contentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(AirportEntry.CONTENT_URI,
                contentValuesList.toArray(new ContentValues[contentValuesList.size()]));

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        assertEquals(insertCount, contentValuesList.size());

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                AirportEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // We should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), contentValuesList.size());

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (ContentValues contentValues : contentValuesList) {
            TestUtilities.validateCursor(cursor, contentValues);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void testBulkInsertStatus() {
        List<ContentValues> contentValuesList = TestUtilities.createStatusValues(null);

        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(StatusEntry.CONTENT_URI, true, contentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(StatusEntry.CONTENT_URI,
            contentValuesList.toArray(new ContentValues[contentValuesList.size()]));

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        assertEquals(insertCount, contentValuesList.size());

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                StatusEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // We should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), contentValuesList.size());

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (ContentValues contentValues : contentValuesList) {
            TestUtilities.validateCursor(cursor, contentValues);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void testBulkInsertFlight() {
        // Required in order to satisfy foreign key constraints
        testBulkInsertAirline();
        testBulkInsertAirport();
        testBulkInsertStatus();

        List<ContentValues> contentValuesList = TestUtilities.createFlightValues(null);

        TestUtilities.TestContentObserver contentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(FlightEntry.CONTENT_URI, true, contentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(FlightEntry.CONTENT_URI,
            contentValuesList.toArray(new ContentValues[contentValuesList.size()]));

        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        assertEquals(insertCount, contentValuesList.size());

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FlightEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // We should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), contentValuesList.size());

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (ContentValues contentValues : contentValuesList) {
            TestUtilities.validateCursor(cursor, contentValues);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
