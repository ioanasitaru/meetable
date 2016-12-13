package de.htwg_konstanz.moco.meetable;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class GpsPullService extends IntentService {

    //constants for status report
    public static final String  STATUS_REPORT_ACTION = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_ACTION";
    public static final String STATUS_REPORT_LATITUDE = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_LATITUDE";
    public static final String STATUS_REPORT_LONGITUDE = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_LONGITUDE";

    private static final String ACTION_GET_LOCATION = "de.htwg_konstanz.moco.meetable.action.ACTION_GET_LOCATION";

    private double latitude, longitude;

    public GpsPullService() {
        super("GpsPullService");
    }

    /**
     * Starts this service to perform action GetLocation with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGetLocation(Context context) {
        Intent intent = new Intent(context, GpsPullService.class);
        intent.setAction(ACTION_GET_LOCATION);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LOCATION.equals(action)) {
                handleActionGetLocation();
            }
        }
    }


    /**
     * Handle action GetLocation in the provided background thread
     */
    private void handleActionGetLocation() {
        // TODO: Get actual data from server
        if (true) throw new UnsupportedOperationException("Not yet implemented");

        reportStatus();


    }

    private void reportStatus(){
        Intent localIntent = new Intent(STATUS_REPORT_ACTION);
        localIntent.putExtra(STATUS_REPORT_LATITUDE,latitude);
        localIntent.putExtra(STATUS_REPORT_LONGITUDE,longitude);

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

}
