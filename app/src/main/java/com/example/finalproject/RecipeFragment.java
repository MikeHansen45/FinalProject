package com.example.finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public MyRecipeOpener dbOpener = new MyRecipeOpener(RecipeActivity.sContext);
    public SQLiteDatabase db = dbOpener.getWritableDatabase();
    ImageView thumbnail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recipeDetails = inflater.inflate(R.layout.fragment_recipe, container, false);
        Bundle data = getArguments();

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

        String items = data.getString(INGREDIENTS_SELECTED);
        TextView ingredients = recipeDetails.findViewById(R.id.ingredientsView);
        ingredients.setText(items);

        Switch fav = recipeDetails.findViewById(R.id.favRecipeSwitch);
        fav.setSelected(false);
        if (!fav.isSelected()) {
            fav.setOnCheckedChangeListener((a,b) -> {
                long id = saveFavourite(title, url, items, thumbnailURL);
                Snackbar snk = Snackbar.make(fav, (b? R.string.recipeFavSaved:R.string.recipeFavRemoved), snackTime);
                snk.setAction(R.string.undo, click -> {
                    fav.setChecked(!b);
                    deleteRecipe(id);
                })
                        .show();

            });

        }
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
            if (getImage() != null) thumbnail.setImageBitmap(getImage());
            else Log.i("Thumbnail", "Failed to load");
        }
    }


}
