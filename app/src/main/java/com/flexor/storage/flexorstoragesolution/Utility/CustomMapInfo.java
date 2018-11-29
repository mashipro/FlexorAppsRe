package com.flexor.storage.flexorstoragesolution.Utility;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMapInfo implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public CustomMapInfo(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_map_info, null);
        ImageView infoImage = view.findViewById(R.id.map_info_image);
        TextView infoText = view.findViewById(R.id.map_info_vendor_name);
        infoText.setText(marker.getTitle());
        //Todo update vendor image info map here
        return view;
    }
}
