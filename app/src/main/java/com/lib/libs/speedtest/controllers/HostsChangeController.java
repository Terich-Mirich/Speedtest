package com.lib.libs.speedtest.controllers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
    private boolean hostsStatus;
    private ViewGroup mLinearHostChange;


    public HostsChangeController(Activity activity, ViewGroup root, List<Host> hosts) {
        this.activity = activity;
        this.root = root;
        this.hosts = hosts;
        init();
    }


    public void init (){
        RecyclerView recyclerView = root.findViewById(R.id.hostsList);

        hostsAdapter = new HostsAdapter(activity, hosts);
        recyclerView.setAdapter(hostsAdapter);
    }

    public void show(){

        Animation animTranslateUp = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_up);
        root.startAnimation(animTranslateUp);
        root.setVisibility(View.VISIBLE);
        hostsStatus = true;
    }



    public void hide(){
        mLinearHostChange = activity.findViewById(R.id.linearHostChange);
        Animation animTranslateDown = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_down);
        animTranslateDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                root.setVisibility(View.GONE);
                Animation animTranslateLinearHostChangeUp = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_host_change_up);
                mLinearHostChange.startAnimation(animTranslateLinearHostChangeUp);
                mLinearHostChange.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        root.startAnimation(animTranslateDown);
        hostsStatus = false;
    }

    public boolean getHostStatus() {
        return hostsStatus;
    }




}
