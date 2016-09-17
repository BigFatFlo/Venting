package com.example.florentremis.venting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartVentingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_venting);
    }

    public void startVentRoom(View view) {
        Intent startVentRoomIntent = new Intent(StartVentingActivity.this, VentRoomActivity.class);
        startActivity(startVentRoomIntent);
    }

}