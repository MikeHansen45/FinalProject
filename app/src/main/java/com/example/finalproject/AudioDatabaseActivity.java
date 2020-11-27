package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class AudioDatabaseActivity extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "AUDIO_DB_ACTIVITY";
    ListView listView;
    Button button;
    ProgressBar progressBar;
    EditText editText;
    //private int currPos = 0;

    //change arraylist object holding type to new audio item object
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
           AlertDialog.Builder  alertDialogBuilder = new AlertDialog.Builder(this);
           alertDialogBuilder.setTitle("Item");
           alertDialogBuilder.setMessage("This is an item");
           alertDialogBuilder.setPositiveButton("yes", (click, arg) -> {
              //do something
           });
           alertDialogBuilder.setNegativeButton("no", (click, arg)-> {
               // do something else
           });
        });

        button.setOnClickListener(e-> {
            //elements = new ArrayList<AudioDBObject>();
            Toast.makeText(this, "You pressed a button", Toast.LENGTH_LONG).show();

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

            saveData();

        });

        loadData();
        updateViews();
        listView.setAdapter(myAdapter);
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
    }

    public void updateViews() {
        editText.setText(text);
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

                myAdapter.notifyDataSetChanged();
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
    }

    protected void deleteObject(AudioDBObject obj) {
        db.delete(AudioDBOpener.TABLE_NAME, AudioDBOpener.COL_ID + "= ?", new String[] {Long.toString(obj.getId())});
    }

    public void printCursor(Cursor c, int version) {
        c.moveToFirst();
        Log.e("DATABASE VERSION NO: ", String.valueOf(db.getVersion()));
        Log.e("NUMBER OF COLUMNS: ", String.valueOf(c.getColumnCount()));

        String[] names = c.getColumnNames();

        for (int i = 0; i < c.getColumnCount(); i++) {
            Log.e("column name: ", names[i]);
        }

        Log.e("NUMBER OF ROWS", String.valueOf(c.getCount()));

        // finish adding column indexes using android labs as example
    }
}