package com.example.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.net.URL;

public class RecipeFragment extends Fragment {

    private static final String TITLE_SELECTED = "Title";
    private static final String LINK_SELECTED = "Link";
    private static final String INGREDIENTS_SELECTED = "Ingredients";
    private static final String THUMBNAIL_SELECTED = "Thumbnail";
    private int snackTime = Snackbar.LENGTH_LONG;
    private int duration = Toast.LENGTH_LONG;
    private MyRecipeOpener dbOpener;
    private SQLiteDatabase db;
    ImageView thumbnail;
    public Toast failed;

    /**
     * creates the fragment which is inflated into the parent activity
     *
     * @param inflater used to inflate the layout
     * @param container the container ViewGroup
     * @param savedInstanceState the previously savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recipeDetails = inflater.inflate(R.layout.fragment_recipe, container, false);
        dbOpener = new MyRecipeOpener(RecipeActivity.sContext);
        db = dbOpener.getWritableDatabase();

        //gets the data passed from the activity
        Bundle data = getArguments();

        //create a toast message that will display if no image was found for each recipe
        failed = Toast.makeText(getActivity(), R.string.toast_message, duration);

        //get the recipe thumbnail URL and use a second async task to load the image from the web
        String thumbnailURL = data.getString(THUMBNAIL_SELECTED);
        MyThumbnailRequest req = new MyThumbnailRequest();
        req.execute(thumbnailURL);
        thumbnail = recipeDetails.findViewById(R.id.thumbnailImage);

        //get the title from bundle
        String title = data.getString(TITLE_SELECTED);
        TextView titleView = recipeDetails.findViewById(R.id.recipeTitle);
        Log.i("adding title", title);
        titleView.setText(title);

        //get the direct link from the bundle and create a listener that opens the browser when clicked
        String url = data.getString(LINK_SELECTED);
        TextView link = recipeDetails.findViewById(R.id.recipeLink);
        link.setText(url);
        link.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
        );

        //get the list of ingredients from the bundle
        String items = data.getString(INGREDIENTS_SELECTED);
        TextView ingredients = recipeDetails.findViewById(R.id.ingredientsView);
        ingredients.setText(items);

        //setting up the favourites switch
        Switch fav = recipeDetails.findViewById(R.id.favRecipeSwitch);

        //checking to see if the recipe already exists in the database and sets the switch to "ON" if it is
        long checkID = isSaved(title);
        Log.i("Recipe Database ID is:", String.valueOf(checkID));
        boolean check = checkID != 0L;
        Log.i("check", String.valueOf(check));
        fav.setChecked(check);

        //Switch listener that deletes the recipe or saves it depending on the toggle state
        //also shows a snackbar to undo the changes
        fav.setOnCheckedChangeListener((a, b) -> {

            Log.i("The switch is", String.valueOf(b));
            final View viewPos = recipeDetails.findViewById(R.id.snackbarlocation);
            Snackbar snk = Snackbar.make(viewPos, (b ? R.string.recipeFavSaved : R.string.recipeFavRemoved), snackTime);
            snk.setAction(R.string.undo, click -> {
                fav.setChecked(!b);
                Log.i("After snk the switch is", String.valueOf(b));
            })
                    .show();
            if (b) {
                saveFavourite(title, url, items, thumbnailURL);
                Log.i("Saved:", title + "added to database");
            } else {
                if (check) deleteRecipe(checkID);
                else deleteRecipe(isSaved(title));
                Log.i("Deleted", "from database");
            }

        });

        //Setting up button that goes back to search
        Button goBack = recipeDetails.findViewById(R.id.backToRecipe);
        goBack.setOnClickListener(v ->
        {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            updateFavList();
        });

        return recipeDetails;
    }

    /**
     * Saves the recipe in this fragment to the database.
     *
     * @param title       the recipe title passed from the bundle
     * @param URL         the direct link to the recipe
     * @param ingredients the list of ingredients
     * @param thumbnail   the thumbnail url from the bundle
     * @return long database id of the newly saved recipe
     */

    public long saveFavourite(String title, String URL, String ingredients, String thumbnail) {
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(MyRecipeOpener.COL_TITLE, title);
        newRowValues.put(MyRecipeOpener.COL_URL, URL);
        newRowValues.put(MyRecipeOpener.COL_INGREDIENTS, ingredients);
        newRowValues.put(MyRecipeOpener.COL_THUMBNAIL, thumbnail);
        return db.insert(MyRecipeOpener.TABLE_NAME, null, newRowValues);
    }

    /**
     * updates the favourites list on the parent activity from the fragment.
     * Only if the parent activity is RecipeFavourites.
     */
    private void updateFavList() {
        boolean favUpdate = getActivity().toString().contains("RecipeFavourites");
        if (favUpdate) {
            ((RecipeFavourites) getActivity()).updateListFromFragment();
        }
    }

    /**
     * Checks to see if the recipe already exists in the database by name. So no two recipes can exist with the same name.
     *
     * @param title the recipe title being searching for in the database
     * @return the database id if found or 0 if not found
     */
    public long isSaved(String title) {
        Long id = 0L;
        String[] columns = {
                MyRecipeOpener.COL_ID, MyRecipeOpener.COL_TITLE, MyRecipeOpener.COL_URL, MyRecipeOpener.COL_INGREDIENTS, MyRecipeOpener.COL_THUMBNAIL
        };
        Cursor searchDB = db.query(MyRecipeOpener.TABLE_NAME, columns, null, null, null, null, null);
        int titleColIndex = searchDB.getColumnIndex(MyRecipeOpener.COL_TITLE);
        int idColIndex = searchDB.getColumnIndex(MyRecipeOpener.COL_ID);
        Boolean matched = false;
        while (!matched && searchDB.moveToNext()) {
            matched = searchDB.getString(titleColIndex).equals(title);
        }
        if (matched) {
            id = searchDB.getLong(idColIndex);
        }
        return id;
    }

    /**
     * Deletes the recipe from the database
     *
     * @param id the database id of the recipe being deleted
     */
    protected void deleteRecipe(long id) {
        db.delete(MyRecipeOpener.TABLE_NAME, MyRecipeOpener.COL_ID + "= ?", new String[]{Long.toString(id)});
    }

    private class MyThumbnailRequest extends AsyncTask<String, Integer, String> {
        private Bitmap image;

        /**
         * Getter method for Bitmap variable
         * @return the Bitmap image
         */
        public Bitmap getImage() {
            return image;
        }

        /**
         * Performs a computation in the background. It is checking for an image from the url provided for each recipe.
         *
         * @param args The URLs that will be connected to in the background.
         * @return null
         */
        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                Log.i("Searching for image:", args[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                Log.i("Thumbnail Request", "Connection established");
                int responseCode = connection.getResponseCode();
                Log.i("Thumbnail Request", String.valueOf(connection.getResponseCode()));
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());

                    publishProgress(100);
                }
            } catch (Exception e) {

            }
            return null;
        }

        /**
         * Uses the results of doInBackground to set the image of the recipe. If the image doesn't exist then a toast is displayed
         * with an error message
         *
         * @param strings
         */
        @Override
        public void onPostExecute(String strings) {
            if (getImage() != null)
                thumbnail.setImageBitmap(Bitmap.createScaledBitmap(getImage(), 530 , 400, false));
            else {
                failed.show();
                Log.i("Thumbnail", "Failed to load");
            }
        }
    }


}
