package de.htwg_konstanz.moco.meetable;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    private Button button_friendscoordonates;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_friendscoordonates=(Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView2);
        if(!runtimePermissions()) {
            enableButtons();
            Intent i= new Intent(getApplicationContext(),GPSPushService.class);
            startService(i);
        }
    }
    private void enableButtons() {
        button_friendscoordonates.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // i need the PullService for enabling the button
                Intent i2= new Intent(getApplicationContext(),GPSPullService.class);
                startService(i2);
            }
        });
    }
    private boolean runtimePermissions(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100 );
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enableButtons();
            }
            else
                runtimePermissions();
        }
    }
}