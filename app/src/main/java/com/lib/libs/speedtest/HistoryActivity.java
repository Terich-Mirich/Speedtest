package com.lib.libs.speedtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.os.Bundle;
import android.widget.Button;

import com.activeandroid.query.Delete;
import com.lib.libs.speedtest.adapters.HistoryAdapter;
import com.lib.libs.speedtest.models.HistoryItem;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private List<HistoryItem> historyItems;
    private Button mDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mDelete = findViewById(R.id.delete);
        RecyclerView recyclerView = findViewById(R.id.list);
        // создаем адаптер
        historyItems = HistoryItem.getAll();
        HistoryAdapter historyAdapter = new HistoryAdapter(this, historyItems);
        // устанавливаем для списка адаптер
        if (historyItems.isEmpty()) {
            mDelete.setEnabled(false);
        }
        recyclerView.setAdapter(historyAdapter);
        mDelete.setOnClickListener(v -> onDalete());

    }

    public void onDalete (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Важное сообщение!")
                .setMessage("Вы точно хотите удалить?")
                .setPositiveButton("ОК, иди на хуй", (dialog, id) -> {
                    new Delete().from(HistoryItem.class).execute();
                    recreate();
                })
                .setNegativeButton("Отменить", (dialog, id) -> {
                    dialog.cancel();
                })
                .create().show();
    }







}