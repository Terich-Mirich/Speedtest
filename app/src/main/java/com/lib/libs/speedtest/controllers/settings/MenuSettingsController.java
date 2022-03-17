package com.lib.libs.speedtest.controllers.settings;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.controllers.MenuController;

public class MenuSettingsController {

    private Activity activity;
    private ViewGroup root;
    private MenuController mainMenu;

    private TextView mRemoveADSButton;

    public MenuSettingsController(Activity activity, MenuController mainMenu, ViewGroup root) {
        this.activity = activity;
        this.mainMenu = mainMenu;
        this.root = root;
        init();
    }

    private void init (){
        mRemoveADSButton = root.findViewById(R.id.removeADSButton);
        mRemoveADSButton.setOnClickListener(v -> {
            removeADS();
        });
    }

    private void removeADS(){

    }

    public void show(){
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.translate_in);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
        mainMenu.setTitleNameMenu(activity.getString(R.string.menu_settings_title));
    }

    public void hide(){
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
    }

}
