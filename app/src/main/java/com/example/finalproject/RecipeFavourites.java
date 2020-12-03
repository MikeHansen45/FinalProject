package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class RecipeFavourites extends MainActivity {

    private static final String TITLE_SELECTED = "Title";
    private static final String LINK_SELECTED = "Link";
    private static final String INGREDIENTS_SELECTED = "Ingredients";
    private static final String THUMBNAIL_SELECTED = "Thumbnail";
    private static final String ITEM_POSITION = "Position";
    private static final String ITEM_ID = "ID";
    SQLiteDatabase db;
    ArrayList<Recipe> elements = new ArrayList<>();
    private MyRecipeFavAdapter myAdapter = new MyRecipeFavAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favourites);
        boolean isTablet = findViewById(R.id.recipeFragment) != null; //check if the FrameLayout is loaded

        //Set the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        loadDataFromDatabase();
        ListView favList = findViewById(R.id.favList);
        favList.setAdapter(myAdapter);

        favList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE_SELECTED, elements.get(position).getRecipeTitle());
            dataToPass.putString(LINK_SELECTED, elements.get(position).getRecipeLink());
            dataToPass.putString(INGREDIENTS_SELECTED, elements.get(position).getIngredients());
            dataToPass.putString(THUMBNAIL_SELECTED, elements.get(position).getThumbnail());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            RecipeFragment rFragment = new RecipeFragment(); //add a DetailFragment
            rFragment.setArguments(dataToPass); //pass it a bundle for information
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipeFragment, rFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
        });

        Button backToSearch = findViewById(R.id.backToRecipeSearch);
        backToSearch.setOnClickListener(v -> startActivity(new Intent(RecipeFavourites.this, RecipeActivity.class)));
    }

    public void updateListFromFragment() {
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void loadDataFromDatabase() {
        MyRecipeOpener dbOpener = new MyRecipeOpener(this);
        db = dbOpener.getWritableDatabase();
        String[] columns = {
                MyRecipeOpener.COL_ID, MyRecipeOpener.COL_TITLE, MyRecipeOpener.COL_URL, MyRecipeOpener.COL_INGREDIENTS, MyRecipeOpener.COL_THUMBNAIL
        };

        Cursor results = db.query(MyRecipeOpener.TABLE_NAME, columns, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(MyRecipeOpener.COL_ID);
        int titleColIndex = results.getColumnIndex(MyRecipeOpener.COL_TITLE);
        int urlColIndex = results.getColumnIndex(MyRecipeOpener.COL_URL);
        int ingredientsColIndex = results.getColumnIndex(MyRecipeOpener.COL_INGREDIENTS);
        int thumbnailColIndex = results.getColumnIndex(MyRecipeOpener.COL_THUMBNAIL);

        while (results.moveToNext()) {
            String title = results.getString(titleColIndex);
            String url = results.getString(urlColIndex);
            String ingredients = results.getString(ingredientsColIndex);
            String thumbnail = results.getString(thumbnailColIndex);
            long id = results.getLong(idColIndex);
            elements.add(new Recipe(id, title, url, ingredients, thumbnail));
        }
        printCursor(results, MyRecipeOpener.VERSION_NUM);
    }

    private void printCursor(Cursor c, int version) {
        String names = "";
        for (String col : c.getColumnNames()) {
            names += col + ", ";
        }
        String printMessage = ("Database Version Number: " + db.getVersion() + ", Number of Columns: " + c.getColumnCount() + ", Column Names: " + names + "Number of Rows: " + c.getCount());
        Log.i(MyRecipeOpener.DATABASE_NAME, printMessage);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                long idColIndex = c.getLong(c.getColumnIndex(MyRecipeOpener.COL_ID));
                String title = c.getString(c.getColumnIndex(MyRecipeOpener.COL_TITLE));
                String urlDB = c.getString(c.getColumnIndex(MyRecipeOpener.COL_URL));
                String ingredients = c.getString(c.getColumnIndex(MyRecipeOpener.COL_INGREDIENTS));
                String thumbnail = c.getString(c.getColumnIndex(MyRecipeOpener.COL_THUMBNAIL));
                Log.i(MyRecipeOpener.DATABASE_NAME, "ID: " + idColIndex + ", Title: " + title + ", URL: " + urlDB + ", ingredients" + ingredients + ", thumbnail" + thumbnail);
                c.moveToNext();
            }
        }

    }

    protected void deleteMessage(Recipe r) {
        db.delete(MyRecipeOpener.TABLE_NAME, MyRecipeOpener.COL_ID + "= ?", new String[]{Long.toString(r.getId())});
    }

    private class MyRecipeFavAdapter extends BaseAdapter {

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
            return (long) elements.get(position).getId();
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
}