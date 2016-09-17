package com.example.florentremis.venting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EnterInfosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_infos);
    }

    public void viewVentRooms(View view) {
        Intent viewVentRoomsIntent = new Intent(EnterInfosActivity.this, ViewVentRoomsActivity.class);
        startActivity(viewVentRoomsIntent);
    }

}
