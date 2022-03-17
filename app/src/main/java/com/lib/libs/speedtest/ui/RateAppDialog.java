package com.lib.libs.speedtest.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.views.RatingView;

public class RateAppDialog extends DialogFragment {


    private RatingView ratingView;
    private View v1;
    private View v1_1;
    private View v1_review;
    private View root;
    private ImageView smile;
    private boolean isReview;

    public RateAppDialog() {
    }



    @Override
    public void onStart() {
        super.onStart();
        setCancelable(true);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme()){
            @Override
            public void onBackPressed() {
                onBack();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.dialog_rate_v1, viewGroup, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() != null) {
            root = view;
            v1 = view.findViewById(R.id.v1);
            v1_1 = view.findViewById(R.id.v1_1);
            v1_review = view.findViewById(R.id.v1_review);
            View close = view.findViewById(R.id.dialog_rate_v1_btn_close);
            View btnYes = v1.findViewById(R.id.dialog_rate_v1_btn_yes);
            View btnNo = v1.findViewById(R.id.dialog_rate_v1_btn_no);
            close.setOnClickListener(v -> dismiss());
            btnNo.setOnClickListener(v -> {
                openReviewDialog(true);
                //StorageDataSource.get(getActivity()).addProperty(StorageDataSource.Prop.RATE_DONE, true);
            });
            btnYes.setOnClickListener(v -> initRatingViews());
        }
    }

    private void initRatingViews() {
        isReview = false;
        v1.setVisibility(View.GONE);
        v1_review.setVisibility(View.GONE);
        View target = v1_1;
        target.setVisibility(View.VISIBLE);
        smile = v1_1.findViewById(R.id.dialog_rate_v3_smile_image);
        Button rate = target.findViewById(R.id.dialog_rate_v1_rate_button);
        ratingView = target.findViewById(R.id.ratingBar);
        ratingView.setRatingListener((ratingGiven, totalRating) -> {
            rate.setText(R.string.rate_app_text_5);
            changeSmile(ratingGiven);
            if (ratingGiven == 5) {
                rate.setText(R.string.rate_app_text_6);
            }
        });
        rate.setOnClickListener(view1 -> {
            if (getActivity() != null && ratingView != null) {
                if (ratingView.getRating() == 0) return;
                if (ratingView.getRating() == 5) {
                    openPlayStoreForApp();
                } else {
                    openReviewDialog(false);
                }
               // StorageDataSource.get(getActivity()).addProperty(StorageDataSource.Prop.RATE_DONE, true);
            }
        });
    }

    public void openPlayStoreForApp() {
        if (getActivity() == null) return;
        try {
            Bundle params = new Bundle();
           // FirebaseAnalytics.getInstance(getActivity()).logEvent("rate_open_play_store", params);
        }catch (Exception e){
            e.printStackTrace();
        }
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)).addFlags(FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)).addFlags(FLAG_ACTIVITY_NEW_TASK));
        }
        dismiss();
    }

    private void onBack(){
        if (isReview){
            View back = root.findViewById(R.id.dialog_rate_v1_btn_back);
            back.setVisibility(View.GONE);
            initRatingViews();
        }
    }

    public void openReviewDialog(boolean withoutBack){
        if (getActivity() == null) return;
        isReview = true;
        v1.setVisibility(View.GONE);
        v1_1.setVisibility(View.GONE);
        v1_review.setVisibility(View.VISIBLE);
        View back = root.findViewById(R.id.dialog_rate_v1_btn_back);
        if (!withoutBack)
            back.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> {
            back.setVisibility(View.GONE);
            initRatingViews();
        });
        EditText reviewText = v1_review.findViewById(R.id.dialog_rate_v1_review_text);
        reviewText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        reviewText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        View sendBtn = v1_review.findViewById(R.id.dialog_rate_v1_send_button);
        sendBtn.setOnClickListener(v -> {
            if (reviewText.getText().length() > 3){
                try {
                    Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                    selectorIntent.setData(Uri.parse("mailto:"));
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Review");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, reviewText.getText().toString());
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.developer_mail)});
                    emailIntent.setSelector(selectorIntent);
//                    emailIntent.setType("message/rfc822");
                    startActivity(emailIntent);
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    private void changeSmile(int rating){
        switch (rating){
            case 0:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_0, null));
                break;
            case 1:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_1, null));
                break;
            case 2:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_2, null));
                break;
            case 3:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_3, null));
                break;
            case 4:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_4, null));
                break;
            case 5:
                smile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rate_dialog_v3_2_5, null));
        }
    }
}
