package com.lib.libs.speedtest.controllers;


import androidx.fragment.app.FragmentActivity;

import com.lib.libs.speedtest.ui.RateAppDialog;


public class RateDialogController {

    private static boolean isShown;
    private static final String tag = "rate_dialog";

    public static void show(FragmentActivity activity) {
//        if ((StorageDataSource.get(activity).getProperty(StorageDataSource.Prop.RATE_DONE, false) || isShown) && !BuildConfig.DEBUG){
//            return;
//        }
        if (activity.getSupportFragmentManager().findFragmentByTag(tag) != null) return;
        new RateAppDialog().show(activity.getSupportFragmentManager(), tag);
        isShown = true;
    }




}
