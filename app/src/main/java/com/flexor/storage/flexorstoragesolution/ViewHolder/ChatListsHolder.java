package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.ChatsMini;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatListsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ChatListsHolder";

    private View view;
    private Context context;
    private FirebaseFirestore firestore;
    private DocumentReference reference;

    private User user;

    private TextView chatName;
    private CircleImageView chatImage;
    private ConstraintLayout chatCardBase;



    public ChatListsHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        context = view.getContext();

        user=((UserClient)(getApplicationContext())).getUser();

        chatName = view.findViewById(R.id.chat_name);
        chatImage = view.findViewById(R.id.chat_image);
        chatCardBase = view.findViewById(R.id.chat_card_base);

    }
    public void bindData(final ChatsMini chatsMini, final int position){
        chatName.setText(chatsMini.getToID());
        chatCardBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: pos: "+position+" content: "+chatsMini);
                // TODO: 11/7/2019 ON ITEM CLICKED
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
