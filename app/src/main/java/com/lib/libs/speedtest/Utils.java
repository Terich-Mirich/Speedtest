package com.lib.libs.speedtest;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
//    public static String networkType() {
//
//        if (Connectivity.isConnectedWifi(this)) {
//            mConnectivity.setText("WIFI");
//        } else if (Connectivity.isConnectedMobile(this)) {
//            mConnectivity.setText("mmm");
//        }
//    }


//    public String getNetworkClass(Context context) {
//        TelephonyManager mTelephonyManager = (TelephonyManager)
//                context.getSystemService(Context.TELEPHONY_SERVICE);
//        int networkType = mTelephonyManager.getNetworkType();
//        switch (networkType) {
//            case TelephonyManager.NETWORK_TYPE_GPRS:
//            case TelephonyManager.NETWORK_TYPE_EDGE:
//            case TelephonyManager.NETWORK_TYPE_CDMA:
//            case TelephonyManager.NETWORK_TYPE_1xRTT:
//            case TelephonyManager.NETWORK_TYPE_IDEN:
//                return "2G";
//            case TelephonyManager.NETWORK_TYPE_UMTS:
//            case TelephonyManager.NETWORK_TYPE_EVDO_0:
//            case TelephonyManager.NETWORK_TYPE_EVDO_A:
//            case TelephonyManager.NETWORK_TYPE_HSDPA:
//            case TelephonyManager.NETWORK_TYPE_HSUPA:
//            case TelephonyManager.NETWORK_TYPE_HSPA:
//            case TelephonyManager.NETWORK_TYPE_EVDO_B:
//            case TelephonyManager.NETWORK_TYPE_EHRPD:
//            case TelephonyManager.NETWORK_TYPE_HSPAP:
//                return "3G";
//            case TelephonyManager.NETWORK_TYPE_LTE:
//                return "4G";
//            case TelephonyManager.NETWORK_TYPE_NR:
//                return "5G";
//            default:
//                return "Unknown";
//        }
//    }
}
