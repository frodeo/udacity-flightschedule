/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oldervoll.flightschedule.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

import static java.lang.String.format;

public class FlightProvider extends ContentProvider {

    public static final String LOG_TAG = FlightProvider.class.getSimpleName();

    public static final String AIRLINE_SELECTION =
            format("%s.%s = ?", AirlineEntry.TABLE_NAME, AirlineEntry.COLUMN_CODE);
    public static final String AIRPORT_SELECTION =
            format("%s.%s = ?", AirportEntry.TABLE_NAME, AirportEntry.COLUMN_CODE);
    public static final String STATUS_SELECTION =
            format("%s.%s = ?", StatusEntry.TABLE_NAME, StatusEntry.COLUMN_CODE);
    public static final String FLIGHT_SELECTION =
            format("%s.%s = ?", FlightEntry.TABLE_NAME, FlightEntry.COLUMN_FLIGHT_ID);
    public static final String FLIGHT_MY_AIRPORT_SELECTION =
            format("%s.%s = ? AND %s.%s >= ?",
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_MY_AIRPORT,
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_SCHEDULE_TIME);
    public static final String FLIGHT_ARR_DEP_SELECTION  =
            format("%s.%s = ? AND %s.%s >= ?",
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_ARR_DEP,
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_SCHEDULE_TIME);
    public static final String FLIGHT_MY_AIRPORT_AND_ARR_DEP_SELECTION =
            format("%s.%s = ? AND %s.%s = ? AND %s.%s >= ?",
                    FlightEntry.TABLE_NAME, FlightEntry.COLUMN_MY_AIRPORT,
                    FlightEntry.TABLE_NAME, FlightEntry.COLUMN_ARR_DEP,
                    FlightEntry.TABLE_NAME, FlightEntry.COLUMN_SCHEDULE_TIME);
    public static final String FLIGHT_MY_AIRPORT_AND_FLIGHT_ID_SELECTION  =
            format("%s.%s = ? AND %s.%s = ?",
                    FlightEntry.TABLE_NAME, FlightEntry.COLUMN_MY_AIRPORT,
                    FlightEntry.TABLE_NAME, FlightEntry.COLUMN_FLIGHT_ID);
    static final int AIRLINE = 100;
    static final int AIRPORT = 200;
    static final int STATUS = 300;
    static final int FLIGHT = 400;
    static final int FLIGHT_FOR_AIRPORT = 401;
    static final int FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID = 402;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder flightQueryBuilder;
    static{
        flightQueryBuilder = new SQLiteQueryBuilder();
        flightQueryBuilder.setTables(
            FlightEntry.TABLE_NAME
            + format(" LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                AirlineEntry.TABLE_NAME,
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_AIRLINE,
                AirlineEntry.TABLE_NAME, AirlineEntry.COLUMN_CODE)
            + format(" LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                AirportEntry.TABLE_NAME,
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_AIRPORT,
                AirportEntry.TABLE_NAME, AirportEntry.COLUMN_CODE)
            + format(" LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                StatusEntry.TABLE_NAME,
                FlightEntry.TABLE_NAME, FlightEntry.COLUMN_STATUS_CODE,
                StatusEntry.TABLE_NAME, StatusEntry.COLUMN_CODE)
        );
    }
    private FlightDbHelper dbHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_AIRLINE, AIRLINE);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_AIRPORT, AIRPORT);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_STATUS, STATUS);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_FLIGHT, FLIGHT);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_FLIGHT + "/*",
                FLIGHT_FOR_AIRPORT);
        uriMatcher.addURI(FlightContract.CONTENT_AUTHORITY, FlightContract.PATH_FLIGHT + "/*/*",
                FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID);
        return uriMatcher;
    }

    private Cursor getFlights(Uri uri,
                              String[] projection,
                              String selection,
                              String[] selectionArgs,
                              String sortOrder) {
        String myAirport = FlightEntry.getAirportFromUri(uri);
        String arrDep = FlightEntry.getArrDepFromUri(uri);
        long startDate = System.currentTimeMillis(); // Consider getting this from uri or selection

        String calculatedSelection = selection;
        String[] calculatedSelectionArgs = selectionArgs;

        if (myAirport != null && arrDep != null) {
            calculatedSelection = FLIGHT_MY_AIRPORT_AND_ARR_DEP_SELECTION;
            calculatedSelectionArgs = new String[] { myAirport, arrDep, Long.toString(startDate) };
        } else if (myAirport != null) {
            calculatedSelection = FLIGHT_MY_AIRPORT_SELECTION;
            calculatedSelectionArgs = new String[] { myAirport, Long.toString(startDate) };
        } else if (arrDep != null) {
            calculatedSelection = FLIGHT_ARR_DEP_SELECTION;
            calculatedSelectionArgs = new String[] { arrDep, Long.toString(startDate) };
        }

        return flightQueryBuilder.query(
                dbHelper.getReadableDatabase(),
                projection,
                calculatedSelection,
                calculatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFlight(Uri uri,
                             String[] projection,
                             String selection,
                             String[] selectionArgs,
                             String sortOrder) {
        String myAirport = FlightEntry.getAirportFromUri(uri);
        String flightId = FlightEntry.getFlightIdFromUri(uri);

        String calculatedSelection = selection;
        String[] calculatedSelectionArgs = selectionArgs;

        if (myAirport != null && flightId != null) {
            calculatedSelection = FLIGHT_MY_AIRPORT_AND_FLIGHT_ID_SELECTION;
            calculatedSelectionArgs = new String[] { myAirport, flightId };
        }

        return flightQueryBuilder.query(
                dbHelper.getReadableDatabase(),
                projection,
                calculatedSelection,
                calculatedSelectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FlightDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case AIRLINE:
                return AirlineEntry.CONTENT_TYPE;
            case AIRPORT:
                return AirportEntry.CONTENT_TYPE;
            case STATUS:
                return StatusEntry.CONTENT_TYPE;
            case FLIGHT:
                return FlightEntry.CONTENT_TYPE;
            case FLIGHT_FOR_AIRPORT:
                return FlightEntry.CONTENT_TYPE;
            case FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID:
                return FlightEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor returnCursor;
        switch (uriMatcher.match(uri)) {
            case AIRLINE: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        AirlineEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, 
                        sortOrder);
                break;
            }
            case AIRPORT: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        AirportEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case STATUS: {
                returnCursor = dbHelper.getReadableDatabase().query(
                        StatusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FLIGHT: {
                returnCursor = getFlights(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case FLIGHT_FOR_AIRPORT: {
                returnCursor = getFlights(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case FLIGHT_FOR_AIRPORT_AND_FLIGHT_ID: {
                returnCursor = getFlight(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(LOG_TAG, "Query " + uri + " returned " + returnCursor.getCount() + " elements");
        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case AIRLINE: {
                long _id = db.insert(FlightContract.AirlineEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FlightContract.AirlineEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case AIRPORT: {
                long _id = db.insert(FlightContract.AirportEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FlightContract.AirportEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STATUS: {
                long _id = db.insert(FlightContract.StatusEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FlightContract.StatusEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FLIGHT: {
                long _id = db.insert(FlightContract.FlightEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FlightContract.FlightEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        
        switch (match) {
            case AIRLINE:
                rowsDeleted = db.delete(FlightContract.AirlineEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AIRPORT:
                rowsDeleted = db.delete(FlightContract.AirportEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STATUS:
                rowsDeleted = db.delete(FlightContract.StatusEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FLIGHT:
                rowsDeleted = db.delete(FlightContract.FlightEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        if (rowsDeleted != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        final int match = uriMatcher.match(uri);

        switch (match) {
            case AIRLINE:
                rowsUpdated = db.update(AirlineEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AIRPORT:
                rowsUpdated = db.update(AirportEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STATUS:
                rowsUpdated = db.update(StatusEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FLIGHT:
                rowsUpdated = db.update(FlightEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        String tableName;
        switch (match) {
            case AIRLINE:
                tableName = FlightContract.AirlineEntry.TABLE_NAME;
                break;
            case AIRPORT:
                tableName = FlightContract.AirportEntry.TABLE_NAME;
                break;
            case STATUS:
                tableName = FlightContract.StatusEntry.TABLE_NAME;
                break;
            case FLIGHT:
                tableName = FlightContract.FlightEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }
}
