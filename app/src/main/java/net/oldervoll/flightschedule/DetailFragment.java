package net.oldervoll.flightschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hugo.weaving.DebugLog;

import static java.lang.String.format;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int FLIGHT_LOADER_ID = 0;

    private ShareActionProvider shareActionProvider;
    private String shareStr;
    private String shareHtmlStr;
    private ViewHolder viewHolder;
    private Uri uri;

    public static final String[] FLIGHT_COLUMNS = {
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
            FlightEntry.COLUMN_STATUS_TIME,
            FlightEntry.COLUMN_DOM_INT,
            FlightEntry.COLUMN_UNIQUE_ID,
            FlightEntry.COLUMN_VIA_AIRPORT,
            FlightEntry.COLUMN_CHECK_IN,
            FlightEntry.COLUMN_BELT,
            FlightEntry.COLUMN_DELAYED,
            StatusEntry.COLUMN_STATUSTEXT_EN,
            StatusEntry.COLUMN_STATUSTEXT_NO

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
    static final int COL_DOM_INT = 11;
    @SuppressWarnings("unused")
    static final int COL_UNIQUE_ID = 12;
    static final int COL_VIA_AIRPORT = 13;
    static final int COL_CHECK_IN = 14;
    static final int COL_BELT = 15;
    static final int COL_DELAYED = 16;
    @SuppressWarnings("unused")
    static final int COL_STATUSTEXT_EN = 17;
    @SuppressWarnings("unused")
    static final int COL_STATUSTEXT_NO = 18;

    public DetailFragment() {
    }

    @DebugLog
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @DebugLog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            uri = arguments.getParcelable("uri");
        }
        View view =  inflater.inflate(R.layout.fragment_detail, container, false);
        viewHolder = new ViewHolder(view);

        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_GEO_ACTION);
        GeoReceiver geoReceiver = new GeoReceiver(viewHolder);
        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(geoReceiver, intentFilter);

        return view;
    }

    @DebugLog
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FLIGHT_LOADER_ID, null, this);
    }

    @DebugLog
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (shareActionProvider != null && shareHtmlStr != null && shareStr != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }

        String myFlight = Utility.getMyFlight(getActivity());
        String thisFlight = FlightEntry.getFlightIdFromUri(uri);
        item = menu.findItem(R.id.action_myflight);
        if (myFlight.equalsIgnoreCase(thisFlight)) {
            item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        } else {
            item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_myflight) {
            String newFlightId = FlightEntry.getFlightIdFromUri(uri);
            String currentFlightId = Utility.getMyFlight(getActivity());
            if (newFlightId.equalsIgnoreCase(currentFlightId)) {
                item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                Utility.setMyFlight(getActivity(), null);
            } else {
                item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                Utility.setMyFlight(getActivity(), newFlightId);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private Intent createShareIntent() {
        Intent intent;
        if (Utility.isRichContentSharingEnabled(getActivity())) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)viewHolder.iconView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            File cache = getActivity().getApplicationContext().getExternalCacheDir();
            File sharefile = new File(cache, "toshare.png");
            Log.d(LOG_TAG, "Share file " + sharefile.getAbsolutePath());
            try {
                FileOutputStream out = new FileOutputStream(sharefile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Writing share file failed: " + e.getMessage());
            }

            intent = ShareCompat.IntentBuilder
                    .from(getActivity())
                    .setSubject("Flight DY123")
                    .setHtmlText(shareHtmlStr)
                    .setType("text/html")
                    .addStream(Uri.parse("file:// " + sharefile))
                    .getIntent();
        } else {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareStr);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    @DebugLog
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (uri != null) {
            return new CursorLoader(
                    getActivity(),
                    uri,
                    FLIGHT_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @DebugLog
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        String airlineCode = cursor.getString(COL_AIRLINE_CODE);
        String airlineName =
            Optional.fromNullable(cursor.getString(COL_AIRLINE_NAME)).or(airlineCode);
        String flightId = cursor.getString(COL_FLIGHT_ID);
        long scheduledTime = cursor.getLong(COL_SCHEDULE_TIME);
        String day = Utility.getDayName(getActivity(), scheduledTime);
        String date = Utility.getFormattedMonthDay(getActivity(), scheduledTime);
        String arrDep = cursor.getString(COL_ARR_DEP);
        boolean isDeparture = FlightEntry.ARRDEP_D.equalsIgnoreCase(arrDep);
        String airportCode = cursor.getString(COL_AIRPORT_CODE);
        String airportName =
            Optional.fromNullable(cursor.getString(COL_AIRPORT_NAME)).or(airportCode);
        String gate = cursor.getString(COL_GATE);
        Optional<String> status = Utility.getStatus(getActivity(),
                cursor.getString(COL_STATUS_CODE),
                cursor.getLong(COL_STATUS_TIME));
        String domInt = cursor.getString(COL_DOM_INT);
        String viaAirport = cursor.getString(COL_VIA_AIRPORT);
        String checkIn = cursor.getString(COL_CHECK_IN);
        String belt = cursor.getString(COL_BELT);
        String delayed = cursor.getString(COL_DELAYED);

        viewHolder.airlineView.setText(getString(R.string.format_airline, airlineName));
        viewHolder.flightIdView.setText(flightId);
        viewHolder.dayView.setText(day);
        viewHolder.dateView.setText(date);
        int arrDepImageResource = Utility.getArtResourceForFlightArrDep(arrDep);
        int arrDepContentDesc = isDeparture ? R.string.arr_dep_D : R.string.arr_dep_A;
        if (arrDepImageResource != -1) {
            viewHolder.iconView.setImageResource(arrDepImageResource);
            viewHolder.iconView.setContentDescription(getString(arrDepContentDesc));
        }
        viewHolder.airportView.setText(airportName);

        viewHolder.gateView.setText(getString(R.string.format_gate, gate != null ? gate : ""));
        if (status.isPresent()) {
            Log.d(LOG_TAG, "Status set to: " + status.get());
            viewHolder.statusView.setText(status.get());
        }
        if (domInt != null) {
            viewHolder.domIntView.setText(
                Utility.getString(getActivity(), "dom_int_", domInt, null));
        }
        viewHolder.viaAirportView.setText(
            getString(R.string.format_viaAirport, viaAirport != null ? viaAirport : ""));
        viewHolder.checkinView.setText(
            getString(R.string.format_checkIn, checkIn != null ? checkIn : ""));
        viewHolder.beltView.setText(
            getString(R.string.format_belt, belt != null ? belt : ""));
        if (delayed != null && "Y".equalsIgnoreCase(delayed)) {
            viewHolder.delayedView.setText(getString(R.string.is_delayed));
        }

        shareStr = format("%s %s %s %s",
                Utility.getString(getActivity(), "arr_dep_", arrDep, null),
                flightId,
                airportName,
                status.isPresent() ? " - " + status.get() : "");
        shareHtmlStr = format("<html><h1>%s %s</h1><p>%s</p><p>%s</p><p>%s</p></html>",
                Utility.getString(getActivity(), "arr_dep_", arrDep, null),
                flightId,
                airportName,
                status.isPresent() ? " - " + status.get() : "",
                getActivity().getString(
                        R.string.flight_web_uri,
                        isDeparture ? Utility.getMyAirport(getActivity()) : airportCode,
                        isDeparture ? airportCode : Utility.getMyAirport(getActivity()),
                        flightId,
                        Utility.formatDate(getActivity(), scheduledTime),
                        flightId));
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }

        handleDirection(
                isDeparture ? Utility.getMyAirportName(getActivity()) : airportName,
                isDeparture ? airportName : Utility.getMyAirportName(getActivity()));

        Log.d(LOG_TAG, "Details for " + flightId + " loaded");
    }

    private void handleDirection(String fromAirport, String toAirport) {
        Intent intent = new Intent(getActivity(), GeoService.class);
        intent.putExtra(Constants.EXTRA_FROM_AIRPORT, fromAirport);
        intent.putExtra(Constants.EXTRA_TO_AIRPORT, toAirport);
        getActivity().startService(intent);
    }

    @DebugLog
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @DebugLog
    void onAirportChanged(String newAirport) {
        if (null != uri) {
            String flightId = FlightEntry.getFlightIdFromUri(uri);
            uri = FlightEntry.buildUriForAirportAndFlightId(newAirport, flightId);
            getLoaderManager().restartLoader(FLIGHT_LOADER_ID, null, this);
        }
    }

    public static class ViewHolder {
        public final TextView dayView;
        public final TextView dateView;
        public final ImageView iconView;
        public final TextView airportView;
        public final TextView flightIdView;
        public final TextView statusView;
        public final TextView airlineView;
        public final TextView domIntView;
        public final TextView delayedView;
        public final TextView viaAirportView;
        public final TextView checkinView;
        public final TextView gateView;
        public final TextView beltView;
        public final CompassView directionView;

        public ViewHolder(View view) {
            dayView = (TextView) view.findViewById(R.id.detail_day_textview);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            airportView = (TextView) view.findViewById(R.id.detail_airport_textview);
            flightIdView = (TextView) view.findViewById(R.id.detail_flight_id_textview);
            statusView = (TextView) view.findViewById(R.id.detail_status_textview);
            airlineView = (TextView) view.findViewById(R.id.detail_airline_textview);
            domIntView = (TextView) view.findViewById(R.id.detail_domInt_textview);
            delayedView = (TextView) view.findViewById(R.id.detail_delayed_textview);
            viaAirportView = (TextView) view.findViewById(R.id.detail_via_airport_textview);
            checkinView = (TextView) view.findViewById(R.id.detail_checkin_textview);
            gateView = (TextView) view.findViewById(R.id.detail_gate_textview);
            beltView = (TextView) view.findViewById(R.id.detail_belt_textview);
            directionView = (CompassView) view.findViewById(R.id.detail_direction_compassview);
        }
    }

    private static class GeoReceiver extends BroadcastReceiver {

        private ViewHolder viewHolder;

        private GeoReceiver(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            double degrees = intent.getDoubleExtra(Constants.EXTRA_DEGREES_RESULT, -1.0f);
            Log.d(LOG_TAG, "Received degrees: " + degrees);
            if (degrees != -1.0f && viewHolder.directionView != null) {
                viewHolder.directionView.setDegrees((float)degrees);
                viewHolder.directionView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.directionView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
