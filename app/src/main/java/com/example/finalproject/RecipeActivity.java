package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

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
    private MyListAdapter myRecipeAdapter = new MyListAdapter();
    private static final String INGREDIENTS_SELECTED = "Ingredients";
    private static final String THUMBNAIL_SELECTED = "Thumbnail";
    private static final String ITEM_POSITION = "Position";
    private static final String TITLE_SELECTED = "Title";
    private static final String LINK_SELECTED = "Link";
    ArrayList<Recipe> elements = new ArrayList<>();
    private static final String ITEM_ID = "ID";
    public static Context sContext;
    SharedPreferences sp = null;
    ProgressBar progress;
    AlertDialog alt;
    String q = "";
    String i = "";

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * Always followed by onStart().
     *
     * @param savedInstanceState the previously saved instanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //getting the context for the fragments to access the database later
        sContext = getApplicationContext();

        //making the help alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alt = alertDialogBuilder
                .setTitle(R.string.recipeHelp)
                .setMessage(R.string.recipeHelpMessage)
                .setNeutralButton(R.string.ok, (click, arg) -> {
                })
                .create();

        //Set the toolbar, title, and subtitle
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.recipeTitle);
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

        //progress bar
        progress = findViewById(R.id.recipeProgressBar);
        progress.setVisibility(View.INVISIBLE);

        //go to favourites button
        Button toFavs = findViewById(R.id.buttonRecipe);
        Intent goToRecipeFavs = new Intent(this, RecipeFavourites.class);
        toFavs.setOnClickListener(v -> startActivity(goToRecipeFavs));

        //getting the strings from saved preferences
        sp = getSharedPreferences("searchField", Context.MODE_PRIVATE);
        String search = sp.getString("searchField", "");
        String ingredients = sp.getString("ingredientsField", "");

        //setting up the searchViews
        SearchView recipeSearch = findViewById(R.id.searchRecipe);
        SearchView ingredientsSearch = findViewById(R.id.searchIngredients);
        recipeSearch.setQuery(search, false);
        ingredientsSearch.setQuery(ingredients, false);

        //listener which saves what's typed to saved preferences
        //starts async task to search for query
        //updates listView once complete
        recipeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                q = query;
                if (ingredientsSearch.getQuery() != null)
                    i = ingredientsSearch.getQuery().toString();
                Log.i("RecipeSearch:", ingredientsSearch.getQuery().toString());
                String searchURL = "http://www.recipepuppy.com/api/?i=" + i + "&q=" + q + "&p=3";
                MyHTTPRecipeRequest req = new MyHTTPRecipeRequest(); //creates a background thread
                req.execute(searchURL);
                myRecipeAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                saveSharedPrefs(newText, true);
                return false;
            }
        });

        //same listener but for second searchView
        ingredientsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                i = query;
                if (recipeSearch.getQuery() != null) q = recipeSearch.getQuery().toString();
                Log.i("RecipeSearch:", recipeSearch.getQuery().toString());
                String searchURL = "http://www.recipepuppy.com/api/?i=" + i + "&q=" + q + "&p=3&";
                MyHTTPRecipeRequest req = new MyHTTPRecipeRequest(); //creates a background thread
                req.execute(searchURL);
                myRecipeAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                saveSharedPrefs(newText, false);
                return false;
            }
        });

        //setting up the listView
        ListView results = findViewById(R.id.recipeList);
        results.setAdapter(myRecipeAdapter);

        //listener for listView that opens recipeDetail fragment
        results.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE_SELECTED, elements.get(position).getRecipeTitle());
            dataToPass.putString(LINK_SELECTED, elements.get(position).getRecipeLink());
            dataToPass.putString(INGREDIENTS_SELECTED, elements.get(position).getIngredients());
            dataToPass.putString(THUMBNAIL_SELECTED, elements.get(position).getThumbnail());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            RecipeFragment rFragment = new RecipeFragment(); //create fragment object
            rFragment.setArguments(dataToPass); //pass it a bundle for information
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipeFragment, rFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.

        });

    }

    /**
     * This method creates the toolbar menu.
     * @param menu The menu object used to create the toolbar
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem helpButton = menu.findItem(R.id.menu1);
        helpButton.setVisible(true);
        return true;
    }

    /**
     * Listener for items on the toolbar
     * @param item the buttons on the toolbar menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu1) {
            alt.show();
        }
        return true;
    }

    /**
     * Listener for items in the navigation menu.
     * @param item the menu items in the navigation drawer
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    /**
     * Saves a string into a SharedPreferences object so that it appear each time this activity is visited.
     *
     * @param a The string that will be saved
     */
    private void saveSharedPrefs(String a, boolean b) {
        SharedPreferences.Editor editor = sp.edit();
        if (b) editor.putString("searchField", a);
        else editor.putString("ingredientsField", a);
        editor.commit();
    }

    private class MyListAdapter extends BaseAdapter {

        /**
         * Returns the size of the arraylist that will be loaded in to the ListView
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
         * This method inflates the ListView with a View
         * @param position    The index of the item that will be returned
         * @param convertView The ListView that will hold the items
         * @param parent the parent ViewGroup
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

                String line;
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


        /**
         * This method runs after the completion of doInBackground. The results can be modified here for usage in the application.
         * @param result The results from doInBackground
         */
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
                    //Log.i("Titles", oldTitle);
                    title = recipes.getString("title").replace("&quot;", "'''").replace("&amp;", "&");
                    href = recipes.getString("href");
                    ingredients = recipes.getString("ingredients");

                    elements.add(new Recipe(title, href, ingredients, thumbnailURL));
                    Log.i("Recipe Objects made: ", elements.toString());
                }
            } catch (JSONException e) {

            }
            myRecipeAdapter.notifyDataSetChanged();
            progress.setVisibility(View.INVISIBLE);
        }
    }
}