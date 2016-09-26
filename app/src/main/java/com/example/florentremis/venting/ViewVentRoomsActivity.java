package com.example.florentremis.venting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewVentRoomsActivity extends AppCompatActivity {

    private static Context mContext;
    private static final String TAG = "ViewVentRoomsActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String VENTROOMS_AGE_SLOTS_CHILD = "ventRoomsAgeSlots";
    private RecyclerView mVentRoomsRecyclerView;
    private FirebaseRecyclerAdapter<ShortVentRoom, ShortVentRoomViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private static String userName = null;
    private static String ageSlot = null;

    public static class ShortVentRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView ventRoomTitleTextView;
        public String ventRoomId;

        public ShortVentRoomViewHolder(View v) {
            super(v);
            ventRoomTitleTextView = (TextView) v.findViewById(R.id.ventRoomTitleTextView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (userName != null) {
                String title = ((TextView) v.findViewWithTag("ventRoomItem")).getText().toString();
                FirebaseDatabase.getInstance().getReference().child(VENTROOMS_AGE_SLOTS_CHILD).child(ageSlot).child(ventRoomId).removeValue();
                Intent ventRoomIntent = new Intent(mContext, VentRoomActivity.class);
                ventRoomIntent.putExtra("ventRoomId", ventRoomId);
                ventRoomIntent.putExtra("userName", userName);
                mContext.startActivity(ventRoomIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vent_rooms);
        mContext = this;
        Intent intent = getIntent();
        int userAge = intent.getIntExtra("userAge", 0);
        String userSex = intent.getStringExtra("userSex");
        userName = String.valueOf(userAge) + userSex;
        int ageRemainder = userAge % 5;
        ageSlot = String.valueOf(userAge - ageRemainder) + userSex;
        mVentRoomsRecyclerView = (RecyclerView) findViewById(R.id.ventRoomsRecyclerView);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    mFirebaseAdapter = new FirebaseRecyclerAdapter<ShortVentRoom, ShortVentRoomViewHolder>(
                            ShortVentRoom.class,
                            R.layout.item_ventroom,
                            ShortVentRoomViewHolder.class,
                            mFirebaseDatabaseReference.child(VENTROOMS_AGE_SLOTS_CHILD).child(ageSlot).orderByChild("creationTime")) {

                        @Override
                        protected void populateViewHolder(ShortVentRoomViewHolder viewHolder, ShortVentRoom ventRoom, int position) {
                            viewHolder.ventRoomTitleTextView.setText(ventRoom.getTitle());
                            viewHolder.ventRoomId = ventRoom.getRoomId();
                        }
                    };

                    mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            super.onItemRangeInserted(positionStart, itemCount);
                        }
                    });

                    mVentRoomsRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mVentRoomsRecyclerView.setAdapter(mFirebaseAdapter);
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInAnonymously", task.getException());
                        Toast.makeText(ViewVentRoomsActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}