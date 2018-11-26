package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.BoxDetailsActivity;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.R;

import java.util.List;

public class BoxesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "BoxesViewHolder";
    View mView;
    Context mContext;
    String boxStat;

    ImageView boxIndividualImage, boxExtra;
    TextView boxName, boxStatus;
    private String boxID;

    public BoxesViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        mContext=itemView.getContext();
        itemView.setOnClickListener(this);
    }


    public void bindBox (Box box){
        Log.d(TAG, "7bindBox: "+box.toString());
        Log.d(TAG, "bindBox: binding data for box: " +box.getBoxID());
        boxIndividualImage = mView.findViewById(R.id.boxIndividualImage);
        boxExtra = mView.findViewById(R.id.box_extra);
        boxName = mView.findViewById(R.id.box_name);
        boxStatus = mView.findViewById(R.id.box_status);
        boxID = box.getBoxID();

        boxIndividualImage.setImageURI(null);
        boxExtra.setOnClickListener(this);
        boxName.setText(box.getBoxID());

        getBoxStat(box);
//        boxStatus.setText(box.getBoxStatCode().toString());

    }

    private void getBoxStat(Box box) {
        Log.d(TAG, "getBoxStat: getting box stats is: "+box.getBoxStatCode().toString());
        if (box.getBoxStatCode().intValue() == 301){
            boxStatus.setText(R.string.box_stat_available);
        } else if(box.getBoxStatCode().intValue() == 311){
            boxStatus.setText(R.string.box_stat_empty);
        } else if (box.getBoxStatCode().intValue() == 312){
            boxStatus.setText(R.string.box_stat_full);
        }
    }

    @Override
    public void onClick(View view) {
        if (boxExtra.isPressed()){
            Log.d(TAG, "onClick: boxExtra pressed");
            PopupMenu popupMenu = new PopupMenu(mContext,view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_box_details, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new MenuItemClickListener());
            popupMenu.show();
        }

    }

    private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_box_Details:
                    Log.d(TAG, "boxDetails Pressed with id: "+boxID);
                    Intent movePage = new Intent(mContext,BoxDetailsActivity.class);
                    movePage.putExtra("boxIDforExtra",boxID);
                    movePage.putExtra("boxTypeforExtra","vendor");
                    mContext.startActivity(movePage);
                    boxClickedSaveData();

                    return true;
                case R.id.nav_box_Access:
                    Log.d(TAG, "onMenuItemClick: boxAccess pressed");
                    return true;
                case R.id.nav_box_remove:
                    Log.d(TAG, "onMenuItemClick: boxRemove pressed");
                    return true;
            }
            return false;
        }
    }

    private void boxClickedSaveData() {

    }
}
