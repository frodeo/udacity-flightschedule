package net.oldervoll.flightschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.model.Flight;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

import static net.oldervoll.flightschedule.data.FlightContract.FlightEntry;

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getMyAirport(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String airport = prefs.getString(
                context.getString(R.string.pref_airport_key),
                context.getString(R.string.pref_airport_default));
        Log.d(LOG_TAG, "Fetched my airport " + airport + " from context " + context);
        return airport;
    }

    public static void setMyAirport(Context context, String airport) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_airport_key), airport);
        editor.apply();
        Log.d(LOG_TAG, "Saved my airport " + airport + " to context " + context);
    }

    public static int getMyAirportIndex(Context context) {
        String airport = getMyAirport(context);
        String[] codes = context.getResources().getStringArray(R.array.pref_airport_codes);
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equalsIgnoreCase(airport)) {
                return i;
            }
        }
        return 0;
    }

    public static String getMyAirportName(Context context) {
        int index = getMyAirportIndex(context);
        return context.getResources().getStringArray(R.array.pref_airport_names)[index];
    }

    public static String getMyFlight(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String flightId = prefs.getString(
                context.getString(R.string.pref_flight_key),
                context.getString(R.string.pref_flight_default));
        Log.d(LOG_TAG, "Fetched my flight " + flightId + " from context " + context);
        return flightId;
    }

    public static void setMyFlight(Context context, String flightId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_flight_key), flightId);
        editor.apply();
        if (flightId != null) {
            Log.d(LOG_TAG, "Saved my flight " + flightId);
        } else {
            Log.d(LOG_TAG, "Removed my flight");
        }
    }


    public static boolean isNotificationsEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_notification_key),
                Boolean.parseBoolean(context.getString(R.string.pref_notification_default)));
    }

    public static boolean isRichContentSharingEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_sharerich_key),
                Boolean.parseBoolean(context.getString(R.string.pref_sharerich_default)));
    }

    public static String formatDateTime(Context context, long millis) {
        return new DateTime(millis).toString(context.getString(R.string.timeformat_short));
    }

    public static String formatDate(Context context, long millis) {
        return new DateTime(millis).toString(context.getString(R.string.dateformat));
    }

    public static String getDayName(Context context, long dateInMillis) {
        DateTime now = DateTime.now();
        DateTime date = new DateTime(dateInMillis);
        Interval today = new Interval(now.withTimeAtStartOfDay(), Days.ONE);
        Interval tomorrow = new Interval(now.plusDays(1).withTimeAtStartOfDay(), Days.ONE);
        Interval yesterday = new Interval(now.minusDays(1).withTimeAtStartOfDay(), Days.ONE);

        if (today.contains(date)) {
            return context.getString(R.string.today);
        } else if (tomorrow.contains(date)) {
            return context.getString(R.string.tomorrow);
        } else if (yesterday.contains(date)) {
            return context.getString(R.string.yesterday);
        } else {
            return Utility.getString(context, "day_", Integer.toString(date.getDayOfWeek()), null);
        }
    }

    public static String getFormattedMonthDay(Context context, long dateInMillis) {
        return new DateTime(dateInMillis).toString(context.getString(R.string.timeformat_long));
    }

    public static Optional<String> getStatus(Context context, Flight flight) {
        if (flight != null && flight.getStatus().isPresent()) {
            long statusTime = flight.getStatus().get().getTime().isPresent() ?
                flight.getStatus().get().getTime().get().getMillis() : 0;
            return getStatus(context, flight.getStatus().get().getCode(), statusTime);
        } else {
            return Optional.absent();
        }
    }

    public static Optional<String> getStatus(Context context, String statusCode, long statusTime) {
        int resourceId = getStringIdentifier(context, "status_", statusCode, null);
        if (resourceId == 0) {
            return Optional.absent();
        } else {
            String status = context.getString(resourceId);
            if (statusTime != 0) {
                status = status + " " + Utility.formatDateTime(context, statusTime);
            }
            return Optional.of(status);
        }
    }

    public static int getIconResourceForFlightArrDep(String arrDep) {
        if (FlightEntry.ARRDEP_A.equalsIgnoreCase(arrDep)) {
            return R.drawable.ic_arrival;
        } else if (FlightEntry.ARRDEP_D.equalsIgnoreCase(arrDep)) {
            return R.drawable.ic_departure;
        }
        return -1;
    }

    public static int getArtResourceForFlightArrDep(String arrDep) {
        if (FlightEntry.ARRDEP_A.equalsIgnoreCase(arrDep)) {
            return R.drawable.art_arrival;
        } else if (FlightEntry.ARRDEP_D.equalsIgnoreCase(arrDep)) {
            return R.drawable.art_departure;
        }
        return -1;
    }

    public static String getString(Context context,
                                   String prefix,
                                   String key,
                                   String suffix) {
        int id = getStringIdentifier(context, prefix, key, suffix);
        if (id == 0) {
            return null;
        } else {
            return context.getString(id);
        }
    }

    private static int getStringIdentifier(Context context,
                                           String prefix,
                                           String key,
                                           String suffix) {
        if (key == null || key.length() == 0) {
            return 0;
        }
        String id = (prefix != null ? prefix : "") + key + (suffix != null ? suffix : "");
        return  context
                .getResources()
                .getIdentifier(id, "string", context.getApplicationInfo().packageName);
    }
}
