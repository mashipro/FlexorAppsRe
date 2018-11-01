package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.R;

public class BoxesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;

    ImageView boxIndividualImage, boxExtra;
    TextView boxName, boxStatus;

    public BoxesViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        mContext=itemView.getContext();
        itemView.setOnClickListener(this);
    }
    public void bindBox (Box box){
        boxIndividualImage = mView.findViewById(R.id.boxIndividualImage);
        boxExtra = mView.findViewById(R.id.box_extra);
        boxName = mView.findViewById(R.id.box_name);
        boxStatus = mView.findViewById(R.id.box_status);

        boxIndividualImage.setImageURI(null);
        boxExtra.setOnClickListener(this);
        boxName.setText(box.getBoxID());
//        boxStatus.setText(box.getBoxStatCode().toString());
    }

    @Override
    public void onClick(View view) {

    }
}
