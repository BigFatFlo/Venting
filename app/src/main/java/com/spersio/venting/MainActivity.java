package com.spersio.venting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startVenting(View view) {
        Intent startVentingIntent = new Intent(MainActivity.this, StartVentingActivity.class);
        startActivity(startVentingIntent);
    }

    public void helpVent(View view) {
        Intent helpVentIntent = new Intent(MainActivity.this, EnterInfosActivity.class);
        startActivity(helpVentIntent);
    }

}
