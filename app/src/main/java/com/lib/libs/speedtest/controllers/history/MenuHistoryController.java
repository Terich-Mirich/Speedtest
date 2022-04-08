package com.lib.libs.speedtest.controllers.history;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.activeandroid.query.Delete;
import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.adapters.HistoryAdapter;
import com.lib.libs.speedtest.controllers.MenuController;
import com.lib.libs.speedtest.controllers.info.MenuInfoController;
import com.lib.libs.speedtest.models.HistoryItem;

import java.util.List;

public class MenuHistoryController {

    private Activity activity;
    private MenuController mainMenu;
    private ViewGroup root;

    private List<HistoryItem> historyItems;
    private FrameLayout mDelete;
    private HistoryAdapter historyAdapter;

    HistoryChildGroup currentGroup;



    public MenuHistoryController(Activity activity, MenuController mainMenu, ViewGroup root) {
        this.activity = activity;
        this.mainMenu = mainMenu;
        this.root = root;
        init();
    }

    public void init (){
        mDelete = root.findViewById(R.id.delete);
        RecyclerView recyclerView = root.findViewById(R.id.list);
        // создаем адаптер
        historyItems = HistoryItem.getAll();
        historyAdapter = new HistoryAdapter(activity, historyItems);
        // устанавливаем для списка адаптер
        if (historyItems.isEmpty()) {
            mDelete.setVisibility(View.GONE);
        } else {
            mDelete.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(historyAdapter);
        mDelete.setOnClickListener(v -> onDelete());
    }

    public void onDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.important_messege))
                .setMessage(activity.getString(R.string.are_you_sure_you_want_to_delete))
                .setPositiveButton(activity.getString(R.string.ok), (dialog, id) -> {
                    new Delete().from(HistoryItem.class).execute();
                    historyAdapter.clear();
                    mDelete.setVisibility(View.GONE);
                })
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, id) -> {
                    dialog.cancel();
                })
                .create().show();
    }

    public boolean isChildClose(){
        if (currentGroup != null){
            switch (currentGroup){
            }
            currentGroup = null;
            return true;
        }else{
            return false;
        }
    }

    private enum HistoryChildGroup {

    }

    public void show(){
        Animation animTranslateIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_in);
        root.startAnimation(animTranslateIn);
        root.setVisibility(View.VISIBLE);
        mainMenu.setTitleNameMenu(activity.getString(R.string.menu_history_title));
    }

    public void hide(){
        Animation animTranslateOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.translate_out);
        root.startAnimation(animTranslateOut);
        root.setVisibility(View.GONE);
    }

}
