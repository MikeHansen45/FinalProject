package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class AudioDatabaseActivity extends AppCompatActivity {

    ListView listView;
    Button button;
    ProgressBar progressBar;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_db);

        listView = (ListView) findViewById(R.id.audioDBListView);
        button = (Button) findViewById(R.id.audioDBButton);
        progressBar = (ProgressBar) findViewById(R.id.audioDBProgressBar);
        editText = (EditText) findViewById(R.id.audioDBEditText);

        listView.setOnItemClickListener((parent, view, pos, id) ->{
           AlertDialog.Builder  alertDialogBuilder = new AlertDialog.Builder(this);
           alertDialogBuilder.setTitle("Item");
           alertDialogBuilder.setMessage("This is an item");
           alertDialogBuilder.setPositiveButton("yes", (click, arg) -> {
              //do something
           });
           alertDialogBuilder.setNegativeButton("no", (click, arg)-> {
               // do something else
           });
        });

        button.setOnClickListener(e-> {
            Toast.makeText(this, "You pressed a button", Toast.LENGTH_LONG).show();

            Snackbar snackbar = Snackbar.make(listView, "message", Snackbar.LENGTH_LONG);
            snackbar.show();
        });

    }
}