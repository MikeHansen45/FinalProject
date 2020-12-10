package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class AudioDatabaseActivity extends MainActivity {
    public static final String ACTIVITY_NAME = "AUDIO_DB_ACTIVITY";
    ListView listView;
    Button button;
    ProgressBar progressBar;
    EditText editText;
    //private int currPos = 0;

    ArrayList<AudioDBObject> elements = new ArrayList<AudioDBObject>();

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String AUDIO_OBJ = "audioObj";
    private String text;

    private SQLiteDatabase db;
    private AudioDBOpener MyDatabaseHelper;
    protected Cursor results;

    AudioDBAdapter myAdapter = new AudioDBAdapter();

    String idAlbum;
    String idArtist;
    String strAlbum;
    String strArtist;
    String intYearReleased;
    String strGenre;
    String strAlbumThumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_db);

        listView = (ListView) findViewById(R.id.audioDBListView);
        button = (Button) findViewById(R.id.audioDBButton);
        progressBar = (ProgressBar) findViewById(R.id.audioDBProgressBar);
        editText = (EditText) findViewById(R.id.audioDBEditText);
        listView.setAdapter(myAdapter);

        loadDataFromDatabase();

        listView.setOnItemClickListener((parent, view, pos, id) ->{
            String albumID = elements.get(pos).getIdAlbum();
            String albumArtURL = elements.get(pos).getAlbumThumb();

            Intent i = new Intent(AudioDatabaseActivity.this, AlbumDisplayActivity.class);
            //i.putExtra("albumID", albumID);
            i.putExtra("albumObj", elements.get(pos));
            startActivity(i);
        });

        button.setOnClickListener(e-> {
            elements = new ArrayList<AudioDBObject>();
            Snackbar snackbar = Snackbar.make(listView, "searching for: " + editText.getText().toString(), Snackbar.LENGTH_LONG);
            snackbar.show();


            //dont forget to add to database and to get the id for each object and set it
            //progressBar.setVisibility(View.VISIBLE);
            audioSearchTask req = new audioSearchTask();
            req.execute("https://www.theaudiodb.com/api/v1/json/1/searchalbum.php?s=" + editText.getText());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });

            Toast.makeText(this, R.string.audioDBResultsCount + elements.size(), Toast.LENGTH_LONG).show();

            saveData();

            });

        //Set the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // CHANGE THESE STRINGS
        myToolbar.setTitle(R.string.toAudioButtonText);
        myToolbar.setSubtitle(R.string.audioAuthor);
        setSupportActionBar(myToolbar);
        //For NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        loadData();
        updateViews();
        listView.setAdapter(myAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem helpButton = menu.findItem(R.id.menu1);
        helpButton.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        item.setOnMenuItemClickListener(v -> {
            alertDialogBuilder
                    .setTitle(R.string.audioHelp)
                    .setMessage(R.string.audioHelpMessage)
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

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, editText.getText().toString());
        // use google GSON if its ok to shared prefs the arraylist?
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
        audioSearchTask req = new audioSearchTask();
        req.execute("https://www.theaudiodb.com/api/v1/json/1/searchalbum.php?s=" + text);

    }

    public void updateViews() {
        editText.setText(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
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
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.audio_list_item, parent, false);

            TextView listItem = newView.findViewById(R.id.audioItemText);
            ImageView albumArt = newView.findViewById(R.id.audioItemPicture);
            Log.e(ACTIVITY_NAME, "getView: elements: " + elements.toString());
            listItem.setText(elements.get(position).toString());
            if (elements.get(position) != null) {
                //currPos = position;
                new DownloadImageTask(albumArt).execute(elements.get(position).getAlbumThumb());
                //currPos = 0;
            }

            return newView;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
                //elements.get(currPos).setAlbumArtBMP(bmp);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class audioSearchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL audioURL = new URL(strings[0]);

                HttpURLConnection audioDBConnection = (HttpURLConnection) audioURL.openConnection();
                InputStream audioResponse = audioDBConnection.getInputStream();

                BufferedReader audioReader = new BufferedReader(new InputStreamReader(audioResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = audioReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String audioResult = sb.toString();

                JSONObject audioObj = new JSONObject(audioResult);
                JSONArray audioArray = audioObj.getJSONArray("album");

                elements = new ArrayList<AudioDBObject>();
                for (int i = 0; i < audioArray.length(); i++) {
                    JSONObject obj = audioArray.getJSONObject(i);
                    //add new audio item class object and its attributes
                    idAlbum = obj.getString("idAlbum");
                    idArtist = obj.getString("idArtist");
                    strAlbum = obj.getString("strAlbum");
                    strArtist = obj.getString("strArtist");
                    intYearReleased = obj.getString("intYearReleased");
                    strGenre = obj.getString("strGenre");
                    strAlbumThumb = obj.getString("strAlbumThumb");
                    Log.e(ACTIVITY_NAME, "album: " + idAlbum);
                    elements.add(new AudioDBObject(idAlbum, idArtist, strAlbum, strArtist, intYearReleased, strGenre, strAlbumThumb));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    myAdapter.notifyDataSetChanged();

                }
            });
            //Log.e(ACTIVITY_NAME, "elements: " + elements.toString());
            return null;
        }
    }

    private void loadDataFromDatabase() {
        // finish from android labs
        AudioDBOpener dbOpener = new AudioDBOpener(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {AudioDBOpener.COL_ID, AudioDBOpener.COL_ALBUM_ID, AudioDBOpener.COL_ARTIST_ID,
                AudioDBOpener.COL_ALBUM_NAME, AudioDBOpener.COL_ARTIST_NAME, AudioDBOpener.COL_RELEASE_YEAR,
                AudioDBOpener.COL_GENRE, AudioDBOpener.COL_ALBUM_ART};
        Cursor results = db.query(false, AudioDBOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(AudioDBOpener.COL_ID);
        int albumIDColIndex = results.getColumnIndex(AudioDBOpener.COL_ALBUM_ID);
        int artistIDColIndex = results.getColumnIndex(AudioDBOpener.COL_ARTIST_ID);
        int albumNameColIndex = results.getColumnIndex(AudioDBOpener.COL_ALBUM_NAME);
        int artistNameColIndex = results.getColumnIndex(AudioDBOpener.COL_ARTIST_NAME);
        int releaseYearColIndex = results.getColumnIndex(AudioDBOpener.COL_RELEASE_YEAR);
        int genreColIndex = results.getColumnIndex(AudioDBOpener.COL_GENRE);
        int albumArtColIndex = results.getColumnIndex(AudioDBOpener.COL_ALBUM_ART);

        while (results.moveToNext()) {
            long id = results.getLong(idColIndex);
            String albumID = results.getString(albumIDColIndex);
            String artistID = results.getString(artistIDColIndex);
            String albumName = results.getString(albumNameColIndex);
            String artistName = results.getString(artistNameColIndex);
            String releaseYear = results.getString(releaseYearColIndex);
            String genre = results.getString(genreColIndex);
            String albumArt = results.getString(albumArtColIndex);

            AudioDBObject newEntry = new AudioDBObject(albumID, artistID, albumName, artistName, releaseYear, genre, albumArt, id);
            elements.add(newEntry);
        }

        printCursor(results, db.getVersion());
    }

    protected void deleteObject(AudioDBObject obj) {
        db.delete(AudioDBOpener.TABLE_NAME, AudioDBOpener.COL_ID + "= ?", new String[] {Long.toString(obj.getId())});
    }

    public void printCursor(Cursor c, int version) {
        c.moveToFirst();
        Log.e("DATABASE VERSION NO: ", String.valueOf(version));
        Log.e("NUMBER OF COLUMNS: ", String.valueOf(c.getColumnCount()));

        String[] names = c.getColumnNames();

        for (int i = 0; i < c.getColumnCount(); i++) {
            Log.e("column name: ", names[i]);
        }

        Log.e("NUMBER OF ROWS", String.valueOf(c.getCount()));

        // finish adding column indexes using android labs as example

        int idColIndex = c.getColumnIndex(AudioDBOpener.COL_ID);
        int albumIDColIndex = c.getColumnIndex(AudioDBOpener.COL_ALBUM_ID);
        int artistIDColIndex = c.getColumnIndex(AudioDBOpener.COL_ARTIST_ID);
        int albumNameColIndex = c.getColumnIndex(AudioDBOpener.COL_ALBUM_NAME);
        int artistNameColIndex = c.getColumnIndex(AudioDBOpener.COL_ARTIST_NAME);
        int releaseYearColIndex = c.getColumnIndex(AudioDBOpener.COL_RELEASE_YEAR);
        int genreColIndex = c.getColumnIndex(AudioDBOpener.COL_GENRE);
        int albumArtColIndex = c.getColumnIndex(AudioDBOpener.COL_ALBUM_ART);

        while (c.moveToNext()) {
            long id = c.getLong(idColIndex);
            String albumID = c.getString(albumIDColIndex);
            String artistID = c.getString(artistIDColIndex);
            String albumName = c.getString(albumNameColIndex);
            String artistName = c.getString(artistNameColIndex);
            String releaseYear = c.getString(releaseYearColIndex);
            String genre = c.getString(genreColIndex);
            String albumArt = c.getString(albumArtColIndex);

            String s = String.valueOf(id) + " album id: " + albumID + " artist id: " + artistID +
                    " album name: " + albumName +
                    " artist name: " + artistName + " release year: " + releaseYear + " genre: " +
                    genre + " album art url: " + albumArt;

            Log.e("row: ", s);
        }

    }
}