package com.lib.libs.speedtest.controllers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lib.libs.speedtest.R;

public class MenuController {

    private Activity activity;
    private ViewGroup root;

    private boolean menuStatus;

    private View mCloseMenu;
    private View mMenuFrameBackground;

    private View historyButton;
    private View infoButton;
    private View settingsButton;
    private View supportButton;



    public MenuController(Activity activity, ViewGroup root) {
        this.activity = activity;
        this.root = root;
        init();
    }

    private void init(){
        mMenuFrameBackground = ((ViewGroup)root.getParent()).findViewById(R.id.menuFrameBackground);
        mCloseMenu = root.findViewById(R.id.closeMenu);
        mCloseMenu.setOnClickListener(v ->{
            closeMenu();
        });
        initMainMenu();

    }

    private void initMainMenu(){
        historyButton = root.findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.HISTORY);
//            Intent myIntent = new Intent(this, HistoryActivity.class);
//            startActivity(myIntent);
        });
        infoButton = root.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.INFO);
        });
        settingsButton = root.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.SETTINGS);
        });
        supportButton = root.findViewById(R.id.supportButton);
        supportButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.SUPPORT);
        });
    }

    public void openMenu(){
        Animation animFadeIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fade_in);
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
        mMenuFrameBackground.startAnimation(animFadeIn);
        mMenuFrameBackground.setVisibility(View.VISIBLE);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
        menuStatus = true;
    }

    public void closeMenu(){
        Animation animFadeOut = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_out);
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        mMenuFrameBackground.startAnimation(animFadeOut);
        mMenuFrameBackground.setVisibility(View.GONE);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
        menuStatus = false;
    }

    private void showMenuGroup(MenuGroup group){
        switch (group){

            case HISTORY:
                break;
            case INFO:
                break;
            case SETTINGS:
                break;
            case SUPPORT:
                break;
        }
    }

    public boolean getMenuStatus() {
        return menuStatus;
    }

    private enum MenuGroup {
        HISTORY,
        INFO,
        SETTINGS,
        SUPPORT
    }
}


