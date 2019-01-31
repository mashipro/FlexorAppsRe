package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.BoxItem;
import com.flexor.storage.flexorstoragesolution.R;

import java.util.ArrayList;

public class BoxItemViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "BoxItemViewHolder";

    View mView;
    Context mContext;

    TextView item_type,item_ammo;
    Button item_delete;
    public BoxItemViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = mView.getContext();

        item_type = mView.findViewById(R.id.item_type);
        item_ammo = mView.findViewById(R.id.item_ammo);
        item_delete = mView.findViewById(R.id.item_delete);

    }
    public void bindBoxItem (BoxItem boxItem){
        item_type.setText(boxItem.getBoxItemIdentifier());
        item_ammo.setText(boxItem.getBoxItemAmmount());
    }
}
