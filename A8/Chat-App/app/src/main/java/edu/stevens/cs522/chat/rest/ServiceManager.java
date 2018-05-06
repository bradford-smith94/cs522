package edu.stevens.cs522.chat.rest;

/**
 * Created by dduggan.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;
import java.util.UUID;

import edu.stevens.cs522.chat.settings.Settings;

/**
 * SYNC: Used to manage background synchronization of message databases
 */
public class ServiceManager {

    // Interval between syncs in ms (would be best to make it configurable)
    public static final int SYNC_INTERVAL = 10 * 1000;

    private final static String TAG = ServiceManager.class.getCanonicalName();

	/*
	 * This library is used by the UI to manage the background services.  They will typically be
	 * scheduled using the AlarmManager, based on parameters specified in the ConfigInfo preferences.
	 */

    private Context context;

    private Random rand;

    private PendingIntent syncTrigger;

    private static final int SYNC_REQUEST = 1;

    /*
     * Credentials required for scheduling services.
     */
    public ServiceManager(Context context) {
        this.context = context;
        this.rand = new Random();
    }

    private PendingIntent createSyncTrigger() {
        if (syncTrigger == null) {
            Intent intent = new Intent(context, RequestService.class);
            long senderId = Settings.getSenderId(context);
            UUID clientId = Settings.getClientId(context);
            intent.putExtra(RequestService.SERVICE_REQUEST_KEY, new SynchronizeRequest(senderId, clientId));
            syncTrigger = PendingIntent.getService(context, SYNC_REQUEST, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        return syncTrigger;
    }

    private void scheduleRepeating(PendingIntent trigger, int interval) {
        Log.d(TAG, "Scheduling repeating alarm with interval " + interval);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, trigger);
    }

    public void scheduleSyncRepeating(int interval) {
        Log.d(TAG, "Scheduling repeating sync with interval: " + interval);
        scheduleRepeating(createSyncTrigger(), interval);
    }

    private void cancelAlarm(AlarmManager am, PendingIntent trigger) {
        try {
            am.cancel(trigger);
        } catch (Exception e) {
            Log.w(TAG, "Trying to cancel alarm that is not set.", e);
        }
    }

    private void cancel(PendingIntent trigger) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cancelAlarm(am, trigger);
    }

    public void cancelSync() {
        Log.d(TAG, "Canceling sync alarm.");
        cancel(createSyncTrigger());
    }

    public void scheduleBackgroundOperations() {
        Log.d(TAG, "Scheduling background operations");
        scheduleSyncRepeating(rand.nextInt(SYNC_INTERVAL));
    }

    public void cancelBackgroundOperations() {
        Log.d(TAG, "Canceling background operations");
        cancelSync();
    }

    /**
     * Query if the device is currently charging (from mains or USB).
     *
     * @param context
     * @return
     */
    public static boolean isCharging(Context context) {
        Intent stickyBroadcast = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return isCharging(stickyBroadcast);
    }

    public static boolean isCharging(Intent batteryStatusBroadcast) {
        int status = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                (status == BatteryManager.BATTERY_STATUS_FULL);
    }

    /**
     * Get the current battery level.
     *
     * @param context
     * @return
     */
    public static float getBatteryLevel(Context context) {
        Intent batteryStatusBroadcast = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return getBatteryLevel(batteryStatusBroadcast);
    }

    public static float getBatteryLevel(Intent batteryStatusBroadcast) {
        int level = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return level / (float) scale;
    }

}
