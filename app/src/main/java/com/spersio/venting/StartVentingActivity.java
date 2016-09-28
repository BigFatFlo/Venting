package com.spersio.venting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class StartVentingActivity extends AppCompatActivity {

    private static final String TAG = "ViewVentRoomsActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    int i = 0;
    private FirebaseUser user;
    public EditText ventRoomTitleEdit;
    private static final String[] ageCategories =  {
            "15F", "20F", "25F", "30F", "35F",
            "40F", "45F", "50F", "55F", "60F",
            "65F", "70F", "75F", "80F", "85F",
            "90F", "95F", "15M", "20M", "25M",
            "30M", "35M", "40M", "45M", "50M",
            "55M", "60M", "65M", "70M", "75M",
            "80M", "85M", "90M", "95M"};
    private static final long VENTROOM_TIMEOUT = 60*1000; // 1 hour
    public static final String VENTROOMS_AGE_SLOTS_CHILD = "ventRoomsAgeSlots";
    public static final String VENTROOMS_CHILD = "ventRooms";
    public static final String TIME_LEFT_CHILD = "timeLeft";
    public static final String LAST_UPDATE_TIME_CHILD = "lastUpdateTime";
    public static final String VENTROOM_TITLE_CHILD = "title";
    public static final String CREATION_TIME_CHILD = "creationTime";
    public static final String VENTER_ID_CHILD = "venterId";
    public static final String VENTROOM_ID_CHILD = "roomId";
    public static final String LOCKED_CHILD = "locked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_venting);
        ventRoomTitleEdit = (EditText) findViewById(R.id.titleEditText);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            }
        };
    }

    public void startVentRoom(View view) {
        if (user != null) {
            if (ventRoomTitleEdit.getText().length() > 10) {
                if (ventRoomTitleEdit.getText().length() < 100) {
                    String ventRoomId = openNewVentRoom(user.getUid(), ventRoomTitleEdit.getText().toString(), System.currentTimeMillis()/1000);
                    Intent startVentRoomIntent = new Intent(StartVentingActivity.this, VentRoomActivity.class);
                    startVentRoomIntent.putExtra("ventRoomId", ventRoomId);
                    startVentRoomIntent.putExtra("userName", "Venter");
                    startActivity(startVentRoomIntent);
                } else {
                    Toast.makeText(StartVentingActivity.this, "Title too long (more than 100 characters",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StartVentingActivity.this, "Title too short (less than 10 characters)",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Anonymous login failed");
        }
    }

    public String openNewVentRoom(String userId, String title, Long currentTime) {
        final ProgressDialog dlg = new ProgressDialog(StartVentingActivity.this);
        dlg.setTitle(getResources().getString(R.string.please_wait));
        dlg.setMessage(getResources().getString(R.string.opening_ventroom));
        dlg.show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String roomId = String.valueOf(currentTime) + userId;
        VentRoomStringTimes ventRoom = new VentRoomStringTimes(title, roomId, ServerValue.TIMESTAMP, ServerValue.TIMESTAMP, VENTROOM_TIMEOUT, false, userId);
        VentRoomsAgeSlotStringTime ventRoomsAgeSlot = new VentRoomsAgeSlotStringTime(title, roomId, ServerValue.TIMESTAMP, userId);
        Map<String, Object> ventRoomValues = ventRoom.toMap();
        Map<String, Object> ventRoomsAgeSlotValues = ventRoomsAgeSlot.toMap();

        Map<String, Object> childVentRoomUpdates = new HashMap<>();
        Map<String, Object> childVentRoomsAgeSlotUpdates = new HashMap<>();

        for (int i = 0; i < ageCategories.length; i++) {
            childVentRoomsAgeSlotUpdates.put("/" + VENTROOMS_AGE_SLOTS_CHILD + "/" + ageCategories[i] + "/" + roomId, ventRoomsAgeSlotValues);
        }
        mDatabase.updateChildren(childVentRoomsAgeSlotUpdates);
        childVentRoomUpdates.put("/" + VENTROOMS_CHILD + "/" + roomId, ventRoomValues);
        mDatabase.updateChildren(childVentRoomUpdates);
        dlg.dismiss();
        return (roomId);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInAnonymously", task.getException());
                    Toast.makeText(StartVentingActivity.this, "Authentication failed.",
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