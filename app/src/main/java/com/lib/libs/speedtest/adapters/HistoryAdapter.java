package com.lib.libs.speedtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.models.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");


    private final LayoutInflater inflater;
    private final List<HistoryItem> historyItems;

    public HistoryAdapter(Context context, List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        HistoryItem state = historyItems.get(position);
        holder.typeView.setText(state.getType());
        holder.dateView.setText(formatter.format(state.getDate()));
        holder.dLoadView.setText(String.format(Locale.getDefault(),"%.1f", state.getDmbps()));
        holder.uLoadView.setText(String.format(Locale.getDefault(),"%.1f", state.getUmbps()));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView typeView;
        final TextView dateView;
        final TextView dLoadView;
        final TextView uLoadView;
        ViewHolder(View view){
            super(view);
            typeView = view.findViewById(R.id.type);
            dateView = view.findViewById(R.id.date);
            dLoadView = view.findViewById(R.id.dLoad);
            uLoadView = view.findViewById(R.id.uLoad);
        }
    }
}
