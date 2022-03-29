package com.lib.libs.speedtest.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.lib.libs.speedtest.StorageDataSource;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BillingService {

    private static final String TAG = BillingService.class.getSimpleName();
    private BillingClient billingClient;
    private Context context;
    private String skuYear = "", skuM = "";
    private HashMap<String, SkuDetails> skuDetailsSubs = new HashMap<>();
    private HashMap<String, SkuDetails> skuDetailsInApp = new HashMap<>();
    private String skuYearPrice = "NaN", skuMPrice = "NaN";
    private List<String> skuListSubs;
    private List<String> skuListInApp;
    private List<Purchase> purchasesList;

    private static BillingService instance;
    public static BillingService getInstance() {
        if (instance == null)
            instance = new BillingService();
        return instance;
    }

    public BillingService() {

    }

    public void init(Context context){
        this.context = context;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updateSku(RemoteConfigReadyEvent event){
//        InAppInitial();
//    }

    // ИНИЦИАЛИЗАЦИЯ
    private void InAppInitial(){
        skuM = "month";
        skuYear = "year";
        skuListSubs = new ArrayList<>();
        skuListSubs.add(skuM);
        skuListSubs.add(skuYear);

        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener((r, list) -> {
                    if (r.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (list != null && !list.isEmpty()){
                            StorageDataSource.get(context).addProperty(StorageDataSource.Prop.PREMIUM, true);
                            for (Purchase purchase : list) {
                                handlePurchase(purchase);
                            }
                            purchasesList = list;
                        }
//                        AppEvents.send(new OnPurchasesUpdateEvent());
                        Log.d(TAG, "PurchasesUpdatedListener");
                        Log.d(TAG, list != null ? list.toString() : "null");
                    }
                })
                .build();


        connectionToAPIBilling();
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    Log.d(TAG, "PurchasesUpdatedListener");
                });
            }
            StorageDataSource.get(context).addProperty(StorageDataSource.Prop.PREMIUM, true);
        }
    }
    //ПОЛУЧЕНИЕ ВСЕХ ПОЗИЦИЙ
    private void connectionToAPIBilling(){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult responseCode) {
                if(responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult pr = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    if (pr.getResponseCode() == BillingClient.BillingResponseCode.OK && pr.getPurchasesList() != null)
                        StorageDataSource.get(context).addProperty(StorageDataSource.Prop.PREMIUM, !pr.getPurchasesList().isEmpty());
                    querySkuDetails();
                    queryPurchases(); //запрос о покупках

                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected");
            }
        });
    }


    // ПОКУПКА
    public void onConnectToBuy(Activity activity, String sku){
        SkuDetails details = skuDetailsSubs.get(sku);
        if (details == null) return;
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(details)
                .build();
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    //ОБНОВЛЕНИЕ ДАННЫХ О ПОКУПКАХ
    public void onPurchasesUpdated(BillingResult responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "onPurchasesUpdated");
            Log.d(TAG, purchases.toString());
            //сюда мы попадем когда будет осуществлена покупка
//            takePremium();
        }
    }


    // ЗАПРОС О ПОКУПКАХ
    private void queryPurchases() {
       
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (r, list) -> {
            if (r.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                StorageDataSource.get(context).addProperty(StorageDataSource.Prop.PREMIUM, !list.isEmpty());
                for (Purchase purchase : list) {
                    handlePurchase(purchase);
                }
                Log.d(TAG, "billingClient.queryPurchasesAsync SUBS");
                Log.d(TAG, list.toString());
            }
        });
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (r, list) -> {
            if (r.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                StorageDataSource.get(context).addProperty(StorageDataSource.Prop.PREMIUM, !list.isEmpty());
                Log.d(TAG, "billingClient.queryPurchasesAsync INAPP");
                Log.d(TAG, list.toString());
            }
        });
    }
    private List<Purchase> getPurchases(String[] skus, String skuType) {
        Purchase.PurchasesResult pr = billingClient.queryPurchases(skuType);
        BillingResult br = pr.getBillingResult();
        List<Purchase> returnPurchasesList = new LinkedList<>();
        if (br.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Problem getting purchases: " + br.getDebugMessage());
        } else {
            List<Purchase> purchasesList = pr.getPurchasesList();
            if (null != purchasesList) {
                for (Purchase purchase : purchasesList) {
                    for (String sku : skus) {
                        for (String purchaseSku : purchase.getSkus()) {
                            if (purchaseSku.equals(sku)) {
                                if ( !returnPurchasesList.contains(purchase) ) {
                                    returnPurchasesList.add(purchase);
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnPurchasesList;
    }
    private void querySkuDetails() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuListSubs).setType(BillingClient.SkuType.SUBS);

        billingClient.querySkuDetailsAsync(params.build(), (responseCode, skuDetailsList) -> {
            if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails d : skuDetailsList){
                    skuDetailsSubs.put(d.getSku(), d);
                }
            }
        });

        SkuDetailsParams.Builder paramsInApp = SkuDetailsParams.newBuilder();
        paramsInApp.setSkusList(skuListInApp).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(paramsInApp.build(), (responseCode, skuDetailsList) -> {
            if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails d : skuDetailsList){
                    skuDetailsInApp.put(d.getSku(), d);
                }
            }
        });
    }

    public boolean isAnySubActive(){
        if (purchasesList == null) return false;
        return true;
//        for (Purchase purchase : purchasesList) {
//
//        }
    }

    public SkuDetails getSubDetails(String sku){
        return skuDetailsSubs.get(sku);
    }


}
