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
    private ViewGroup privacyPolicy;
    private ViewGroup termsOfUse;

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
        privacyPolicy = ((ViewGroup)root.getParent()).findViewById(R.id.menuInfoPrivacyPolicyGroup);
        mPrivacyPolicy = root.findViewById(R.id.privacyPolicy);
        mPrivacyPolicy.setOnClickListener(v -> {
            showSupportGroup(InfoGroup.PRIVACY_POLICY);
        });
        termsOfUse = ((ViewGroup)root.getParent()).findViewById(R.id.menuInfoTermsOfUseGroup);
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
                Animation animTranslatePPIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
                privacyPolicy.startAnimation(animTranslatePPIn);
                privacyPolicy.setVisibility(View.VISIBLE);
                break;
            case TERMS_OF_USE:
                Animation animTranslateTOUIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
                termsOfUse.startAnimation(animTranslateTOUIn);
                termsOfUse.setVisibility(View.VISIBLE);
                break;
        }

    }

    public boolean isChildClose(){
        if (currentGroup != null){
            switch (currentGroup){
                case APPLICATION_INFO:
                    Animation animTranslateAppOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
                    appinfoLayout.startAnimation(animTranslateAppOut);
                    appinfoLayout.setVisibility(View.GONE);
                    break;
                case PRIVACY_POLICY:
                    Animation animTranslatePPOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
                    privacyPolicy.startAnimation(animTranslatePPOut);
                    privacyPolicy.setVisibility(View.GONE);
                    break;
                case TERMS_OF_USE:
                    Animation animTranslateTOUOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
                    termsOfUse.startAnimation(animTranslateTOUOut);
                    termsOfUse.setVisibility(View.GONE);
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
