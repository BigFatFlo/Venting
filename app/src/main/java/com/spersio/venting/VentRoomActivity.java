package com.spersio.venting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.support.v7.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class VentRoomActivity extends AppCompatActivity {

    private static final String TAG = "VentRoomActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String VENTROOMS_CHILD = "ventRooms";
    public static final String VENTROOMS_AGE_SLOTS_CHILD = "ventRoomsAgeSlots";
    public static final String MESSAGES_CHILD = "messages";
    public static final String TIME_LEFT_CHILD = "timeLeft";
    public static final String LAST_UPDATE_TIME_CHILD = "lastUpdateTime";
    private static final int VENTROOM_TIMEOUT = 2*60*60*1000; // 1 hour, 1 min
    private static final int VENTROOM_LIFESPAN = 60*60*1000; // 1 hour
    private RecyclerView mVentRoomMessagesRecyclerView;
    private FirebaseRecyclerAdapter<VentRoomMessage, VentRoomMessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private String ventRoomId = null;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String userName = null;
    private LinearLayout ventRoomButtonsLayout;
    private Button lockVentRoomButton;
    private Button leaveVentRoomButton;
    private CountDownTimer countDownTimer = null;
    private static final String[] ageCategories =  {
            "15F", "20F", "25F", "30F", "35F",
            "40F", "45F", "50F", "55F", "60F",
            "65F", "70F", "75F", "80F", "85F",
            "90F", "95F", "15M", "20M", "25M",
            "30M", "35M", "40M", "45M", "50M",
            "55M", "60M", "65M", "70M", "75M",
            "80M", "85M", "90M", "95M"};
    private Boolean ventRoomLocked = false;
    private Boolean ventRoomClosed = false;

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
        ventRoomButtonsLayout = (LinearLayout) findViewById(R.id.ventRoomButtonsLayout);
        lockVentRoomButton = (Button) findViewById(R.id.lockVentRoomButton);
        leaveVentRoomButton = (Button) findViewById(R.id.leaveVentRoomButton);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            ventRoomButtonsLayout.setVisibility(View.GONE);
        } else {
            if (userName.contentEquals("Venter")) {
                leaveVentRoomButton.setVisibility(View.GONE);
            } else {
                ventRoomButtonsLayout.setVisibility(View.GONE);
            }
        }
        if (user != null) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            countDownTimer = new CountDownTimer(VENTROOM_TIMEOUT, 3000) {
                public void onTick(long millisUntilFinished) {
                    mFirebaseDatabaseReference.child(VENTROOMS_CHILD).child(ventRoomId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                VentRoom ventRoom = dataSnapshot.getValue(VentRoom.class);
                                if (ventRoom.getTimeLeft() <= 0) {
                                    ventRoomLocked = ventRoom.getLocked();
                                    userName = "Venter";
                                    VentRoomActivity.this.finish();
                                } else {
                                    long creationTime = ventRoom.getCreationTime();
                                    long lastUpdateTime = ventRoom.getLastUpdateTime();
                                    long timeLeft = creationTime + VENTROOM_LIFESPAN - lastUpdateTime;
                                    mFirebaseDatabaseReference.child(VENTROOMS_CHILD).child(ventRoomId).child(TIME_LEFT_CHILD).setValue(timeLeft);
                                    mFirebaseDatabaseReference.child(VENTROOMS_CHILD).child(ventRoomId).child(LAST_UPDATE_TIME_CHILD).setValue(ServerValue.TIMESTAMP);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
                }

                public void onFinish() {
                    VentRoomActivity.this.finish();
                }
            }.start();
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
                        viewHolder.messageTextView.setText("");
                        viewHolder.senderTextView.setText("");
                    } else {
                        viewHolder.venterMessageTextView.setText("");
                        viewHolder.venterSenderTextView.setText("");
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

    protected void closeVentRoom(View view) {
        closeVentRoom(view, R.string.this_will_close_the_ventroom_permanently);
    }

    protected void closeVentRoom(View view, int message) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && userName != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VentRoomActivity.this);
            alertDialogBuilder.setTitle(R.string.close_ventroom_question);
            alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final ProgressDialog dlg = new ProgressDialog(VentRoomActivity.this);
                        dlg.setTitle(getResources().getString(R.string.please_wait));
                        dlg.setMessage(getResources().getString(R.string.closing_ventroom));
                        dlg.show();
                        Intent backToHomeIntent = new Intent(VentRoomActivity.this, MainActivity.class);
                        deleteVentRoom(ventRoomLocked);
                        ventRoomClosed = true;
                        dlg.dismiss();
                        startActivity(backToHomeIntent);
                        VentRoomActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    protected void lockVentRoom(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && userName != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VentRoomActivity.this);
            alertDialogBuilder.setTitle(R.string.lock_ventroom_question);
            alertDialogBuilder
                .setMessage(R.string.people_wont_be_able_to_join_this_VentRoom_anymore)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final ProgressDialog dlg = new ProgressDialog(VentRoomActivity.this);
                        dlg.setTitle(getResources().getString(R.string.please_wait));
                        dlg.setMessage(getResources().getString(R.string.locking_ventroom));
                        dlg.show();
                        deleteVentRoomAgeSlots();
                        ventRoomLocked = true;
                        lockVentRoomButton.setVisibility(View.GONE);
                        dlg.dismiss();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    protected void leaveVentRoom(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VentRoomActivity.this);
        alertDialogBuilder.setTitle(R.string.leave_ventroom_question);
        alertDialogBuilder
            .setMessage(R.string.you_wont_be_able_to_join_this_ventroom_again)
            .setCancelable(false)
            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent backToHomeIntent = new Intent(VentRoomActivity.this, MainActivity.class);
                    startActivity(backToHomeIntent);
                    VentRoomActivity.this.finish();
                }
            })
            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void deleteVentRoom(Boolean ventRoomLocked) {
        Map<String, Object> childVentRoomUpdates = new HashMap<>();
        childVentRoomUpdates.put("/" + MESSAGES_CHILD + "/" + ventRoomId, null);
        childVentRoomUpdates.put("/" + VENTROOMS_CHILD + "/" + ventRoomId, null);
        mFirebaseDatabaseReference.updateChildren(childVentRoomUpdates);
        if (!ventRoomLocked) {
            deleteVentRoomAgeSlots();
        }
    }

    protected void deleteVentRoomAgeSlots() {
        Map<String, Object> childVentRoomsAgeSlotUpdates = new HashMap<>();

        for (int i = 0; i < ageCategories.length; i++) {
            childVentRoomsAgeSlotUpdates.put("/" + VENTROOMS_AGE_SLOTS_CHILD + "/" + ageCategories[i] + "/" + ventRoomId, null);
        }
        mFirebaseDatabaseReference.updateChildren(childVentRoomsAgeSlotUpdates);
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

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        if (userName.contentEquals("Venter")) {
            if (!ventRoomClosed) {
                deleteVentRoom(ventRoomLocked);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VentRoomActivity.this);
        alertDialogBuilder.setTitle(R.string.leave_ventroom_question);
        if (userName.contentEquals("Venter")) {
            closeVentRoom(null, R.string.leaving_will_close_the_ventroom_permanently);
        } else {
            leaveVentRoom(null);
        }
    }
}