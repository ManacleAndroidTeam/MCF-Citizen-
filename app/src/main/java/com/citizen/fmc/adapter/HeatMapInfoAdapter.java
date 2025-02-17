package com.citizen.fmc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class HeatMapInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public HeatMapInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_custom_info_window, null);
        ((TextView) mView.findViewById(R.id.address_text_view)).setText(marker.getTitle());
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
