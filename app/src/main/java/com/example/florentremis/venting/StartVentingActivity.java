package com.example.florentremis.venting;

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

public class StartVentingActivity extends AppCompatActivity {

    private static final String TAG = "ViewVentRoomsActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    int i = 0;
    private FirebaseUser user;
    public EditText ventRoomTitleEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_venting);
        ventRoomTitleEdit = (EditText) findViewById(R.id.title_edit_text);
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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String roomId = String.valueOf(currentTime) + userId;
        mDatabase.child("ventRooms").child(roomId).child("title").setValue(title);
        mDatabase.child("ventRooms").child(roomId).child("creationTime").setValue(currentTime);
        mDatabase.child("ventRooms").child(roomId).child("venterId").setValue(userId);
        mDatabase.child("ventRooms").child(roomId).child("roomId").setValue(roomId);
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