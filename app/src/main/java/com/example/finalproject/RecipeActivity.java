package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeActivity extends MainActivity {
    private static final String TITLE_SELECTED = "Title";
    private static final String LINK_SELECTED = "Link";
    private static final String INGREDIENTS_SELECTED = "Ingredients";
    private static final String THUMBNAIL_SELECTED = "Thumbnail";
    private static final String ITEM_POSITION = "Position";
    private static final String ITEM_ID = "ID";
    SharedPreferences sp = null;
    ArrayList<Recipe> elements = new ArrayList<>();
    private MyListAdapter myAdapter = new MyListAdapter();
    ProgressBar progress;
    String q = "";
    String i = "";
    public static Context sContext;

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
        sContext = getApplicationContext();

        //Set the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.goToRecipe);
        myToolbar.setSubtitle(R.string.recipeAuthor);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        progress = findViewById(R.id.recipeProgressBar);
        progress.setVisibility(View.INVISIBLE);
        boolean isTablet = findViewById(R.id.recipeFragment) != null; //check if the FrameLayout is loaded

        Button toFavs = findViewById(R.id.buttonRecipe);
        Intent goToRecipeFavs = new Intent(this, RecipeFavourites.class);
        toFavs.setOnClickListener(v -> startActivity(goToRecipeFavs));

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
                MyHTTPRecipeRequest req = new MyHTTPRecipeRequest(); //creates a background thread
                req.execute(searchURL);
                myAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                saveSharedPrefs(newText);
                return false;
            }
        });

        ListView results = findViewById(R.id.recipeList);
        results.setAdapter(myAdapter);

        results.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE_SELECTED, elements.get(position).getRecipeTitle());
            dataToPass.putString(LINK_SELECTED, elements.get(position).getRecipeLink());
            dataToPass.putString(INGREDIENTS_SELECTED, elements.get(position).getIngredients());
            dataToPass.putString(THUMBNAIL_SELECTED, elements.get(position).getThumbnail());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if (isTablet) {
                RecipeFragment rFragment = new RecipeFragment(); //add a DetailFragment
                rFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipeFragment, rFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            } else //isPhone
            {
                Intent nextActivity = new Intent(RecipeActivity.this, EmptyRecipeActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem helpButton = menu.findItem(R.id.menu1);
        helpButton.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        item.setOnMenuItemClickListener(v -> {
            alertDialogBuilder
                    .setTitle(R.string.recipeHelp)
                    .setMessage(R.string.recipeHelpMessage)
                    .setNeutralButton(R.string.ok, (click, arg) -> {
                    })
                    .create().show();
            return true;
        });

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
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
            String title = (String) getItem(position);
            info.setText(title);
            return newView;
        }

    }

    private class MyHTTPRecipeRequest extends AsyncTask<String, Integer, String> {

        private String thumbnailURL;
        private String title;
        private String ingredients;
        private String href;

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
                publishProgress(25);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                publishProgress(50);
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

                Log.i("Results from JSON: ", result);

                publishProgress(100);
                return result;

            } catch (Exception e) {
            }

            return "Nothing";
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


        public void onPostExecute(String result) {
            if (elements != null) elements.clear();
            try {
                JSONObject recipeObject = new JSONObject(result);

                JSONArray recipeResults = recipeObject.getJSONArray("results");
                Log.i("JSON Array Contents", recipeResults.toString());

                for (int i = 0; i < recipeResults.length(); i++) {
                    JSONObject recipes = recipeResults.getJSONObject(i);
                    // Pulling items from the array

                    thumbnailURL = recipes.getString("thumbnail");
                    title = recipes.getString("title");
                    href = recipes.getString("href");
                    ingredients = recipes.getString("ingredients");

                    elements.add(new Recipe(title, href, ingredients, thumbnailURL));
                    Log.i("Recipe Objects made: ", elements.toString());
                }
            } catch (JSONException e) {

            }
            myAdapter.notifyDataSetChanged();
            progress.setVisibility(View.INVISIBLE);
        }
    }
}