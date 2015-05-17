package net.oldervoll.flightschedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

/**
 * Manages a local database for flight data.
 */
public class FlightDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FlightDbHelper.class.getSimpleName();

    // Increment the database version when the schema is changed
    // 1: initial version
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "flight.db";

    public FlightDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(LOG_TAG, "Opened database " + DATABASE_NAME + " (version " + DATABASE_VERSION + ")");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_AIRLINE_TABLE = "CREATE TABLE " + AirlineEntry.TABLE_NAME + " (" +
                AirlineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AirlineEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                AirlineEntry.COLUMN_AIRLINE_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + AirlineEntry.COLUMN_CODE + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_AIRLINE_TABLE);
        Log.d(LOG_TAG, "Created table " + AirlineEntry.TABLE_NAME);

        final String SQL_CREATE_AIRPORT_TABLE = "CREATE TABLE " + AirportEntry.TABLE_NAME + " (" +
                AirportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AirportEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                AirportEntry.COLUMN_AIRPORT_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + AirportEntry.COLUMN_CODE + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_AIRPORT_TABLE);
        Log.d(LOG_TAG, "Created table " + AirportEntry.TABLE_NAME);

        final String SQL_CREATE_STATUS_TABLE = "CREATE TABLE " + StatusEntry.TABLE_NAME + " (" +
                StatusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StatusEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                StatusEntry.COLUMN_STATUSTEXT_EN + " TEXT NOT NULL, " +
                StatusEntry.COLUMN_STATUSTEXT_NO + " TEXT NOT NULL, " +
                " UNIQUE (" + StatusEntry.COLUMN_CODE + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_STATUS_TABLE);
        Log.d(LOG_TAG, "Created table " + StatusEntry.TABLE_NAME);

        final String SQL_CREATE_FLIGHT_TABLE = "CREATE TABLE " + FlightEntry.TABLE_NAME + " (" +
                FlightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FlightEntry.COLUMN_MY_AIRPORT + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_UNIQUE_ID + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_AIRLINE + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_FLIGHT_ID + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_DOM_INT + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_SCHEDULE_TIME + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_ARR_DEP + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_AIRPORT + " TEXT NOT NULL, " +
                FlightEntry.COLUMN_VIA_AIRPORT + " TEXT, " +
                FlightEntry.COLUMN_CHECK_IN + " TEXT, " +
                FlightEntry.COLUMN_GATE + " TEXT, " +
                FlightEntry.COLUMN_STATUS_CODE + " TEXT, " +
                FlightEntry.COLUMN_STATUS_TIME + " TEXT, " +
                FlightEntry.COLUMN_BELT + " TEXT, " +
                FlightEntry.COLUMN_DELAYED + " TEXT, " +
                //" FOREIGN KEY (" + FlightEntry.COLUMN_AIRLINE + ") REFERENCES " +
                //AirlineEntry.TABLE_NAME + " (" + AirlineEntry.COLUMN_CODE + "), " +
                //" FOREIGN KEY (" + FlightEntry.COLUMN_AIRPORT + ") REFERENCES " +
                //AirportEntry.TABLE_NAME + " (" + AirportEntry.COLUMN_CODE + "), " +
                //" FOREIGN KEY (" + FlightEntry.COLUMN_STATUS_CODE + ") REFERENCES " +
                //StatusEntry.TABLE_NAME + " (" + StatusEntry.COLUMN_CODE + "), " +
                " UNIQUE (" + FlightEntry.COLUMN_UNIQUE_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_FLIGHT_TABLE);
        Log.d(LOG_TAG, "Created table " + FlightEntry.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + FlightContract.AirlineEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FlightContract.AirportEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FlightContract.StatusEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FlightContract.FlightEntry.TABLE_NAME);
        Log.d(LOG_TAG,
            "Dropped tables due to upgrade from version " + oldVersion + " to " + newVersion);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
