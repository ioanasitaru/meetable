package de.htwg_konstanz.moco.meetable;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class GpsPullService extends IntentService {

    private static final String ACTION_GET_LOCATION = "de.htwg_konstanz.moco.meetable.action.ACTION_GET_LOCATION";

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
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
