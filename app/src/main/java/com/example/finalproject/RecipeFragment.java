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
    private static final String ITEM_POSITION = "Position";
    private static final String ITEM_ID = "ID";
    private int snackTime = Snackbar.LENGTH_LONG;
    private int duration = Toast.LENGTH_LONG;
    public static final String COL_TITLE = "TITLE";
    public static final String COL_URL = "URL";
    public static final String COL_INGREDIENTS = "INGREDIENTS";
    public static final String COL_THUMBNAIL = "THUMBNAIL";
    public static final String DATABASE_NAME = "RecipeDB";
    public MyRecipeOpener dbOpener = new MyRecipeOpener(RecipeActivity.sContext);
    public SQLiteDatabase db = dbOpener.getWritableDatabase();
    ImageView thumbnail;
    public Toast failed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recipeDetails = inflater.inflate(R.layout.fragment_recipe, container, false);
        Log.i("Container is:", container.toString());
        Log.i("getActivity returns:", getActivity().toString());
        Bundle data = getArguments();

        failed = Toast.makeText(getActivity(), R.string.toast_message, duration);

        String thumbnailURL = data.getString(THUMBNAIL_SELECTED);
        MyThumbnailRequest req = new MyThumbnailRequest();
        req.execute(thumbnailURL);
        thumbnail = recipeDetails.findViewById(R.id.thumbnailImage);

        String title = data.getString(TITLE_SELECTED);
        TextView titleView = recipeDetails.findViewById(R.id.recipeTitle);
        Log.i("adding title", title);
        titleView.setText(title);

        String url = data.getString(LINK_SELECTED);
        TextView link = recipeDetails.findViewById(R.id.recipeLink);
        link.setText(url);
        link.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
        );

        String items = data.getString(INGREDIENTS_SELECTED);
        TextView ingredients = recipeDetails.findViewById(R.id.ingredientsView);
        ingredients.setText(items);

        Switch fav = recipeDetails.findViewById(R.id.favRecipeSwitch);
        long checkID = isSaved(title);
        Log.i("Recipe Database ID is:", String.valueOf(checkID));
        boolean check = checkID != 0L;
        Log.i("check", String.valueOf(check));
        fav.setChecked(check);

        if (!fav.isChecked()) {
            Log.i("The switch is", "off");
            fav.setOnCheckedChangeListener((a, b) -> {
                saveFavourite(title, url, items, thumbnailURL);
                Log.i("Saved:", "added to database");
                Snackbar snk = Snackbar.make(fav, (b ? R.string.recipeFavSaved : R.string.recipeFavRemoved), snackTime);
                snk.setAction(R.string.undo, click -> {
                    fav.setChecked(!b);
                    if (check) deleteRecipe(checkID);
                    else deleteRecipe(isSaved(title));
                })
                        .show();
            });


        } else {
            Log.i("The switch is", "on");
            fav.setOnCheckedChangeListener((a, b) -> {
                if (check) deleteRecipe(checkID);
                else deleteRecipe(isSaved(title));
                Log.i("Deleted", "from database");
                Snackbar snk = Snackbar.make(fav, (b ? R.string.recipeFavSaved : R.string.recipeFavRemoved), snackTime);
                snk.setAction(R.string.undo, click -> {
                    fav.setChecked(!b);
                    saveFavourite(title, url, items, thumbnailURL);
                })
                        .show();
            });
        }

        Button goBack = recipeDetails.findViewById(R.id.backToRecipe);
        goBack.setOnClickListener(v ->
        {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            updateFavList();
        });
        return recipeDetails;
    }

    public long saveFavourite(String title, String URL, String ingredients, String thumbnail) {
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(MyRecipeOpener.COL_TITLE, title);
        newRowValues.put(MyRecipeOpener.COL_URL, URL);
        newRowValues.put(MyRecipeOpener.COL_INGREDIENTS, ingredients);
        newRowValues.put(MyRecipeOpener.COL_THUMBNAIL, thumbnail);
        return db.insert(MyRecipeOpener.TABLE_NAME, null, newRowValues);
    }

    private void updateFavList() {
        boolean favUpdate = getActivity().toString().contains("RecipeFavourites");
        Log.i("The activity is", getActivity().toString());
        Log.i("update fave?", String.valueOf(favUpdate));
        if (getActivity().toString().contains("RecipeFavourites")) {
            ((RecipeFavourites) getActivity()).updateListFromFragment();
        }
    }

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

    protected void deleteRecipe(long id) {
        db.delete(MyRecipeOpener.TABLE_NAME, MyRecipeOpener.COL_ID + "= ?", new String[]{Long.toString(id)});
    }

    private class MyThumbnailRequest extends AsyncTask<String, Integer, String> {
        private Bitmap image;

        public Bitmap getImage() {
            return image;
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
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

        @Override
        public void onPostExecute(String strings) {
            if (getImage() != null)
                thumbnail.setImageBitmap(Bitmap.createScaledBitmap(getImage(), 500, 500, false));
            else {
                failed.show();
                Log.i("Thumbnail", "Failed to load");
            }
        }
    }


}
