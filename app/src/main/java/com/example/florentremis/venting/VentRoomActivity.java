package com.example.florentremis.venting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class VentRoomActivity extends AppCompatActivity {

    private static final String TAG = "VentRoomActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String MESSAGES_CHILD = "messages";
    private RecyclerView mVentRoomMessagesRecyclerView;
    private FirebaseRecyclerAdapter<VentRoomMessage, VentRoomMessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private String ventRoomId = null;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String userName = null;

    public static class VentRoomMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView venterMessageTextView;
        public TextView venterSenderTextView;
        public TextView messageTextView;
        public TextView senderTextView;

        public VentRoomMessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            senderTextView = (TextView) v.findViewById(R.id.senderTextView);
            venterMessageTextView = (TextView) v.findViewById(R.id.venterMessageTextView);
            venterSenderTextView = (TextView) v.findViewById(R.id.venterSenderTextView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vent_room);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        Intent intent = getIntent();
        ventRoomId = intent.getStringExtra("ventRoomId");
        userName = intent.getStringExtra("userName");
        mVentRoomMessagesRecyclerView = (RecyclerView) findViewById(R.id.ventRoomMessagesRecyclerView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendMessageButton = (Button) findViewById(R.id.sendButton);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<VentRoomMessage, VentRoomMessageViewHolder>(
                    VentRoomMessage.class,
                    R.layout.item_message,
                    VentRoomMessageViewHolder.class,
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(ventRoomId)) {

                @Override
                protected void populateViewHolder(VentRoomMessageViewHolder viewHolder, VentRoomMessage ventRoomMessage, int position) {
                    String sender = ventRoomMessage.getSender();
                    String roomMessage = ventRoomMessage.getText();
                    if (sender.contentEquals("Venter")) {
                        viewHolder.venterMessageTextView.setText(roomMessage);
                        viewHolder.venterSenderTextView.setText(sender);
                    } else {
                        viewHolder.messageTextView.setText(roomMessage);
                        viewHolder.senderTextView.setText(sender);
                    }
                }
            };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int messageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                    // to the bottom of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                        mVentRoomMessagesRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mVentRoomMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);
            mVentRoomMessagesRecyclerView.setAdapter(mFirebaseAdapter);
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void sendMessage(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (messageEditText.getText().toString().length() > 0 && userName != null && user != null) {
            VentRoomMessage message = new VentRoomMessage(messageEditText.getText().toString(), userName);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(ventRoomId).push().setValue(message);
            messageEditText.setText("");
        }
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