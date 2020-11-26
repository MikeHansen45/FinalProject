package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AudioDatabaseActivity extends AppCompatActivity {

    ListView listView;
    Button button;
    ProgressBar progressBar;
    EditText editText;

    ArrayList elements = new ArrayList<>();

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

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

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(TEXT, editText.getText().toString());
            editor.apply();

        });

    }

    class AudioDBAdapter extends BaseAdapter {
        public int getCount() {
            return elements.size();
        }

        public String getItem(int position) {
            return elements.get(position).toString();
        }

        public long getItemId(int position) {
            return (long)position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        }
    }
}