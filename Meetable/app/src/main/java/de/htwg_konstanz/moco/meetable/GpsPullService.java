package de.htwg_konstanz.moco.meetable;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class GpsPullService extends IntentService {

    //constants for status report
    public static final String STATUS_REPORT_ACTION = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_ACTION";
    public static final String STATUS_REPORT_LATITUDE = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_LATITUDE";
    public static final String STATUS_REPORT_LONGITUDE = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_LONGITUDE";
    public static final String STATUS_REPORT_ACTION_FAIL = "de.htwg_konstanz.moco.meetable.action.STATUS_REPORT_ACTION_FAIL";

    //constant for action intent
    private static final String ACTION_GET_LOCATION = "de.htwg_konstanz.moco.meetable.action.ACTION_GET_LOCATION";

    //database access
    private static final String DATABASE_ACCESS = "https://meetable2.herokuapp.com/";
    private static final String DATABASE_GET_LOCATION_METHOD = "getGPS";
    private static final int DATABASE_AREA_IN_KM = 1;

    private class Position {

        public Position(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        private double latitude, longitude;


        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location !=null) {
                setMyLocation(location);
            }
        }

        //required by interface
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    }

    Position myPosition;
    Position otherPhonePosition;


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
        System.out.println("Inside helper method");
        Intent intent = new Intent(context, GpsPullService.class);
        intent.setAction(ACTION_GET_LOCATION);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Inside service");
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
        determineMyPosition();
        String jsonString = accessDatabase(); //could be null, deal with it
        if (jsonString == null){
            reportFail();
            return;
        }
        try {
            parseJson(jsonString);
        } catch (JSONException ex){
            reportFail();
            return;
        }
        reportStatus();
    }

    private void determineMyPosition() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            reportFail();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            setMyLocation(location);
        } else {
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 50, locationListener);
            Location locationx = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    private void setMyLocation(Location location){
        myPosition = new Position(location.getLatitude(),location.getLongitude());
    }

    private String accessDatabase() {
        URL accessUrl = composeDatabaseAccessUrl();
        try{
            HttpsURLConnection urlConnection = (HttpsURLConnection) accessUrl.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException ex) {
            return null;
        }


    }

    private URL composeDatabaseAccessUrl(){
        String url = DATABASE_ACCESS +
                    DATABASE_GET_LOCATION_METHOD +
                    "/" + myPosition.getLatitude() +
                    "/" + myPosition.getLongitude() +
                    "/" + DATABASE_AREA_IN_KM;
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    private void parseJson(String jsonString) throws JSONException {
        JSONArray array = new JSONArray(jsonString);
        JSONArray internalArray = array.getJSONArray(0);
        JSONObject object = internalArray.getJSONObject(0);
        otherPhonePosition = new Position(0,0);
        otherPhonePosition.setLatitude(object.getDouble("latitude"));
        otherPhonePosition.setLongitude(object.getDouble("longitude"));
    }

    /**
     * This approach used for reporting status:
     * https://developer.android.com/training/run-background-service/report-status.html
     * "Receive Status Broadcasts from an IntentService" chapter is important for activity
     */
    private void reportStatus() {
        Intent localIntent = new Intent(STATUS_REPORT_ACTION);
        localIntent.putExtra(STATUS_REPORT_LATITUDE, myPosition.getLatitude());
        localIntent.putExtra(STATUS_REPORT_LONGITUDE, myPosition.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        System.out.println("Lat:" + otherPhonePosition.getLatitude() + "\nLon: " + otherPhonePosition.getLongitude());
    }

    private void reportFail(){
        Intent localIntent = new Intent(STATUS_REPORT_ACTION_FAIL);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        System.out.println(STATUS_REPORT_ACTION_FAIL + ": Getlocation failed");
    }
}
