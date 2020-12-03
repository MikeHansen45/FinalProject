package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CovidData extends MainActivity {

    CovListAdapter CovAdt = new CovListAdapter();
    ArrayList<Covid> CovArray = new ArrayList();
    Covid c;
    String country;
    String countryCode;
    String province;
    String city;
    int cases;
    SharedPreferences cvprefs = null;
    CovidOpener cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data);

        Button Covidbtn = findViewById(R.id.Covbtn);
        Button CovSearchbtn = findViewById(R.id.Resultbtn);
        ListView Covidlist = findViewById(R.id.Covlist);
        EditText edt = findViewById(R.id.cvedt);
        Covidlist.setAdapter(CovAdt);

        cvprefs = getSharedPreferences("Input_Data", Context.MODE_PRIVATE);
        String cvsavedString = cvprefs.getString("COUNTRY", "");

        edt.setText(cvsavedString);

        //Set the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        CovSearchbtn.setOnClickListener(click -> {
            //Declare and execute a search query
            CovidQuery cq = new CovidQuery();
            cq.execute("https://api.covid19api.com/country/" + edt.getText() + "/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
            CovAdt.notifyDataSetChanged();

            //Saves the query from the edit text into the file
            SharedPreferences.Editor editor = cvprefs.edit();
            String stringToSave = edt.getText().toString();
            editor.putString("COUNTRY", stringToSave);
            editor.apply();
        });

        Covidbtn.setOnClickListener(click -> {
            Snackbar.make(Covidbtn, "Hello", Snackbar.LENGTH_LONG).show();//Creates snackbar with long lenght
            Toast.makeText(this, "Hello - Toast", Toast.LENGTH_SHORT).show();//Creates toast with short duration
        });


        Covidlist.setOnItemClickListener((parent, view, position, id) -> {
            //Creating alert
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.cvalert_t))
                    .setMessage(getString(R.string.cvalert_mess))
                    .setNegativeButton(getString(R.string.cvalert_negative), (click, arg) -> {})//Cancels the alert
                    .create().show();
        });

        /*CovidQuery cq = new CovidQuery();
        cq.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00ZV");
        CovAdt.notifyDataSetChanged();*/
        Covidlist.setAdapter(CovAdt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        return super.onNavigationItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    public class CovidQuery extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL covurl = new URL(strings[0]);
                //Create connection
                HttpURLConnection covurlConnection = (HttpURLConnection) covurl.openConnection();
                InputStream covresponse = covurlConnection.getInputStream();

                //Get input stream
                BufferedReader covreader = new BufferedReader(new InputStreamReader(covresponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = covreader.readLine()) != null){
                    sb.append(line + "\n");
                }
                String covresult = sb.toString();

                //Declare JSON array
                JSONArray covobj = new JSONArray(covresult);

                //https://www.semicolonworld.com/question/47685/get-jsonarray-without-array-name
                //Loop through the array to add elements to a list view
                for(int i=0;i<covobj.length();i++){
                    JSONObject obj = covobj.getJSONObject(i);
                country = obj.getString("Country");
                countryCode = obj.getString("CountryCode");
                province = obj.getString("Province");
                city = obj.getString("City");
                cases = obj.getInt("Cases");
                    CovArray.add(new Covid(countryCode, province, city, cases));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class CovListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CovArray.size();
        }
        //Returns elements in the list based on the array size

        @Override
        public Covid getItem(int position) {
            return CovArray.get(position);
        }//Item returned in the list

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater covinflater = getLayoutInflater();
            View covview = covinflater.inflate(R.layout.covid_data_row, parent, false);
            TextView getCov = covview.findViewById(R.id.coviddatatv);
            getCov.setText(getItem(position).toString());
            return covview;
        }
    }
}