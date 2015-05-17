package net.oldervoll.flightschedule.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import static net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import static net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

public class FlightDbHelperTest extends AndroidTestCase {

    public static final String LOG_TAG = FlightDbHelperTest.class.getSimpleName();

    public void setUp() {
        mContext.deleteDatabase(FlightDbHelper.DATABASE_NAME);
        Log.d(LOG_TAG, "Deleted database " + FlightDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        final Set<String> tableNames = new HashSet<>();
        tableNames.add(AirlineEntry.TABLE_NAME);
        tableNames.add(AirportEntry.TABLE_NAME);
        tableNames.add(StatusEntry.TABLE_NAME);
        tableNames.add(FlightEntry.TABLE_NAME);

        final SQLiteDatabase db = new FlightDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue(c.moveToFirst());

        do {
            tableNames.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue(tableNames.isEmpty());

        // Table airline
        c = db.rawQuery("PRAGMA table_info(" + AirlineEntry.TABLE_NAME + ")", null);
        assertTrue(c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        HashSet<String> columnNames = new HashSet<>();
        columnNames.add(AirlineEntry._ID);
        columnNames.add(AirlineEntry.COLUMN_CODE);
        columnNames.add(AirlineEntry.COLUMN_AIRLINE_NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnNames.remove(columnName);
        } while(c.moveToNext());

        assertTrue(columnNames.isEmpty());

        // Table airport
        c = db.rawQuery("PRAGMA table_info(" + AirportEntry.TABLE_NAME + ")", null);
        assertTrue(c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        columnNames = new HashSet<>();
        columnNames.add(AirportEntry._ID);
        columnNames.add(AirportEntry.COLUMN_CODE);
        columnNames.add(AirportEntry.COLUMN_AIRPORT_NAME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnNames.remove(columnName);
        } while(c.moveToNext());

        assertTrue(columnNames.isEmpty());

        // Table status
        c = db.rawQuery("PRAGMA table_info(" + StatusEntry.TABLE_NAME + ")", null);
        assertTrue(c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        columnNames = new HashSet<>();
        columnNames.add(StatusEntry._ID);
        columnNames.add(StatusEntry.COLUMN_CODE);
        columnNames.add(StatusEntry.COLUMN_STATUSTEXT_EN);
        columnNames.add(StatusEntry.COLUMN_STATUSTEXT_NO);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnNames.remove(columnName);
        } while(c.moveToNext());

        assertTrue(columnNames.isEmpty());

        // Table flight
        c = db.rawQuery("PRAGMA table_info(" + FlightEntry.TABLE_NAME + ")", null);
        assertTrue(c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        columnNames = new HashSet<>();
        columnNames.add(FlightEntry._ID);
        columnNames.add(FlightEntry.COLUMN_UNIQUE_ID);
        columnNames.add(FlightEntry.COLUMN_AIRLINE);
        columnNames.add(FlightEntry.COLUMN_FLIGHT_ID);
        columnNames.add(FlightEntry.COLUMN_DOM_INT);
        columnNames.add(FlightEntry.COLUMN_SCHEDULE_TIME);
        columnNames.add(FlightEntry.COLUMN_ARR_DEP);
        columnNames.add(FlightEntry.COLUMN_AIRPORT);
        columnNames.add(FlightEntry.COLUMN_VIA_AIRPORT);
        columnNames.add(FlightEntry.COLUMN_CHECK_IN);
        columnNames.add(FlightEntry.COLUMN_GATE);
        columnNames.add(FlightEntry.COLUMN_STATUS_CODE);
        columnNames.add(FlightEntry.COLUMN_STATUS_TIME);
        columnNames.add(FlightEntry.COLUMN_BELT);
        columnNames.add(FlightEntry.COLUMN_DELAYED);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnNames.remove(columnName);
        } while(c.moveToNext());

        assertTrue(columnNames.isEmpty());

        c.close();
        db.close();
    }

    public void testAirlineTable() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = TestUtilities.createAirlineValues(null);

        assertNotNull(contentValuesList);
        assertEquals(2, contentValuesList.size());

        for (ContentValues contentValues : contentValuesList) {
            long id = database.insert(AirlineEntry.TABLE_NAME, null, contentValues);
            assertTrue(id != -1);
            Log.d(LOG_TAG, "Inserted " + contentValues + " into " + AirlineEntry.TABLE_NAME);

            Cursor cursor = database.query(
                    AirlineEntry.TABLE_NAME, // Table
                    null, // Columns
                    "_id = " + id, // Selection
                    null, // Selection args
                    null, // Group by
                    null, // Having
                    null // Order by
            );
            assertNotNull(cursor);

            boolean success = cursor.moveToFirst();
            assertTrue(success);

            Log.d(LOG_TAG, "Validating airline entry " + contentValues);
            TestUtilities.validateCursorAndClose(cursor, contentValues);
            assertFalse(cursor.moveToNext());

            cursor.close();
        }
        database.close();
    }

    public void testAirportTable() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = TestUtilities.createAirportValues(null);

        assertNotNull(contentValuesList);
        assertEquals(3, contentValuesList.size());

        for (ContentValues contentValues : contentValuesList) {
            long id = database.insert(AirportEntry.TABLE_NAME, null, contentValues);
            assertTrue(id != -1);
            Log.d(LOG_TAG, "Inserted " + contentValues + " into " + AirlineEntry.TABLE_NAME);

            Cursor cursor = database.query(
                    AirportEntry.TABLE_NAME, // Table
                    null, // Columns
                    "_id = " + id, // Selection
                    null, // Selection args
                    null, // Group by
                    null, // Having
                    null // Order by
            );
            assertNotNull(cursor);

            boolean success = cursor.moveToFirst();
            assertTrue(success);

            Log.d(LOG_TAG, "Validating airport entry " + contentValues);
            TestUtilities.validateCursorAndClose(cursor, contentValues);
            assertFalse(cursor.moveToNext());

            cursor.close();
        }
        database.close();
    }

    public void testStatusTable() {
        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = TestUtilities.createStatusValues(null);

        assertNotNull(contentValuesList);
        assertEquals(3, contentValuesList.size());

        for (ContentValues contentValues : contentValuesList) {
            long id = database.insert(StatusEntry.TABLE_NAME, null, contentValues);
            assertTrue(id != -1);
            Log.d(LOG_TAG, "Inserted " + contentValues + " into " + AirlineEntry.TABLE_NAME);

            Cursor cursor = database.query(
                    StatusEntry.TABLE_NAME, // Table
                    null, // Columns
                    "_id = " + id, // Selection
                    null, // Selection args
                    null, // Group by
                    null, // Having
                    null // Order by
            );
            assertNotNull(cursor);

            boolean success = cursor.moveToFirst();
            assertTrue(success);

            Log.d(LOG_TAG, "Validating status entry " + contentValues);
            TestUtilities.validateCursorAndClose(cursor, contentValues);
            assertFalse(cursor.moveToNext());

            cursor.close();
        }
        database.close();
    }

    public void testFlightTable() {
        // Required in order to satisfy foreign key constraints
        testAirlineTable();
        testAirportTable();
        testStatusTable();

        FlightDbHelper dbHelper = new FlightDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = TestUtilities.createFlightValues(null);

        assertNotNull(contentValuesList);
        assertEquals(2, contentValuesList.size());

        for (ContentValues contentValues : contentValuesList) {
            long id = database.insert(FlightEntry.TABLE_NAME, null, contentValues);
            assertTrue(id != -1);
            Log.d(LOG_TAG, "Inserted " + contentValues + " into " + AirlineEntry.TABLE_NAME);

            Cursor cursor = database.query(
                    FlightEntry.TABLE_NAME, // Table
                    null, // Columns
                    "_id = " + id, // Selection
                    null, // Selection args
                    null, // Group by
                    null, // Having
                    null // Order by
            );
            assertNotNull(cursor);

            boolean success = cursor.moveToFirst();
            assertTrue(success);

            Log.d(LOG_TAG, "Validating flight entry " + contentValues);
            TestUtilities.validateCursorAndClose(cursor, contentValues);
            assertFalse(cursor.moveToNext());

            cursor.close();
        }
        database.close();
    }
}
