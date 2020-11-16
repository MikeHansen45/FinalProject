package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class CovidData extends AppCompatActivity {

    CovListAdapter CovAdt = new  CovListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data);

        Button Covidbtn = findViewById(R.id.Covbtn);
        ListView Covidlist = findViewById(R.id.Covlist);

        Covidbtn.setOnClickListener( click -> {
            Snackbar.make(Covidbtn, "Hello", Snackbar.LENGTH_LONG).show();
            Toast.makeText(this, "Hello - Toast", Toast.LENGTH_SHORT).show();
        });

        Covidlist.setAdapter(CovAdt);

        Covidlist.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Covid data")
                    .setMessage("This is covid data")
                    .setNegativeButton("Cancel", (click, arg) -> {})
                    .create().show();
        });

    }

    class CovListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public String getItem(int position) {
            return "Covid" + position;
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView getCov = new TextView(CovidData.this);
            getCov.setText(getItem(position));
            return getCov;
        }
    }
}