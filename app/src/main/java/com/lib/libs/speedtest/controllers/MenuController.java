package com.lib.libs.speedtest.controllers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.controllers.history.MenuHistoryController;
import com.lib.libs.speedtest.controllers.info.MenuInfoController;
import com.lib.libs.speedtest.controllers.settings.MenuSettingsController;
import com.lib.libs.speedtest.controllers.support.MenuSupportController;
import com.lib.libs.speedtest.models.HistoryItem;

public class MenuController {

    private Activity activity;
    private ViewGroup root;

    private boolean menuStatus;

    private View mCloseMenu;
    private View mBackMenuButton;
    private View mMenuFrameBackground;

    private View historyButton;
    private View infoButton;
    private View settingsButton;
    private View supportButton;

    private MenuHistoryController menuHistoryController;
    private MenuInfoController menuInfoController;
    private MenuSettingsController menuSettingsController;
    private MenuSupportController menuSupportController;

    private TextView mMenuTitle;

    private MenuGroup currentGroup;



    public MenuController(Activity activity, ViewGroup root) {
        this.activity = activity;
        this.root = root;
        init();
    }

    private void init(){
        mMenuFrameBackground = ((ViewGroup)root.getParent()).findViewById(R.id.menuFrameBackground);
        mCloseMenu = root.findViewById(R.id.closeMenu);
        mCloseMenu.setOnClickListener(v ->{
            closeMenu(true);
        });
        mBackMenuButton = root.findViewById(R.id.backMenuButton);
        mBackMenuButton.setOnClickListener(v -> {
            closeMenu(false);
        });
        mBackMenuButton.setVisibility(View.GONE);
        mMenuTitle = root.findViewById(R.id.menuTitle);

        initMainMenu();
        menuHistoryController = new MenuHistoryController(activity, this, root.findViewById(R.id.menuHistoryGroup));
        menuInfoController = new MenuInfoController(activity, this, root.findViewById(R.id.menuInfoGroup));
        menuSettingsController = new MenuSettingsController(activity,this, root.findViewById(R.id.menuSettingsGroup));
        menuSupportController = new MenuSupportController(activity, this, root.findViewById(R.id.menuSupportGroup));


    }

    private void initMainMenu(){
        historyButton = root.findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.HISTORY);
        });
        infoButton = root.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.INFO);
        });
        settingsButton = root.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.SETTINGS);
        });
        supportButton = root.findViewById(R.id.supportButton);
        supportButton.setOnClickListener(v -> {
            showMenuGroup(MenuGroup.SUPPORT);
        });
    }

    public void openMenu(){
        Animation animFadeIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fade_in);
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
        mMenuFrameBackground.startAnimation(animFadeIn);
        mMenuFrameBackground.setVisibility(View.VISIBLE);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
        menuStatus = true;
        menuHistoryController.init();
    }

    public void closeMenu(boolean close){

        if (currentGroup != null && !close){
            switch (currentGroup){
                case HISTORY:
                    if (!menuHistoryController.isChildClose()){
                        menuHistoryController.hide();
                    }else {
                        return;
                    }
                    break;
                case INFO:
                    if (!menuInfoController.isChildClose()){
                        menuInfoController.hide();
                    }else {
                        return;
                    }
                    break;
                case SETTINGS:
                    menuSettingsController.hide();
                    break;
                case SUPPORT:
                    menuSupportController.hide();
                    break;
                default:
                    break;
            }
            currentGroup = null;
            setTitleNameMenu("");
            Animation animFadeOut = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_out_menu);
            mBackMenuButton.startAnimation(animFadeOut);
            mBackMenuButton.setVisibility(View.GONE);
            return;
        }else if (close){
            if (currentGroup != null){
                switch (currentGroup){
                    case HISTORY:
                        menuHistoryController.isChildClose();
                        menuHistoryController.hide();
                        break;
                    case INFO:
                        menuInfoController.isChildClose();
                        menuInfoController.hide();
                        break;
                    case SETTINGS:
                        menuSettingsController.hide();
                        break;
                    case SUPPORT:
                        menuSupportController.hide();
                        break;
                }
                currentGroup = null;
                setTitleNameMenu("");
                Animation animFadeOut = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_out_menu);
                mBackMenuButton.startAnimation(animFadeOut);
                mBackMenuButton.setVisibility(View.GONE);
            }
        }
        Animation animFadeOut = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_out);
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        mMenuFrameBackground.startAnimation(animFadeOut);
        mMenuFrameBackground.setVisibility(View.GONE);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
        menuStatus = false;
    }

    public void setTitleNameMenu (String name){
        mMenuTitle.setText(name);
    }

    private void showMenuGroup(MenuGroup group){
        if (currentGroup != null) return;
        currentGroup = group;
        Animation animFadeIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in_menu);
        mBackMenuButton.startAnimation(animFadeIn);
        mBackMenuButton.setVisibility(View.VISIBLE);
        switch (group){
            case HISTORY:
                menuHistoryController.show();
                break;
            case INFO:
                menuInfoController.show();
                break;
            case SETTINGS:
                menuSettingsController.show();
                break;
            case SUPPORT:
                menuSupportController.show();
                break;
            default:
                break;
        }
    }



    public boolean getMenuStatus() {
        return menuStatus;
    }

    private enum MenuGroup {
        HISTORY,
        INFO,
        SETTINGS,
        SUPPORT
    }
}


