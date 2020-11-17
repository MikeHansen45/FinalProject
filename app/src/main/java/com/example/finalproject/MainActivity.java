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
    Button toRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toTicket = findViewById(R.id.toTicket);
        toRecipe = findViewById(R.id.toRecipe);

        Intent goToTicket = new Intent(this,TicketMasterActivity.class);

        //Recipe button goes to Recipe Activity
        Intent goToRecipe = new Intent(this,RecipeActivity.class);
        toRecipe.setOnClickListener(v -> startActivity(goToRecipe));

        toTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToTicket);
            }
        });
    }
}