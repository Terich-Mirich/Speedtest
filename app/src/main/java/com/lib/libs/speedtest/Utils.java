package com.lib.libs.speedtest;

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

}
