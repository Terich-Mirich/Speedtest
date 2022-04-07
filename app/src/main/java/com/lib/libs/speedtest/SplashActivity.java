package com.lib.libs.speedtest;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.lib.libs.speedtest.test.PingTest;
import com.lib.libs.speedtest.utils.DevSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
//TODO: (RDS) import com.sweetvrn.therm.data.RemoteDataSource;

@SuppressLint("NonConstantResourceId")
public class SplashActivity extends AppCompatActivity {

    //TODO: (RDS)
//    static {
//        RemoteDataSource.get();
//    }


    private boolean rc_onboard_enable = true;

    private boolean rc_ad_after_loading_enable = true;

    private boolean firstStart = false;

    private View mSplashLoadingLayout;
    private View mSplashLoadingDoneLayout;
    private View mContinueBtn;


    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    private AtomicInteger executedTasksCount;
    private ArrayList<Host> hosts = new ArrayList<>();
//    private ArrayMap<String, Double> pongs = new ArrayMap<>();

    private synchronized void put(Host host){
        // заменить на втыкание Array лист
        hosts.add(host);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
        executedTasksCount = new AtomicInteger(0);
        tempBlackList = new HashSet<>();
        searchOptimalServer();
        //TODO: (RDS)
        // remoteDataUpdate(null);

        mSplashLoadingLayout = findViewById(R.id.splash_loading_layout);
        mSplashLoadingDoneLayout = findViewById(R.id.splash_loading_done_layout);
        mContinueBtn = findViewById(R.id.continue_btn);

//        StorageDataSource.get(this).addProperty(StorageDataSource.Prop.ONBOARD_VIEWED, false);

        int launchCount = StorageDataSource.get(this).getPropertyInt(StorageDataSource.Prop.LAUNCH_COUNTER, 0) + 1;
        StorageDataSource.get(this).addProperty(StorageDataSource.Prop.LAUNCH_COUNTER, launchCount);

        firstStart = launchCount <= 1 || DevSettings.FIRST_START_FOREVER;

        mSplashLoadingDoneLayout.setVisibility(View.GONE);
        mSplashLoadingDoneLayout.setAlpha(0f);
        mContinueBtn.setVisibility(View.GONE);
        mContinueBtn.setAlpha(0f);
        mContinueBtn.setOnClickListener(v -> {
            if (!rc_ad_after_loading_enable) {
                startMainScreen();
                return;
            }
            boolean showing = AdDataSource.get().showInterstitial(this,
                    new FullScreenContentCallback() {
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) { }
                        @Override public void onAdShowedFullScreenContent() { }
                        @Override public void onAdDismissedFullScreenContent() { startMainScreen(); }
                    });
            if (!showing)  {
                startMainScreen();
            }
        });
        if (rc_ad_after_loading_enable) AdDataSource.get().loadInterstitial(this);
    }


//TODO: (RDS)
//    @Subscribe
//    public void remoteDataUpdate(RemoteConfigReadyEvent event){
//        rc_onboard_enable = RemoteDataSource.get().getLongValue(RC_SHOW_ONBOARD_KEY) == 1;
//        rc_ad_after_loading_enable = RemoteDataSource.get().getLongValue(RC_SHOW_AD_AFTER_SPLASH_KEY) == 1;
//    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Handler h = new Handler();
        h.postDelayed(() -> {
            playEndAnimation();
            if ((firstStart || !StorageDataSource.get(this).getProperty(StorageDataSource.Prop.ONBOARD_VIEWED, false)) && rc_onboard_enable ){
                startOnBoarding();
            }
        }, 4000);

    }

    private void playEndAnimation(){
        mSplashLoadingLayout
                .animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mSplashLoadingLayout != null) mSplashLoadingLayout.setVisibility(View.GONE);
                        if (mSplashLoadingDoneLayout != null) {
                            mSplashLoadingDoneLayout.setVisibility(View.VISIBLE);
                            mSplashLoadingDoneLayout
                                    .animate()
                                    .alpha(1f)
                                    .setDuration(500);
                        }

                    }
                });
        if (firstStart && rc_onboard_enable) return;
        mContinueBtn.setVisibility(View.VISIBLE);
        mContinueBtn.animate()
                .alpha(1f)
                .setStartDelay(1000)
                .setDuration(500);
    }

    private void startOnBoarding(){
        Intent intent = new Intent(getApplicationContext(), OnBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMainScreen(){
        Intent i = new Intent(this, MainActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)hosts);
        i.putExtra("BUNDLE",args);

        startActivity(i);
        finish();
    }

    private void searchOptimalServer(){
        AsyncTask.execute(()->{
            while (!getSpeedTestHostsHandler.isFinished()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Find closest server
            HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
            HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
            int findServerIndex = 0;
            for ( int i = 0; i < mapKey.size(); i++) {
                Host host = new Host();
                host.setId(i);
                String pingAddr = mapValue.get(i).get(6).replace(":8080", "");
                String upAddr = mapKey.get(i).replace("http://", "https://");
                String dwArrd = upAddr.replace(upAddr.split("/")[upAddr.split("/").length - 1], "");
                String longitudeCoordinates = mapValue.get(i).get(0);
                String latitudeCoordinates = mapValue.get(i).get(1);
                String cityHost = mapValue.get(i).get(2);
                String countryHost = mapValue.get(i).get(3);
                String providerHost = mapValue.get(i).get(5);

                host.setHost(pingAddr);
                host.setUpLoadAddress(upAddr);
                host.setDownLoadAddress(dwArrd);
                host.setLongitudeCoordinates(longitudeCoordinates);
                host.setLatitudeCoordinates(latitudeCoordinates);
                host.setCityHost(cityHost);
                host.setCountryHost(countryHost);
                host.setProviderHost(providerHost);
                int taskId = i + 1;
                startTask(taskId, host);
            }
            System.out.println("");
        });

    }

    private void startTask ( int taskId, Host url){
        TestTask task = new TestTask(taskId, url);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class TestTask extends AsyncTask<Void, Void, Void> {

        private final int id;
        private Host h;


        TestTask(int id, Host h) {
            this.id = id;
            this.h = h;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int taskExecutionNumber = executedTasksCount.incrementAndGet();
            log("doInBackground: entered, taskExecutionNumber = " + taskExecutionNumber);
            PingTest ping = new PingTest(h, 2, (host) -> put(host));
            ping.start();
            log("doInBackground: is about to finish, taskExecutionNumber = " + taskExecutionNumber);
            return null;
        }

        private void log(String msg) {
            Log.d("TestTask #" + id, msg);
        }
    }

}