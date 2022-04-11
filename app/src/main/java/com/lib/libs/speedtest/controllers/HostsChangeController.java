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
import com.lib.libs.speedtest.utils.ViewWeightAnimationWrapper;

import java.util.List;

public class HostsChangeController {

    private Activity activity;
    private List<Host> hosts;
    private HostsAdapter hostsAdapter;
    private ViewGroup root;
    private boolean hostsStatus;
    private ViewGroup mIncludeHostList;
    private ViewGroup mFrameIncludeHost;
    ViewWeightAnimationWrapper wrapper;


    public HostsChangeController(Activity activity, ViewGroup root, List<Host> hosts) {
        this.activity = activity;
        this.root = root;
        wrapper = new ViewWeightAnimationWrapper(root);
        this.hosts = hosts;
        init();
    }


    public void init (){
        RecyclerView recyclerView = root.findViewById(R.id.hostsList);
        hostsAdapter = new HostsAdapter(activity, hosts);
        recyclerView.setAdapter(hostsAdapter);
    }

    public void show(){
        ObjectAnimator animator =ObjectAnimator.ofFloat(wrapper, "weight", wrapper.getWeight(), 1);
        animator.setDuration(300);
        animator.start();
        hostsStatus = true;

    }

    public void hide(){
//        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_down);
//        ValueAnimator animator = new ValueAnimator();

        ObjectAnimator animator = ObjectAnimator.ofFloat(wrapper, "weight", wrapper.getWeight(), 0);
        animator.setDuration(300);
        animator.start();
        hostsStatus = false;
    }

    public boolean getHostStatus() {
        return hostsStatus;
    }




}
