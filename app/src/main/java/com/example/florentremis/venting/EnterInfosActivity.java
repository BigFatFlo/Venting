package com.example.florentremis.venting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class EnterInfosActivity extends AppCompatActivity {

    Spinner sexesSpinner = null;
    List<String> listSexes = new ArrayList<String>();
    Spinner agesSpinner = null;
    List<String> listAges = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_infos);
        sexesSpinner = (Spinner) findViewById(R.id.selectSexSpinner);
        agesSpinner = (Spinner) findViewById(R.id.selectAgeSpinner);

        listSexes.add("Male");
        listSexes.add("Female");
        for (int i = 15; i < 100; i++) {
            listAges.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapterSexes = new ArrayAdapter<String>(EnterInfosActivity.this,
                android.R.layout.simple_spinner_item , listSexes);
        adapterSexes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexesSpinner.setAdapter(adapterSexes);

        ArrayAdapter<String> adapterAges = new ArrayAdapter<String>(EnterInfosActivity.this,
                android.R.layout.simple_spinner_item , listAges);
        adapterAges.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agesSpinner.setAdapter(adapterAges);
    }

    public void viewVentRooms(View view) {
        Intent viewVentRoomsIntent = new Intent(EnterInfosActivity.this, ViewVentRoomsActivity.class);
        String selectedSex = sexesSpinner.getSelectedItem().toString();
        if (selectedSex.contentEquals("Male")) {
            selectedSex = "M";
        } else if (selectedSex.contentEquals("Female")) {
            selectedSex = "F";
        }
        int selectedAge = Integer.parseInt(agesSpinner.getSelectedItem().toString());
        viewVentRoomsIntent.putExtra("userAge", selectedAge);
        viewVentRoomsIntent.putExtra("userSex", selectedSex);
        startActivity(viewVentRoomsIntent);
    }

}
