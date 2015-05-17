package net.oldervoll.flightschedule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import net.oldervoll.flightschedule.sync.SyncAdapter;


public class MainActivity
        extends ActionBarActivity
        implements DetailCallback, ActionBar.OnNavigationListener {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String LOG_TAG = FlightFragment.class.getSimpleName();

    private String selectedAirport;
    private boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SyncAdapter.initializeSyncAdapter(this);

        selectedAirport = Utility.getMyAirport(this);
        if (findViewById(R.id.flight_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            isTwoPane = true;
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flight_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
            }

        } else {
            isTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        FlightFragment flightFragment = (FlightFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment_flight);
        flightFragment.setUseTwoPaneLayout(isTwoPane);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
                getSupportActionBar().getThemedContext(),
                R.array.pref_airport_names,
                R.layout.support_simple_spinner_dropdown_item);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);
        actionBar.setSelectedNavigationItem(Utility.getMyAirportIndex(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAirport();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetailSelected(Uri uri) {
        if (isTwoPane) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable("uri", uri);
            detailFragment.setArguments(args);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flight_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(uri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPostion, long itemId) {
        String[] codes = getResources().getStringArray(R.array.pref_airport_codes);
        Log.i(LOG_TAG, "Selected airport " + codes[itemPostion]);
        Utility.setMyAirport(this, codes[itemPostion]);
        loadAirport();
        return true;
    }

    private void loadAirport() {
        String airport = Utility.getMyAirport(this);
        // Update the location in our second pane using the fragment manager
        if (airport != null && !airport.equals(selectedAirport)) {
            FlightFragment flightFragment = (FlightFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_flight);
            if (flightFragment != null) {
                flightFragment.onAirportChanged();
            }
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if (detailFragment != null) {
                detailFragment.onAirportChanged(airport);
            }
            selectedAirport = airport;

            ActionBar actionBar = getSupportActionBar();
            actionBar.setSelectedNavigationItem(Utility.getMyAirportIndex(this));
        }
    }
}
