package com.lib.libs.speedtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.lib.libs.speedtest.adapters.HistoryAdapter;
import com.lib.libs.speedtest.models.HistoryItem;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        RecyclerView recyclerView = findViewById(R.id.list);
        // создаем адаптер
        HistoryAdapter historyAdapter = new HistoryAdapter(this, HistoryItem.getAll());
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(historyAdapter);

    }

}