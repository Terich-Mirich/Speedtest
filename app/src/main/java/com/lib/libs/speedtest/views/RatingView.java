package com.lib.libs.speedtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;


import androidx.annotation.RequiresApi;

import com.lib.libs.speedtest.R;


public class RatingView extends LinearLayout {

    private boolean ratingOngoing = false;

    private Context mContext;

    private OnRateListener onRateListener;
    private boolean mRatingAllowed;
    private int mNumStars = 5;
    private int mRating = 0;
    private boolean mDisable = false;

    public RatingView(Context context) {
        super(context);

        init(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        this.mContext = context;

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        setIsRatingAllowed(true);

        final TypedArray styleAttributesArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView);
        mNumStars = styleAttributesArray.getInteger(R.styleable.RatingView_numStars, mNumStars);
        mRating = styleAttributesArray.getInteger(R.styleable.RatingView_rating, mRating);
        mDisable = styleAttributesArray.getBoolean(R.styleable.RatingView_disable, mDisable);
        styleAttributesArray.recycle();

        addRatingStars();
        updateViewAfterRatingChanged(mRating-1);
    }

    private void addRatingStars() {
        if(mNumStars!=0){
            for (int i = 0; i < mNumStars; i++) {
                addView(getRatingView());
            }
        }
    }

    private CheckBox getRatingView(){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.rating_star_item_v1, this, false);
        checkBox.setId(getChildCount());
        checkBox.setOnCheckedChangeListener(onCheckChangeListener);
        if (mDisable) checkBox.setClickable(false);
        return checkBox;
    }

    public void setRatingListener(OnRateListener onRateListener){
        this.onRateListener = onRateListener;
    }

    private final CompoundButton.OnCheckedChangeListener onCheckChangeListener = (buttonView, isChecked) -> {
        if(ratingOngoing){
            return;
        }
        if(buttonView.getId() >= 0 && buttonView.getId() <= getChildCount()-1){
            updateViewAfterRatingChanged(buttonView.getId());
        }
    };

    private void updateViewAfterRatingChanged(int checkedPosition) {
        this.mRating = checkedPosition+1;
        ratingOngoing = true;
        if(checkedPosition < getChildCount()){
            for(int i = 0; i < getChildCount(); i++){
                ((CheckBox)getChildAt(i)).setChecked(i <= checkedPosition);
            }
            if(onRateListener!=null){
                onRateListener.onRated(checkedPosition+1, getChildCount());
            }
        }
        ratingOngoing=false;
    }

    public void setIsRatingAllowed(boolean mRatingAllowed){
        this.mRatingAllowed = mRatingAllowed;
        setRatingStarsCheckAllowed();
    }

    private void setRatingStarsCheckAllowed(){
        if(getChildCount()!=0) {
            for (int i = 0; i < getChildCount(); i++) {
                CheckBox cb = (CheckBox) getChildAt(i);
                cb.setEnabled(this.mRatingAllowed);
            }
        }
    }

    public void setRating(int mRating){
        this.mRating = mRating;
        updateViewAfterRatingChanged(this.mRating-1);
    }

    public int getRating(){
        return mRating;
    }

    public interface OnRateListener{
        void onRated(int ratingGiven, int totalRating);
    }
}