package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AlbumDisplayActivity extends MainActivity {

    AudioDBObject album;
    ArrayList<TrackObject> elements = new ArrayList<TrackObject>();
    ListView listView;
    TextView albumNameText;
    TextView albumArtistText;
    TextView albumGenreYearText;
    ImageView albumArt;

    albumDisplayAdapter myAdapter = new albumDisplayAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_display);

        album = (AudioDBObject) getIntent().getSerializableExtra("albumObj");

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

        listView = (ListView) findViewById(R.id.albumDisplayList);
        albumNameText = (TextView) findViewById(R.id.albumDisplayAlbumText);
        albumArtistText = (TextView) findViewById(R.id.albumDisplayArtistText);
        albumGenreYearText = (TextView) findViewById(R.id.albumDisplayGenreYear);

        listView.setAdapter(myAdapter);

        albumListTask req = new albumListTask();
        req.execute("https://theaudiodb.com/api/v1/json/1/track.php?m=" + album.getIdAlbum());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });

        albumGenreYearText.setText(album.getGenre() + ", " + album.getYearString());
        albumNameText.setText(album.getAlbum());
        albumArtistText.setText(album.getArtist());
        albumArt = (ImageView) findViewById(R.id.albumDisplayImage);
        new DownloadImageTask(albumArt).execute(album.getAlbumThumb());

        listView.setOnItemClickListener((parent, view, pos, id) -> {
            Uri uri = Uri.parse("http://www.google.com/search?q=" + elements.get(pos).getStrTrack() + "+" + elements.get(pos).getStrArtist());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class albumDisplayAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = convertView;
            TrackObject trackAtPos = elements.get(position);

            newView = getLayoutInflater().inflate(R.layout.track_list_item, null);

            TextView trackText = newView.findViewById(R.id.trackListText);
            trackText.setText(elements.get(position).getStrTrack());

            return newView;
        }
    }

    private class albumListTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL albumURL = new URL(strings[0]);

                HttpURLConnection connection = (HttpURLConnection) albumURL.openConnection();
                InputStream response = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                JSONArray array = obj.getJSONArray("track");

                elements = new ArrayList<TrackObject>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject trackObj = array.getJSONObject(i);
                    String idTrack = trackObj.getString("idTrack");
                    String idAlbum = trackObj.getString("idAlbum");
                    String idArtist = trackObj.getString("idArtist");
                    String strTrack = trackObj.getString("strTrack");
                    String strAlbum = trackObj.getString("strAlbum");
                    String strArtist = trackObj.getString("strArtist");
                    String strGenre = trackObj.getString("strGenre");
                    String strTrackThumb = trackObj.getString("strTrackThumb");

                    TrackObject newTrack = new TrackObject(idTrack, idAlbum, idArtist, strTrack, strAlbum, strArtist, strGenre, strTrackThumb);

                    elements.add(newTrack);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });
            return null;
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


}