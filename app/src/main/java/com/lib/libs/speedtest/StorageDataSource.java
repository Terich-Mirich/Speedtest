package com.lib.libs.speedtest;


import android.content.Context;
import android.content.SharedPreferences;


public class StorageDataSource {

    private static final String STORAGE_NAME = "THERMO";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    public static StorageDataSource get(Context c) {
        return new StorageDataSource(c);
    }

    private StorageDataSource(Context _context){
        settings = _context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.apply();
    }


    public void addProperty(Prop name, boolean value) {
        editor.putBoolean(name.val, value);
        editor.apply();
    }

    public void addProperty(Prop name, Long value) {
        editor.putLong(name.val, value);
        editor.apply();
    }

    public void addProperty(Prop name, String value) {
        editor.putString(name.val, value);
        editor.apply();
    }

    public void addProperty(Prop name, int value) {
        editor.putInt(name.val, value);
        editor.apply();
    }

    public boolean getProperty(Prop name, boolean def) {
        boolean b = def;
        try { b = settings.getBoolean(name.val, def); } catch (ClassCastException i) {
            i.printStackTrace();
            deleteProperty(name);
        }
        return b;
    }

    public Long getPropertyLong(Prop name, long def) {
        long b = def;
        try { b = settings.getLong(name.val, def); } catch (ClassCastException i) {
            i.printStackTrace();
            deleteProperty(name);
        }
        return b;
    }


    public String getPropertyString(Prop name, String def) {
        String b = def;
        try { b = settings.getString(name.val, def); } catch (ClassCastException i) {
            i.printStackTrace();
            deleteProperty(name);
        }
        return b;
    }

    public int getPropertyInt(Prop name, int def) {
        int b = def;
        try { b = settings.getInt(name.val, def); } catch (ClassCastException i) {
            i.printStackTrace();
            deleteProperty(name);
        }
        return b;
    }

    public void deleteProperty(Prop name) {
        editor.remove(name.val);
        editor.apply();
    }

    public enum Prop {
        RATE_DONE("rate_done"),
        PREMIUM("premium"),
        PREMIUM_MONTHLY("premium_monthly"),
        PREMIUM_YEARLY("premium_yearly"),
        PREMIUM_TRIAL("premium_trial"),
        ONBOARD_VIEWED("onboard_viewed"),
        LAUNCH_COUNTER("launch_counter");

        String val;

        Prop(String val) {
            this.val = val;
        }
    }

}
