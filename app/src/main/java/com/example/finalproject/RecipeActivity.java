package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    private int duration = Toast.LENGTH_LONG;
    private int snackTime = Snackbar.LENGTH_LONG;
    SharedPreferences sp = null;
    ArrayList<String> elements = new ArrayList<>();
    private MyListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //temporarily adding to list view
        elements.add("Recipe 1");
        elements.add("Recipe 2");
        elements.add("Recipe 3");

        //loading saved preferences
        sp = getSharedPreferences("searchField", Context.MODE_PRIVATE);
        String search = sp.getString("searchField", "");
        EditText recipeSearch = findViewById(R.id.searchRecipe);
        recipeSearch.setText(search);

        //listener which saves what's typed to saved preferences
        recipeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveSharedPrefs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Recipe Button
        Button btn = findViewById(R.id.buttonRecipe);

        //Toast and snackbar on the button
        btn.setOnClickListener(v -> {
            Toast.makeText(this, R.string.toast_message, duration).show();
            Snackbar.make(btn, R.string.snack_message, snackTime).show();
        });

        ListView results = findViewById(R.id.recipeList);
        results.setAdapter(myAdapter = new MyListAdapter());

        //alert dialog which shows more info about the recipe
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //long click listener which triggers alert dialog
        results.setOnItemLongClickListener((parent, view, pos, id) -> {
            alertDialogBuilder
                    .setTitle("Recipe Title")
                    .setMessage("Info")
                    .create().show();
            return true;
        });

    }

    private void saveSharedPrefs (String a){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("searchField", a);
        editor.commit();
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView;
            newView = inflater.inflate(R.layout.recipe_list, parent, false);
            TextView info = newView.findViewById(R.id.recipeInfo);
            info.setText(getItem(position).toString());
            return newView;
        }
    }
}