package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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
    ArrayList<String> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    ProgressBar progress;

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

        MyHTTPRequest req = new MyHTTPRequest(); //creates a background thread
        req.execute("http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3");  //Type 1

        //temporarily adding to list view
        elements.add("Recipe 1");
        elements.add("Recipe 2");
        elements.add("Recipe 3");

        //loading saved preferences
        sp =

                getSharedPreferences("searchField", Context.MODE_PRIVATE);

        String search = sp.getString("searchField", "");
        EditText recipeSearch = findViewById(R.id.searchRecipe);
        recipeSearch.setText(search);

        //listener which saves what's typed to saved preferences
        recipeSearch.addTextChangedListener(new

                                                    TextWatcher() {
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
            return elements.get(position);
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
            info.setText(getItem(position).toString());
            return newView;
        }
    }

    private class MyHTTPRequest extends AsyncTask<String, Integer, String> {

        /**
         * Performs a computation in the background.
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

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8"); //response is data from the server

                publishProgress(100);

            } catch (Exception e) {
            }
            return "Done";
        }

        /**
         * This method runs whenever publishProgress is called in doInBackground method.
         * @param values The values indicating the amount of progress that has occurred.
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        /**
         *  Runs after doInBackground has completed.
         * @param fromDoInBackground The String "Done" which is returned from doInBackground
         */
        public void onPostExecute(String fromDoInBackground) {
            Log.i("HTTP", fromDoInBackground);
//            progress.setVisibility(View.INVISIBLE);
        }
    }
}