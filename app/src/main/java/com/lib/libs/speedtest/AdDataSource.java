package com.lib.libs.speedtest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.lib.libs.speedtest.utils.Utils;


public class AdDataSource {

    private static final String TAG = AdDataSource.class.getSimpleName();

    private static final String native_unit = "ca-app-pub-3940256099942544/2247696110";
    private static final String debug_native_unit = "ca-app-pub-3940256099942544/2247696110";

    private static final String interstitial_unit = "ca-app-pub-3940256099942544/1033173712";
    private static final String interstitial_tabs_unit = "ca-app-pub-3940256099942544/1033173712";
    private static final String debug_interstitial_unit = "ca-app-pub-3940256099942544/1033173712";

    private static final String reward_unit = "ca-app-pub-3940256099942544/5224354917";
    private static final String debug_reward_unit = "ca-app-pub-3940256099942544/5224354917";

    private static final String reward_inter_unit = "ca-app-pub-3940256099942544/6300978111";
    private static final String debug_reward_inter_unit = "ca-app-pub-3940256099942544/6300978111";

    private InterstitialAd interstitialAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private RewardedAd rewardedAd;

    private static AdDataSource instance;

    public static AdDataSource get() {
        if (instance == null)
            instance = new AdDataSource();
        return instance;
    }

    private AdDataSource() {
//        EventBus.getDefault().register(this);
    }

    //region InterstitialAd ------------------------------------------------------------------------

