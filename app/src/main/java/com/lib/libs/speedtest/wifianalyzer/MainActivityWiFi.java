package com.lib.libs.speedtest.wifianalyzer;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lib.libs.speedtest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivityWiFi extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wifi);

        Switch mySwitch = (Switch) findViewById(R.id.switch1);

        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled())
            mySwitch.setChecked(true);
        else
            mySwitch.setChecked(false);

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                WifiManager wifi;

                if(isChecked){
                    wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);
                }else{
                    wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);
                }

            }
        });

        findWifiStrength();
        updateStrength();
    }

    public void updateStrength() {

        final long time = 7000;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findWifiStrength();
                    }
                });
            }
        }, time, time);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityWiFi.this);

            builder.setTitle(R.string.action_settings);
            // builder.setIcon(R.drawable.ic_info);
            builder.setMessage(R.string.dialog_text);
            builder.setCancelable(true);
            builder.setPositiveButton("Okay", null);

            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findWifiStrength() {


        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int dbm = wifiInfo.getRssi();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showWifiDetails(View view) {

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        // Create a fake list of properties.
        ArrayList<Properties> properties = new ArrayList<>();
        properties.add(new Properties("SSID",wifiInfo.getSSID()));
        properties.add(new Properties("Bars", ""+level));
        properties.add(new Properties("Frequency",wifiInfo.getFrequency()+" MHz"));
        properties.add(new Properties("Link Speed",wifiInfo.getLinkSpeed()+" Mbps"));
        properties.add(new Properties("IP Address",ipAddress));
        //properties.add(new Properties("MAC Address",wifiInfo.getMacAddress()));

        // Create a new {@link ArrayAdapter} of properties


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityWiFi.this);
       // builder.setIcon(R.drawable.icon_wifi);
        builder.setTitle("Wi-Fi Properties");

        builder.setNegativeButton("Cancel",null);
        builder.show();

    }

    public void clearData(View vew) {
        File file = new File(getFilesDir(), "WiFi");
        file.delete();
        Toast.makeText(getApplicationContext(),"File cleared",Toast.LENGTH_LONG).show();
    }
}