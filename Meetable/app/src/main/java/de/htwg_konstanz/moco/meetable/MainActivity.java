package de.htwg_konstanz.moco.meetable;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GpsPullService.STATUS_REPORT_ACTION)){
                System.out.println("Latitude: " + intent.getStringExtra(GpsPullService.STATUS_REPORT_LATITUDE)+
                        "\nLongitude: " +intent.getStringExtra(GpsPullService.STATUS_REPORT_LONGITUDE));
            }
        }
    }

    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Starting up service");
        IntentFilter filter = new IntentFilter(GpsPullService.STATUS_REPORT_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.INTERNET},
                1);

        ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        1);



        try{
            //GpsPullService.startActionGetLocation(getApplicationContext());
            GpsPullService.startActionGetLocation(this.getBaseContext());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }


    }
}
