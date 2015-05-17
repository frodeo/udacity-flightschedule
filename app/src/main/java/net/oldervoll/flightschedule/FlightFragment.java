package net.oldervoll.flightschedule;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.oldervoll.flightschedule.sync.SyncAdapter;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

import static net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import static net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlightFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = FlightFragment.class.getSimpleName();

    private static final int FLIGHT_LOADER_ID = 0;
    private static final String POSITION_KEY = "position";

    private FlightAdapter flightAdapter;
    private int savedPosition = ListView.INVALID_POSITION;
    private ListView listView;
    @SuppressWarnings("unused")
    private boolean useTwoPaneLayout;

    private static final String[] FLIGHT_COLUMNS = {
        FlightEntry.TABLE_NAME + "." + FlightEntry._ID,
        FlightEntry.COLUMN_AIRLINE,
        AirlineEntry.COLUMN_AIRLINE_NAME,
        FlightEntry.COLUMN_FLIGHT_ID,
        FlightEntry.COLUMN_SCHEDULE_TIME,
        FlightEntry.COLUMN_ARR_DEP,
        FlightEntry.COLUMN_AIRPORT,
        AirportEntry.COLUMN_AIRPORT_NAME,
        FlightEntry.COLUMN_GATE,
        FlightEntry.COLUMN_STATUS_CODE,
        FlightEntry.COLUMN_STATUS_TIME
    };

    // These indices are tied to FLIGHT_COLUMNS.  If FLIGHT_COLUMNS changes, these must change.
    @SuppressWarnings("unused")
    static final int COL_INTERNAL_ID = 0;
    static final int COL_AIRLINE_CODE = 1;
    static final int COL_AIRLINE_NAME = 2;
    static final int COL_FLIGHT_ID = 3;
    static final int COL_SCHEDULE_TIME = 4;
    static final int COL_ARR_DEP = 5;
    static final int COL_AIRPORT_CODE = 6;
    static final int COL_AIRPORT_NAME = 7;
    static final int COL_GATE = 8;
    static final int COL_STATUS_CODE = 9;
    static final int COL_STATUS_TIME = 10;

    public FlightFragment() {}

    @DebugLog
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);        
    }

    @DebugLog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        flightAdapter = new FlightAdapter(getActivity(), null, 0);
        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            savedPosition = savedInstanceState.getInt(POSITION_KEY, ListView.INVALID_POSITION);
        }

        listView = (ListView) rootView.findViewById(R.id.listview_fragment);
        listView.setAdapter(flightAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Uri uri = FlightEntry.buildUriForAirportAndFlightId(
                        Utility.getMyAirport(getActivity()), cursor.getString(COL_FLIGHT_ID));
                    ((DetailCallback) getActivity()).onDetailSelected(uri);
                }
                savedPosition = position;
            }
        });
        return rootView;
    }

    @DebugLog
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FLIGHT_LOADER_ID, null, this);
    }

    @DebugLog
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (savedPosition != ListView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, savedPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @DebugLog
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.forecastfragment, menu);
    }

    @DebugLog
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();
        if (id == R.id.action_map) {
            showMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
        */return false;
    }

    private void updateFlights() {
        SyncAdapter.syncImmediately(getActivity());
    }

    @DebugLog
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String airport = Utility.getMyAirport(getActivity());
        String sortOrder = FlightEntry.COLUMN_SCHEDULE_TIME + " ASC";
        Uri uri = FlightEntry.buildUriForAirport(airport);
        Log.d(LOG_TAG, "CursorLoader uri: " + uri.toString());
        return new CursorLoader(
                getActivity(),
                uri,
                FLIGHT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @DebugLog
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        flightAdapter.swapCursor(cursor);
        Log.d(LOG_TAG, "Loaded " + (cursor != null ? cursor.getCount() : 0) + " flights");
        if (savedPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(savedPosition);
        }

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(getActivity(), getString(R.string.no_flights), Toast.LENGTH_SHORT).show();
        } else {
            List<String> flightIds = new ArrayList<>();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                flightIds.add(cursor.getString(COL_FLIGHT_ID));
            }
            Log.d(LOG_TAG, "Loaded flights: " + flightIds);
        }
    }

    @DebugLog
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        flightAdapter.swapCursor(null);
    }

    @DebugLog
    public void onAirportChanged() {
        updateFlights();
        getLoaderManager().restartLoader(FLIGHT_LOADER_ID, null, this);
    }
    
    public void setUseTwoPaneLayout(boolean useTwoPaneLayout) {
        this.useTwoPaneLayout = useTwoPaneLayout;
        if (flightAdapter != null) {
            flightAdapter.setUseTwoPaneLayout(useTwoPaneLayout);
        }
    }
}
