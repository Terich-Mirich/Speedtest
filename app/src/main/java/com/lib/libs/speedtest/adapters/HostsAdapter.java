package com.lib.libs.speedtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lib.libs.speedtest.Host;
import com.lib.libs.speedtest.R;
import com.lib.libs.speedtest.controllers.HostsChangeController;

import java.util.List;
import java.util.Locale;

public class HostsAdapter extends RecyclerView.Adapter<HostsAdapter.ViewHolder> {

    private final List<Host> hosts;
    private final LayoutInflater hostInflater;
    private HostChanger hostChanger;
    private final Context context;
    private int prevId = -1;

    public HostsAdapter(Context context, List<Host> host, HostChanger hostChanger) {
        this.hosts = host;
        this.context = context;
        this.hostInflater = LayoutInflater.from(context);
        this.hostChanger = hostChanger;
    }

    @Override
    public HostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = hostInflater.inflate(R.layout.hosts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HostsAdapter.ViewHolder holder, int position) {
        Host hostData = hosts.get(position);
        holder.nameProviderView.setText(hostInflater.getContext().getString(R.string.linear_host_change) + hostData.getProviderHost());
        holder.cityProviderView.setText(hostData.getCityHost());
        holder.countryProviderView.setText(hostData.getCountryHost());
        holder.pingProviderView.setText(String.format(Locale.getDefault(), "%.1f", hostData.getPing()));
        prevId = hostData.isSelected() && prevId == -1 ? hostData.getId() : prevId;
        holder.mLinearHostServers.setOnClickListener(v -> {
            prevId = hostData.getId();
            notifyDataSetChanged();
            hostChanger.onHostChange(hostData);
        });
        if (prevId == hostData.getId()) {
            holder.mLinearHostServers.setBackgroundColor(context.getResources().getColor(R.color.blue_grey_900));
        } else {
            holder.mLinearHostServers.setBackgroundColor(context.getResources().getColor(R.color.blue_grey_800));
        }
    }

    @Override
    public int getItemCount() {
        return hosts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameProviderView;
        final TextView cityProviderView;
        final TextView countryProviderView;
        final TextView pingProviderView;
        final View mLinearHostServers;

        ViewHolder(View view) {
            super(view);
            nameProviderView = view.findViewById(R.id.nameProvider);
            cityProviderView = view.findViewById(R.id.cityProvider);
            countryProviderView = view.findViewById(R.id.countryProvider);
            pingProviderView = view.findViewById(R.id.pingProvider);
            mLinearHostServers = view.findViewById(R.id.linearHostServers);
        }
    }

    public void clear() {
        int size = hosts.size();
        hosts.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface HostChanger{
        void onHostChange(Host host);
    }

}
