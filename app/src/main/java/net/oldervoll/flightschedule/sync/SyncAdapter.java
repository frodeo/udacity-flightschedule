package net.oldervoll.flightschedule.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.MainActivity;
import net.oldervoll.flightschedule.R;
import net.oldervoll.flightschedule.Utility;
import net.oldervoll.flightschedule.data.ContentValuesMapper;
import net.oldervoll.flightschedule.data.FlightContract.AirlineEntry;
import net.oldervoll.flightschedule.data.FlightContract.AirportEntry;
import net.oldervoll.flightschedule.data.FlightContract.FlightEntry;
import net.oldervoll.flightschedule.data.FlightContract.StatusEntry;
import net.oldervoll.flightschedule.model.AirlineNames;
import net.oldervoll.flightschedule.model.Airport;
import net.oldervoll.flightschedule.model.AirportNames;
import net.oldervoll.flightschedule.model.Flight;
import net.oldervoll.flightschedule.model.FlightStatuses;
import net.oldervoll.flightschedule.model.Status;
import net.oldervoll.flightschedule.remote.AvinorService;
import net.oldervoll.flightschedule.remote.AvinorServiceImpl;

import org.joda.time.DateTime;
import org.joda.time.Period;

import static java.lang.String.format;


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();

    private final AvinorService avinorService = new AvinorServiceImpl();
    private final ContentResolver contentResolver;

    public static final int SYNC_INTERVAL = 60 * 3; // 3 minutes
    public static final int SYNC_FLEXTIME = 60; // 1 minute

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int FLIGHT_NOTIFICATION_ID = 1001;
    
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        String airport = Utility.getMyAirport(getContext());
        Log.d(LOG_TAG, "FlightSchedule service fetching flight data for " + airport);

        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastSyncMetadataKey = context.getString(R.string.last_sync_meta);
        long lastSyncMetadata = prefs.getLong(lastSyncMetadataKey, 0);

        int countAirlines = 0;
        int countAirports = 0;
        int countStatuses = 0;
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(AirlineEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                countAirlines = cursor.getCount();
                cursor.close();
                cursor = null;
            }

            cursor = contentResolver.query(AirportEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                countAirports = cursor.getCount();
                cursor.close();
                cursor = null;
            }

            cursor = contentResolver.query(StatusEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                countStatuses = cursor.getCount();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if ((System.currentTimeMillis() - lastSyncMetadata >= DAY_IN_MILLIS)
                || (countAirports == 0)
                || (countAirlines == 0)
                || (countStatuses == 0)) {
            long currentSyncMetadata = System.currentTimeMillis();

            AirlineNames airlineNames = avinorService.getAirlineNames();
            if (airlineNames != null && !airlineNames.isEmpty()) {
                // Delete old data
                int deleted = contentResolver.delete(AirlineEntry.CONTENT_URI, null, null);
                Log.i(LOG_TAG, "Deleted " + deleted + " airline names from content provider");
                // Insert new data
                int inserted = contentResolver.bulkInsert(AirlineEntry.CONTENT_URI,
                    ContentValuesMapper.map(airlineNames));
                Log.i(LOG_TAG, "Inserted " + inserted + " airline names to content provider");
            } else {
                currentSyncMetadata = 0; // Force a new reload at next sync
            }

            AirportNames airportNames = avinorService.getAirportNames();
            if (airportNames != null && !airportNames.isEmpty()) {
                // Delete old data
                int deleted = contentResolver.delete(AirportEntry.CONTENT_URI, null, null);
                Log.i(LOG_TAG, "Deleted " + deleted + " airport names from content provider");
                // Insert new data
                int inserted = contentResolver.bulkInsert(AirportEntry.CONTENT_URI,
                        ContentValuesMapper.map(airportNames));
                Log.i(LOG_TAG, "Inserted " + inserted + " airport names to content provider");
            } else {
                currentSyncMetadata = 0; // Force a new reload at next sync
            }

            FlightStatuses flightStatuses = avinorService.getFlightStatuses();
            if (flightStatuses!= null && !flightStatuses.isEmpty()) {
                // Delete old data
                int deleted = contentResolver.delete(StatusEntry.CONTENT_URI, null, null);
                Log.i(LOG_TAG, "Deleted " + deleted + " statuses from content provider");
                // Insert new data
                int inserted = contentResolver.bulkInsert(StatusEntry.CONTENT_URI,
                        ContentValuesMapper.map(flightStatuses));
                Log.i(LOG_TAG, "Inserted " + inserted + " statuses to content provider");
            } else {
                currentSyncMetadata = 0; // Force a new reload at next sync
            }

            // Refreshing last sync
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastSyncMetadataKey, currentSyncMetadata);
            editor.apply();
        }

        String myAirport = Utility.getMyAirport(context);
        Airport flights = avinorService.getFlights(myAirport);
        if (flights!= null && !flights.isEmpty()) {
            int inserted = contentResolver.bulkInsert(FlightEntry.CONTENT_URI,
                    ContentValuesMapper.map(myAirport, flights));
            Log.i(LOG_TAG, "Inserted " + inserted + " flights to content provider");

            // Delete old data
            int numRowsDeleted = getContext().getContentResolver().delete(
                    FlightEntry.CONTENT_URI,
                    FlightEntry.COLUMN_SCHEDULE_TIME + "<= ?",
                    new String[] { Long.toString(System.currentTimeMillis() - DAY_IN_MILLIS)});
            Log.i(LOG_TAG, "Deleted " + numRowsDeleted + " old rows");

            Optional<Flight> flightOptional = flights.getFlight(
                    Utility.getMyFlight(context), Optional.of(DateTime.now().minusMinutes(30)));
            if (flightOptional.isPresent()) {
                notifyFlight(flightOptional.get());
            } else {
                Utility.setMyFlight(context, null);
            }
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * Called externally
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Typically called from main activity in order to initialize the sync adapter and the account
     * @param context the context
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
 
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
 
        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private void notifyFlight(Flight flight) {
        if (flight != null) {
            Log.d(LOG_TAG, "Calculating notification for " + flight.getFlight_id());
            Context context = getContext();
            if (Utility.isNotificationsEnabled(context)) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String lastNotifTimePref = context.getString(R.string.pref_notification_last_time);
                String lastNotifKeyPref = context.getString(R.string.pref_notification_last_key);
                long lastNotifTime = prefs.getLong(lastNotifTimePref, 0);
                String lastNotifKey = prefs.getString(lastNotifKeyPref, null);
                Period departurePeriod = Period.minutes(
                    Integer.parseInt(context.getString(R.string.notify_before_departure)));
                DateTime now = DateTime.now();

                boolean notifyBeforeDeparture = flight.isDeparture()
                        && !flight.isCancelled()
                        && flight.getEffectiveScheduleTime().minus(departurePeriod).isBeforeNow();

                Optional<Status> status = flight.getStatus();
                Optional<DateTime> statusTime = status.isPresent() ?
                    status.get().getTime() : Optional.<DateTime>absent();

                boolean notifyStatusChange = status.isPresent()
                        && (statusTime.isPresent() && statusTime.get().isAfter(lastNotifTime) ||
                        !status.get().getKey().equalsIgnoreCase(lastNotifKey));

                if (notifyBeforeDeparture || notifyStatusChange) {
                    int iconId = Utility.getIconResourceForFlightArrDep(flight.getArr_dep());
                    String statusString = Utility.getStatus(context, flight).or("");
                    String title = format("%s %s", flight.getFlight_id(), statusString);
                    String contentText = notifyStatusChange ?
                        context.getString(R.string.notification_status,
                                flight.getFlight_id(),
                                flight.getAirport(),
                                statusString) :
                        context.getString(R.string.notification_departure,
                                    flight.getFlight_id(),
                                    flight.getAirport(),
                                    Utility.formatDateTime(context,
                                            flight.getEffectiveScheduleTime().getMillis()));

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                            .setSmallIcon(iconId)
                            .setContentTitle(title)
                            .setContentText(contentText)
                            .setAutoCancel(true);
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(resultPendingIntent);

                    Notification notification = builder.build();
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;

                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(FLIGHT_NOTIFICATION_ID, notification);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotifTimePref, now.getMillis());
                    editor.putString(lastNotifKeyPref, status.get().getKey());
                    editor.apply();
                }
            }
        }
    }
}