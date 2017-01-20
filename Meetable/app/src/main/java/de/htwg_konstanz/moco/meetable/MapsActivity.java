package de.htwg_konstanz.moco.meetable;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;


/**
 * @author peterbencik
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //logging
    private static final String TAG = "MapsActivity";

    // Broadcast receiver for receiving status updates from the GpsPullService
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Inside broadcast reciever - onRecieve");
            if (intent.getAction().equals(GpsPullService.STATUS_REPORT_ACTION)){
                double lat,lon;
                lat = intent.getDoubleExtra(GpsPullService.STATUS_REPORT_LATITUDE,Double.POSITIVE_INFINITY);
                lon = intent.getDoubleExtra(GpsPullService.STATUS_REPORT_LONGITUDE,Double.POSITIVE_INFINITY);

                Log.i(TAG, "Friend found at:\nLatitude: " + lat + "\nLongitude: " +lon + "on refresh");
                //ask if available
                placeMarker(lat,lon);

            } else if (intent.getAction().equals(GpsPullService.STATUS_REPORT_ACTION_FAIL)){
                Log.i(TAG, "No friends found on refresh");
                noFriendsAvailableToast();
            }
        }
    };



    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLocalBroadcastReciever();

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.INTERNET},
                1);

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1);

    }

    @Override
    protected void onResume(){
        super.onResume();
        registerLocalBroadcastReciever();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        registerLocalBroadcastReciever();

        Log.i(TAG,"Checking GPS permissions");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"Requesting GPS permissions");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            //TODO: react to denied permissions
        }
        mMap.setMyLocationEnabled(true);
        LatLng konstanz = new LatLng(47.667395, 9.171689);
        mMap.addMarker(new MarkerOptions().position(konstanz).title("Your friend"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konstanz,14.0f));
    }

    private void registerLocalBroadcastReciever() {
        Log.d(TAG,"Registering broadcast reciever");
        IntentFilter filter = new IntentFilter(GpsPullService.STATUS_REPORT_ACTION);
        filter.addAction(GpsPullService.STATUS_REPORT_ACTION_FAIL);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    //called when refresh button is clicked
    public void refreshFriendsMarkers(View view){
        Log.i(TAG,"Refreshing friends markers");
        mMap.clear();
        startGpsNeighboursLookup();
    }

    private void startGpsNeighboursLookup(){
        Log.d(TAG,"Firing up GpsPullService");
        try{
            GpsPullService.startActionGetLocation(this.getBaseContext());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    private void placeMarker(double lat, double lon){
        if(lat != Double.POSITIVE_INFINITY && lon != Double.POSITIVE_INFINITY){
            LatLng friend = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(friend).title("Your friend"));
        } else {
            Log.w(TAG,"Bad data in the database: Lat: " +lat + " ,Lon: " + lon);
            noFriendsAvailableToast();
        }

    }

    private void noFriendsAvailableToast(){
        Log.i(TAG,"Notifying user abut no friend locations available");

        Context context = getApplicationContext();
        CharSequence text = "No friends found nearby";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        //TODO: small notification
    }
}
