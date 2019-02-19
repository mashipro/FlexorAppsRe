package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

class BoxGlobalViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "BoxGlobalViewHolder";
    View mView;
    Context context;

    private ImageView boxIndividualImage, boxExtra;
    private TextView boxName, boxStatus;
    private Box boxes;
    private TransitionalStatCode transitionalStatCode;

    private ArrayList<SingleBox> userBoxArrayList = new ArrayList<>();
    private int transitionCode;

    public BoxGlobalViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        context = mView.getContext();

        transitionalStatCode = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        transitionCode = transitionalStatCode.getDerivedPaging();
        userBoxArrayList = transitionalStatCode.getSingleBoxesContainer();

        boxExtra = mView.findViewById(R.id.box_extra);
        boxIndividualImage = mView.findViewById(R.id.boxIndividualImage);
        boxName = mView.findViewById(R.id.storage_name);
        boxStatus = mView.findViewById(R.id.box_status);
    }

    public void bindData(Box box) {
        boxes = box;
        Log.d(TAG, "bindData: id: "+ box.getBoxID()+" ,stat: "+box.getBoxStatCode());
        /**change box status*/
        if (transitionCode == Constants.STATSCODE_USER_USER){
            /** USER VIEW*/
            boxExtra.setVisibility(View.GONE);
            if (box.getBoxStatCode() == 300){
                mView.setVisibility(View.GONE);
            }else if (box.getBoxStatCode() == 301){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_available);
            }else if (box.getBoxStatCode() == 311){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_empty);
            }else if (box.getBoxStatCode() ==312){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_full);
            }
        }else {
            /**VENDOR AND ADMIN VIEW*/
            boxExtra.setVisibility(View.VISIBLE);
            if (box.getBoxStatCode() == 300){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_disabled);
            }else if (box.getBoxStatCode() == 301){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_available);
            }else if (box.getBoxStatCode() == 311){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_user);
            }else if (box.getBoxStatCode() ==312){
                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_user);
            }
        }


        boxName.setText(box.getBoxName());
        getBoxStat(box.getBoxStatCode());


    }
    private void getBoxStat(int i) {
        Log.d(TAG, "getBoxStat: getting box stats is: "+i);
        if (i == 311){
            boxStatus.setText(R.string.box_stat_empty);
        } else if (i == 312){
            boxStatus.setText(R.string.box_stat_full);
        } else {
            boxStatus.setText(R.string.box_stat_available);
        }
    }

    private boolean isUserBox() {
        SingleBox newSingle = new SingleBox();
        newSingle.setBoxID(boxes.getBoxID());
        newSingle.setBoxVendor(boxes.getUserVendorOwner());
        return userBoxArrayList.contains(newSingle);
    }


}
