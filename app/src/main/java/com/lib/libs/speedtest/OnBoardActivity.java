package com.lib.libs.speedtest;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.SkuDetails;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.lib.libs.speedtest.utils.BillingService;
import com.lib.libs.speedtest.utils.Utils;


public class OnBoardActivity extends AppCompatActivity {

    private static String TAG = "ONBOARD_SCREEN";

    private ViewGroup root;

    private Button nextButton;

    private String yearId;
    private String monthId;

    private String skuYearPrice = "...";
    private String skuMPrice = "...";

    private boolean singleIntent;

    private boolean subScreenWillShown = true;
    private boolean showAdInTheEnd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        yearId = "year";
        monthId = "month";

        root = findViewById(R.id.onboard_root);

        singleIntent = getIntent().getBooleanExtra("single", false);
        StorageDataSource.get(this).addProperty(StorageDataSource.Prop.ONBOARD_VIEWED, true);

        AdDataSource.get().loadInterstitial(this);



        if (singleIntent){
            if (Utils.checkPremium(this)) {
                start();
            } else {
                setOnBoardSubsScreen();
            }
        }else {
            setOnBoardOneV2();
        }
    }


    @Override
    public void onBackPressed(){
        if (singleIntent) finish();
    }


    private void setOnBoardOneV2(){
        setOnBoardScreen(R.layout.onboard_1, "onboard_1");;

//        View termFront = root.findViewById(R.id.onboard_1_image_t_cold);
//        YoYo.with(Techniques.FadeIn)
//                .repeatMode(ValueAnimator.REVERSE)
//                .delay(400)
//                .repeat(YoYo.INFINITE)
//                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
//                .duration(2000)
//                .playOn(termFront);

        nextButton.setOnClickListener(view -> setOnBoardTwoV2());
    }



    private void setOnBoardTwoV2(){
        setOnBoardScreen(R.layout.onboard_2, "onboard_2");

        nextButton.setOnClickListener(view -> {
            if (Utils.checkPremium(this) || !subScreenWillShown) {
                start();
            }else{
                setOnBoardSubsScreen();
            }
        });
    }


    private TextView mTextPrice;
    private TextView yTextPrice;
    private boolean closeAfterPurchase = false;
    private boolean isSubSelected = true;
    private View previousScreen;

    private void setOnBoardSubsScreen(){
        setOnBoardScreen(R.layout.onboard_3, "onboard_3");
        TextView mTextDesc, yTextDesc;
        if(singleIntent){
            View d = findViewById(R.id.progress_dots);
            d.setVisibility(View.GONE);
        }

        SkuDetails mDetails = BillingService.getInstance().getSubDetails(monthId);
        SkuDetails yDetails = BillingService.getInstance().getSubDetails(yearId);
        if (mDetails == null || yDetails == null) {
            start();
            return;
        }
        mTextDesc = findViewById(R.id.onboard_3_price_1);
        yTextDesc = findViewById(R.id.onboard_3_price_2);
        mTextPrice = findViewById(R.id.onboard_3_1_price_m);
        mTextPrice.setText(getString(R.string.onboard_3_price_1_1, mDetails.getPrice()));
        yTextPrice = findViewById(R.id.onboard_3_1_price_y);
        yTextPrice.setText(getString(R.string.onboard_3_price_2_1, yDetails.getPrice()));
        View mSub = findViewById(R.id.onboard_three_days_sub);
        View ySub = findViewById(R.id.onboard_seven_days_sub);
        isSubSelected = false;
        mSub.setOnClickListener(view -> {
            if (view.getTag() == null || !view.getTag().equals("selected")){
                isSubSelected = true;
                view.setTag("selected");
                view.setBackgroundResource(R.drawable.onbrd_sub_selected);
                ySub.setTag("unselected");
                ySub.setBackgroundResource(R.drawable.onbrd_sub_unselected);
            }
        });

        ySub.setOnClickListener(view -> {
            if (view.getTag() == null || !view.getTag().equals("selected")){
                isSubSelected = false;
                view.setTag("selected");
                view.setBackgroundResource(R.drawable.onbrd_sub_selected);
                mSub.setTag("unselected");
                mSub.setBackgroundResource(R.drawable.onbrd_sub_unselected);
            }
        });

        closeAfterPurchase = true;

        nextButton.setOnClickListener(view -> {
            if (isSubSelected) {
                BillingService.getInstance().onConnectToBuy(this, monthId);
            }else{
                BillingService.getInstance().onConnectToBuy(this, yearId);
            }
        });
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void purchasesUpdate(OnPurchasesUpdateEvent event){
//        if (closeAfterPurchase && BillingService.getInstance().isAnySubActive()) start();
//    }


    private void setOnBoardScreen(int res, String tag){
        clearRoot();
        if (previousScreen == null){
            View current = getLayoutInflater().inflate(res, null);
            root.addView(current);
            current.bringToFront();
            setOnCloseListener(current);
            initNextButton(current);
            previousScreen = current;
        }else{
            View current = getLayoutInflater().inflate(res, null);
            root.addView(current);
            current.bringToFront();
            setOnCloseListener(root.findViewWithTag(tag));
            initNextButton(root.findViewWithTag(tag));
            previousScreen = current;
        }
    }


    private void initNextButton(View current){
        nextButton = current.findViewById(R.id.onboard_button_next);
    }

    private void clearRoot(){
        root.removeAllViews();
    }

    private void setOnCloseListener(View current){
        current.findViewById(R.id.onboard_button_close).setOnClickListener(view -> {
            Log.d(TAG, "On CLOSE click");
            start();
        });
    }

    private void start() {
        if (singleIntent){
            finish();
            return;
        }

        if (showAdInTheEnd) {
            if (
                    !AdDataSource.get().showInterstitial(
                    this,
                    new FullScreenContentCallback() {
                        @Override public void onAdDismissedFullScreenContent() { startMainScreen(); }
                        @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) { startMainScreen(); }
                    })
            )
            {
                startMainScreen();
            };
        }else{
            startMainScreen();
        }
    }

    private void startMainScreen(){
        startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
        finish();
    }



}
