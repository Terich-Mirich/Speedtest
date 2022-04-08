package com.lib.libs.speedtest.controllers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.lib.libs.speedtest.Host;
import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.adapters.HostsAdapter;

import java.util.List;

public class HostsChangeController {

    private Activity activity;
    private List<Host> hosts;
    private HostsAdapter hostsAdapter;
    private ViewGroup root;

    public HostsChangeController(Activity activity, ViewGroup root) {
        this.activity = activity;
        this.root = root;
        this.hosts = hosts;
        init();
    }


    public void init (){
        RecyclerView recyclerView = root.findViewById(R.id.list);
        hostsAdapter = new HostsAdapter(activity, hosts);
        recyclerView.setAdapter(hostsAdapter);
    }

    public void show(){
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_up);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
    }

    public void hide(){
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_down);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
    }




}