    /** LOAD INTERSTITIAL AD */
    public void loadInterstitial(Context context){
        if (Utils.checkPremiumWithTrial(context) || interstitialAd != null) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                getInterstitialAdUnitId(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                        Log.e(TAG, "inter onAdFailedToLoad");
                    }
                });
    }

    /** SHOW INTERSTITIAL AD */
    public boolean showInterstitial(Activity activity){
        if (Utils.checkPremiumWithTrial(activity)) return false;
        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Log.d(TAG, "The inter ad failed to show.");
                }
                @Override
                public void onAdShowedFullScreenContent() {
                    interstitialAd = null;
                    Log.d(TAG, "The inter ad was shown.");
                    if (activity != null) loadInterstitial(activity);
                }
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "The inter ad was dismissed.");
                }
            });
            interstitialAd.show(activity);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
            return false;
        }
        return true;
    }

    /** SHOW INTERSTITIAL AD WITH CALLBACK */
    public boolean showInterstitial(Activity activity, FullScreenContentCallback callback){
        if (Utils.checkPremiumWithTrial(activity)) return false;

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            Log.d(TAG, "The inter ad failed to show.");
                            callback.onAdFailedToShowFullScreenContent(adError);
                        }
                        @Override
                        public void onAdShowedFullScreenContent() {
                            interstitialAd = null;
                            callback.onAdShowedFullScreenContent();
                            Log.d(TAG, "The inter ad was shown.");
                            if (activity != null) loadInterstitial(activity);
                        }
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            callback.onAdDismissedFullScreenContent();
                            Log.d(TAG, "The inter ad was dismissed.");
                        }
                    });
            interstitialAd.show(activity);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
            return false;
        }
        return true;
    }
    //endregion -----------------------------------------------------------------------------------








    //region REWARDED INTER VIDEO -----------------------------------------------------------------

    /** LOAD STANDARD REWARDED INTER VIDEO AD */
    public void loadRewardedInterstitialAd(Context context, AdRewardCallback callback){
        if (Utils.checkPremiumWithTrial(context)) return;
        RewardedInterstitialAd.load(context, getRewardInterAdUnitId(),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        callback.onAdLoaded(null);
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.setFullScreenContentCallback(callback);
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        callback.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "rewardedInterstitialAd onAdFailedToLoad");
                    }
                });
    }

    /** SHOW STANDARD REWARDED INTER VIDEO AD */
    public boolean showRewardedInterstitialAd(Activity activity, OnUserEarnedRewardListener rewardListener){
        if (Utils.checkPremiumWithTrial(activity)) return false;
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd.show(activity, rewardListener);
        } else {
            Log.d(TAG, "The rewarded inter ad wasn't ready yet.");
            return false;
        }
        return true;
    }
    //endregion ------------------------------------------------------------------------------------








    //region REWARDED VIDEO ------------------------------------------------------------------------
    /** LOAD STANDARD REWARDED VIDEO AD */
    public void loadRewardedAd(Context context, AdRewardCallback callback){
        if (Utils.checkPremiumWithTrial(context)) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, getRewardAdUnitId(),
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        callback.onAdFailedToLoad(loadAdError);
                        Log.d(TAG, loadAdError.getMessage());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        callback.onAdLoaded(ad);
                        rewardedAd = ad;
                        rewardedAd.setFullScreenContentCallback(callback);
                        Log.d(TAG, "Ad was loaded.");
                    }
                });
    }

    /** SHOW STANDARD REWARDED VIDEO AD */
    public boolean showRewardedAd(Activity activity, OnUserEarnedRewardListener rewardListener){
        if (Utils.checkPremiumWithTrial(activity)) return false;
        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardListener);
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            return false;
        }
        return true;
    }

    //endregion ------------------------------------------------------------------------------------








    public static class AdRewardCallback extends FullScreenContentCallback {
        private RewardedAd ad;
        /** Called when the ad failed to show full screen content. */
        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            Log.i(TAG, "onAdFailedToShowFullScreenContent");
        }
        /** Called when ad showed the full screen content. */
        @Override
        public void onAdShowedFullScreenContent() {
            Log.i(TAG, "onAdShowedFullScreenContent");
            if (ad != null) ad = null;
        }
        /** Called when full screen content is dismissed. */
        @Override
        public void onAdDismissedFullScreenContent() {
            Log.i(TAG, "onAdDismissedFullScreenContent");
        }

        public void onAdFailedToLoad(LoadAdError loadAdError) {
            if (ad != null) ad = null;
            Log.d(TAG, loadAdError.getMessage());
        }

        public void onAdLoaded(RewardedAd ad) {
            this.ad = ad;
            Log.d(TAG, "Ad was loaded.");
        }
    }








    //region Native -------------------------------------------------------------------------------
    public static void populateNativeAds(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        if (nativeAd.getHeadline() == null && adView.getHeadlineView() != null) {
            adView.getHeadlineView().setVisibility(View.INVISIBLE);
        } else if (adView.getHeadlineView() != null){
            adView.getHeadlineView().setVisibility(View.VISIBLE);
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }
        if (nativeAd.getBody() == null && adView.getBodyView() != null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else if (adView.getBodyView() != null){
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null && adView.getCallToActionView() != null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else if (adView.getCallToActionView() != null) {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null && adView.getIconView() != null) {
            adView.getIconView().setVisibility(View.GONE);
        } else if (adView.getIconView() != null){
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null && adView.getAdvertiserView() != null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else if (adView.getAdvertiserView() != null){
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }
    //endregion -----------------------------------------------------------------------------------








    //region Getters Ad Units ----------------------------------------------------------------------
    public static String getNativeAdUnitId() {
        return BuildConfig.DEBUG ? debug_native_unit : native_unit;
    }

    public static String getInterstitialAdUnitId() {
        return BuildConfig.DEBUG ? debug_interstitial_unit : interstitial_unit;
    }

    public static String getInterstitialTabsAdUnitId() {
        return BuildConfig.DEBUG ? debug_interstitial_unit : interstitial_tabs_unit;
    }

    public static String getRewardAdUnitId() {
        return BuildConfig.DEBUG ? debug_reward_unit : reward_unit;
    }

    public static String getRewardInterAdUnitId() {
        return BuildConfig.DEBUG ? debug_reward_inter_unit : reward_inter_unit;
    }
    //endregion ------------------------------------------------------------------------------------
}
