package com.lib.libs.speedtest;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.lib.libs.speedtest.utils.DevSettings;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
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
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) { startMainScreen(); }
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
        startActivity(i);
        finish();
    }

}