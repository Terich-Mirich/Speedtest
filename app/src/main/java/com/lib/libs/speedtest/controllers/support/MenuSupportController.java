package com.lib.libs.speedtest.controllers.support;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.fragment.app.FragmentActivity;

import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.controllers.MenuController;
import com.lib.libs.speedtest.controllers.RateDialogController;

public class MenuSupportController {

    private Activity activity;
    private ViewGroup root;
    private MenuController mainMenu;

    private View mFeedbackButton;
    private View mReviewButton;




    public MenuSupportController(Activity activity, MenuController mainMenu, ViewGroup root) {
        this.activity = activity;
        this.root = root;
        this.mainMenu = mainMenu;
        init();
    }

    private void init (){
        mFeedbackButton = root.findViewById(R.id.feedbackButton);
        mFeedbackButton.setOnClickListener(v -> {
            showFeedbackDialog();
            mainMenu.closeMenu(true);
        });
        mReviewButton = root.findViewById(R.id.reviewButton);
        mReviewButton.setOnClickListener(v -> {
            showReviewDialog();
            mainMenu.closeMenu(true);
        });
    }

    private void showFeedbackDialog(){
        try {
            Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
            selectorIntent.setData(Uri.parse("mailto:"));
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Speedtest feedback");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {activity.getString(R.string.developer_mail)});
            emailIntent.setSelector(selectorIntent);
//                    emailIntent.setType("message/rfc822");
            activity.startActivity(emailIntent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showReviewDialog() {
        RateDialogController.show((FragmentActivity) activity);
    }

    public void show(){
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.translate_in);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
        mainMenu.setTitleNameMenu(activity.getString(R.string.menu_support_title));
    }

    public void hide(){
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
    }

}
