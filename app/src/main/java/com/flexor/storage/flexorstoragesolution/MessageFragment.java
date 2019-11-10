package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.ChatsMini;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataSeparatorListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.SingleBoxListener;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.ViewHolder.ChatListsHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";

    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private ConstraintLayout layout;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private DatabaseReference databaseReference;
    private UserManager userManager;
    private User user;
    private FirebaseRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter<ChatsMini,ChatListsHolder> firestoreRecyclerAdapter;
    private ChatListsHolder chatListsHolder;
    private Query query;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message,container,false);
        context = view.getContext();

        recyclerView = view.findViewById(R.id.recyclerView);
        layout = view.findViewById(R.id.messageHeader);

        userManager = new UserManager();
        userManager.getInstance();
        user = userManager.getUser();

        firestore = FirebaseFirestore.getInstance();

        Log.d(TAG, "onCreateView: user found: "+user);
        query = firestore.collection("Users").document(user.getUserID()).collection("ChatsData");

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (!query.get().isSuccessful()){
            Toast.makeText(context, "query error", Toast.LENGTH_SHORT).show();
        }
        FirestoreRecyclerOptions<ChatsMini> options = new FirestoreRecyclerOptions.Builder<ChatsMini>()
                .setQuery(query,ChatsMini.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatsMini, ChatListsHolder>(options) {
            @NonNull
            @Override
            public ChatListsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View views = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_chats, viewGroup, false);
                return new ChatListsHolder(views);
            }
            @Override
            protected void onBindViewHolder(@NonNull ChatListsHolder holder, int position, @NonNull ChatsMini model) {
                holder.bindData(model, position);
            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };

        if (firestoreRecyclerAdapter.getItemCount() <= 0){
            Toast.makeText(context, "Chats empty!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStop() {
        firestoreRecyclerAdapter.startListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        firestoreRecyclerAdapter.startListening();
        super.onStart();
    }
}
