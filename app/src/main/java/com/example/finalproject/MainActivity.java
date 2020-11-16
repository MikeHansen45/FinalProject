package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    Button toTicket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toTicket = findViewById(R.id.toTicket);
        Intent goToTicket = new Intent(this,TicketMasterActivity.class);

        toTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToTicket);
            }
        });

        Button covbutton = findViewById(R.id.toCovid);
        covbutton.setOnClickListener( click -> {
            Intent goToCovid = new Intent(MainActivity.this, CovidData.class);
            startActivity(goToCovid);});
    }
}