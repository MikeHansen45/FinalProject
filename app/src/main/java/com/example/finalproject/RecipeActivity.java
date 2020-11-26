package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    private int duration = Toast.LENGTH_LONG;
    private int snackTime = Snackbar.LENGTH_LONG;
    SharedPreferences sp = null;
    ArrayList<Recipe> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    ProgressBar progress;
    String q = "";
    String i = "";

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * Always followed by onStart().
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        progress = findViewById(R.id.recipeProgressBar);
        progress.setVisibility(View.VISIBLE);

        //loading saved preferences
        sp =

                getSharedPreferences("searchField", Context.MODE_PRIVATE);

        String search = sp.getString("searchField", "");
        SearchView recipeSearch = findViewById(R.id.searchRecipe);
        recipeSearch.setQuery(search, false);

        //listener which saves what's typed to saved preferences
        recipeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                q = query;
                String searchURL = "http://www.recipepuppy.com/api/?format=JSON,i=" + i + "&q=" + q + "&p=3";
                // need to make another new request if we want to run another search
                MyHTTPRecipeRequest req = new MyHTTPRecipeRequest(); //creates a background thread
                req.execute(searchURL);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                saveSharedPrefs(newText);
                return false;
            }
        });

        //Recipe Button
        Button btn = findViewById(R.id.buttonRecipe);

        //Toast and snackbar on the button
        btn.setOnClickListener(v ->

        {
            Toast.makeText(this, R.string.toast_message, duration).show();
            Snackbar.make(btn, R.string.snack_message, snackTime).show();
        });

        ListView results = findViewById(R.id.recipeList);
        results.setAdapter(myAdapter = new

                MyListAdapter());

        //alert dialog which shows more info about the recipe
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //long click listener which triggers alert dialog
        results.setOnItemLongClickListener((parent, view, pos, id) ->

        {
            alertDialogBuilder
                    .setTitle("Recipe Title")
                    .setMessage("Info")
                    .create().show();
            return true;
        });

    }

    /**
     * Saves a string into a SharedPreferences object so that it appear each time this activity is visited.
     *
     * @param a The string that will be saved
     */
    private void saveSharedPrefs(String a) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("searchField", a);
        editor.commit();
    }

    private class MyListAdapter extends BaseAdapter {

        /**
         * @return the size of the arrayList
         */
        @Override
        public int getCount() {
            return elements.size();
        }

        /**
         * @param position The index of the item that will be returned
         * @return the item stored in the arrayList
         */
        @Override
        public Object getItem(int position) {
            return elements.get(position).getRecipeTitle();
        }

        /**
         * @param position The index of the item that will be returned
         * @return The database id of the object
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * @param position    The index of the item that will be returned
         * @param convertView The ListView that will hold the items
         * @param parent
         * @return The view that will be inflated to the ListView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView;
            newView = inflater.inflate(R.layout.recipe_list, parent, false);
            TextView info = newView.findViewById(R.id.recipeInfo);
            String title = (String)getItem(position);
            info.setText(title);
            return newView;
        }
    }

    private class MyHTTPRecipeRequest extends AsyncTask<String, Integer, String> {

        /**
         * Performs a computation in the background.
         *
         * @param args The URLs that will be connected to in the background.
         * @return The string "Done"
         */
        public String doInBackground(String... args) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading:   Look at slide 26
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string


                // convert string to JSON: Look at slide 27:
                JSONObject recipeObject = new JSONObject(result);

                //get the double associated with "value"
                JSONArray recipeResults = recipeObject.getJSONArray("results");
                Log.i("", recipeResults.toString());
                onProgressUpdate(25);

                for (int i = 0; i < recipeResults.length(); i++)
                    try {
                        JSONObject recipes = recipeResults.getJSONObject(i);
                        // Pulling items from the array
                        elements.add(new Recipe(recipes.getString("title"), recipes.getString("href"), recipes.getString("ingredients")));
                        Log.i("", elements.toString());
                        onProgressUpdate(100);

                    } catch (JSONException e) {
                        // handle the exception
                    }


            } catch (Exception e) {
            }
            return "Done";
        }

        /**
         * This method runs whenever publishProgress is called in doInBackground method.
         *
         * @param values The values indicating the amount of progress that has occurred.
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        /**
         * Runs after doInBackground has completed.
         *
         * @param fromDoInBackground The String "Done" which is returned from doInBackground
         */
        public void onPostExecute(String fromDoInBackground) {
            Log.i("HTTP", fromDoInBackground);
            myAdapter.notifyDataSetChanged();
            progress.setVisibility(View.INVISIBLE);
        }
    }
}