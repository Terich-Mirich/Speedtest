package com.lib.libs.speedtest.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.lib.libs.speedtest.StorageDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {

    public static float mbits(BigDecimal bit) {
        return bit.floatValue() / 1024 / 1024;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    public static boolean checkPremium(Context context) {
        boolean premium = StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM, false);
        if (StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM_MONTHLY, false)) {
            premium = true;
        }
        if (StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM_YEARLY, false)) {
            return true;
        }
        return DevSettings.PREMIUM || premium;
    }

    public static boolean checkPremiumWithTrial(Context context) {
        boolean premium = StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM, false);
        if (StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM_MONTHLY, false)) {
            premium = true;
        }
        if (StorageDataSource.get(context).getProperty(StorageDataSource.Prop.PREMIUM_YEARLY, false)) {
            return true;
        }
        //TODO: RemoteDataSource хз что сним придется делать (RDS)
//        if (
//                System.currentTimeMillis()
//                        -  StorageDataSource.get(context).getPropertyLong(StorageDataSource.Prop.PREMIUM_TRIAL, 0L)
//                        <  minToMs(RemoteDataSource.get().getLongValue(RemoteDataSource.RCParams.RC_TRIAL_TiME_KEY))
//        )
//        {
//            return true;
//        }
        return DevSettings.PREMIUM || premium;
    }

    public static long minToMs(long mins) {
        return mins * 60 * 1000;
    }

    /**
     * Find all views with (string) tags matching the given pattern.
     * Collection<View> itemNameViews = findViewsByTag(v, "^item_name_[0-9]+$");
     */
    public static Collection<View> findViewsByTag(View root, String tagPattern) {
        List<View> views = new ArrayList<>();
        final Object tagObj = root.getTag();
        if (tagObj instanceof String) {
            String tagString = (String) tagObj;
            if (tagString.matches(tagPattern)) {
                views.add(root);
            }
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                views.addAll(findViewsByTag(vg.getChildAt(i), tagPattern));
            }
        }
        return views;
    }

    public static Map<String, Double> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Double> retMap = new HashMap<String, Double>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Double> toMap(JSONObject object) throws JSONException {
        Map<String, Double> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            if (object.get(key) instanceof Integer){
                continue;
            }
            Double value = (Double) object.get(key);

            map.put(key, value);
        }
        return map;
    }


}
