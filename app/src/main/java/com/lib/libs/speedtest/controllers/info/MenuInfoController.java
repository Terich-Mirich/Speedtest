package com.lib.libs.speedtest.controllers.info;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lib.libs.speedtest.R;

public class MenuInfoController {

    private Activity activity;
    private ViewGroup root;

    private View mApplicationInfo;
    private View mPrivacyPolicy;
    private View mTermsOfUse;

    private ViewGroup appinfoLayout;

    InfoGroup currentGroup;

    public MenuInfoController(Activity activity, ViewGroup root) {
        this.activity = activity;
        this.root = root;
        init();
    }

    private void init (){
        appinfoLayout = ((ViewGroup)root.getParent()).findViewById(R.id.menuInfoAppinfoGroup);
        mApplicationInfo = root.findViewById(R.id.applicationInfo);
        mApplicationInfo.setOnClickListener(v -> {
            showSupportGroup(InfoGroup.APPLICATION_INFO);
        });
        mPrivacyPolicy = root.findViewById(R.id.privacyPolicy);
        mPrivacyPolicy.setOnClickListener(v -> {
            showSupportGroup(InfoGroup.PRIVACY_POLICY);
        });
        mTermsOfUse = root.findViewById(R.id.termsOfUse);
        mTermsOfUse.setOnClickListener(v -> {
            showSupportGroup(InfoGroup.TERMS_OF_USE);
        });


    }


    private void showSupportGroup(InfoGroup group){
        currentGroup = group;

        switch (group){

            case APPLICATION_INFO:
                Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
                appinfoLayout.startAnimation(animTranslateIn);
                appinfoLayout.setVisibility(View.VISIBLE);
                break;
            case PRIVACY_POLICY:
                break;
            case TERMS_OF_USE:
                break;
        }

    }

    public boolean isChildClose(){
        if (currentGroup != null){
            switch (currentGroup){
                case APPLICATION_INFO:
                    Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
                    appinfoLayout.startAnimation(animTranslateIn);
                    appinfoLayout.setVisibility(View.GONE);
                    break;
                case PRIVACY_POLICY:
                    break;
                case TERMS_OF_USE:
                    break;
            }
            currentGroup = null;
            return true;
        }else{
            return false;
        }
    }

    private enum InfoGroup {
        APPLICATION_INFO,
        PRIVACY_POLICY,
        TERMS_OF_USE,
    }

    public void show(){
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
    }

    public void hide(){
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
    }


}
