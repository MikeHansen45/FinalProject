 package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
    ArrayList<Covid> Dates = new ArrayList();
    Covid c;
    String country;
    String countryCode;
    String province;
    String city;
    String date;
    long id;
    int cases;
    SharedPreferences cvprefs = null;
    SQLiteDatabase cdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data);

        Button Covidbtn = findViewById(R.id.Covbtn);
        Button CovSearchbtn = findViewById(R.id.Resultbtn);
        Button Gotobtn = findViewById(R.id.gotobtn);
        Button clearbtn = findViewById(R.id.clearbtn);
        ListView Covidlist = findViewById(R.id.Covlist);
        EditText edt = findViewById(R.id.cvedt);
        EditText edtd = findViewById(R.id.dateedt);
        EditText edtt = findViewById(R.id.timeedt);
        EditText edtd2 = findViewById(R.id.dateedt2);
        EditText edtt2 = findViewById(R.id.timeedt2);
        Covidlist.setAdapter(CovAdt);

        initDatabase();
        loadDataFromCovidDatabase();

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
            CovArray.clear();
            String ec = edt.getText().toString();
            CovidQuery cq = new CovidQuery();
            if (ec.isEmpty()) {
            cq.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");}
            else{
            cq.execute("https://api.covid19api.com/country/" + ec +"/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");}
            //cq.execute("https://api.covid19api.com/country/" +edt.getText()+ "/status/confirmed/live?from=" +edtd.getText()+ "T" +edtt.getText()+ "Z&to=" +edtd2.getText()+ "T" +edtt2.getText()+ "Z");

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

           for (int i=0; CovArray.size() > i; i++){
           insertDatabase(CovArray.get(i).getCountry(),CovArray.get(i).getCountryCode(),CovArray.get(i).getProvince(),CovArray.get(i).getCity(),CovArray.get(i).getCases(),CovArray.get(i).getDate());}
        });


        Covidlist.setOnItemClickListener((parent, view, position, id) -> {
            //Creating alert
            Covid cvpos = CovArray.get(position);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
           /* alert.setTitle(getString(R.string.cvalert_t))
                    .setMessage(getString(R.string.cvalert_mess))
                    .setNegativeButton(getString(R.string.cvalert_negative), (click, arg) -> {})//Cancels the alert*/
            alert.setTitle("Information")
                    .setMessage("There are "+cvpos.cases+" cases in "+cvpos.country+", "+cvpos.displayCityProvince()+" as of "+cvpos.convertDate())
                    .setNegativeButton(("Close"), (click, arg) -> {})
                    .setPositiveButton(("Save"), (click, arg) -> {
                    })
                    .create().show();
        });

        /*CovidQuery cq = new CovidQuery();
        cq.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00ZV");
        CovAdt.notifyDataSetChanged();*/
        Covidlist.setAdapter(CovAdt);


        Gotobtn.setOnClickListener(click -> {

            Intent phonefm = new Intent(CovidData.this, CovidSavedDate.class);
            startActivity(phonefm);
        });

        clearbtn.setOnClickListener(click ->{
            CovArray.clear();
            edt.setText("");edtd.setText("");edtd2.setText("");edtt.setText("");edtt2.setText("");
        });
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
       MenuItem helpButton = menu.findItem(R.id.menu1);
       helpButton.setVisible(true);
       return true;
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

        public String doInBackground(String... strings) {
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
                date = obj.getString("Date");
                    CovArray.add(new Covid(country, countryCode, province, city, cases, date, id));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    public void onProgressUpdate(Integer ... value){

    }

    public void onPostExecute(String fromDoInBackground){
        CovAdt.notifyDataSetChanged();
    }
    }

    public void loadDataFromCovidDatabase(){

        String [] columns = {CovidOpener.COL_COUNTRY, CovidOpener.COL_COUNTRYCODE, CovidOpener.COL_PROVINCE, CovidOpener.COL_CITY, CovidOpener.COL_CASES, CovidOpener.COL_DATE, CovidOpener.COL_ID};

        Cursor results = cdb.query(false, CovidOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);
        int countryCodeColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRYCODE);
        int provinceColIndex = results.getColumnIndex(CovidOpener.COL_PROVINCE);
        int cityColIndex = results.getColumnIndex(CovidOpener.COL_CITY);
        int casesColIndex = results.getColumnIndex(CovidOpener.COL_CASES);
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(CovidOpener.COL_ID);

        while(results.moveToNext()){
            country = results.getString(countryColIndex);
            countryCode = results.getString(countryCodeColIndex);
            province = results.getString(provinceColIndex);
            city = results.getString(cityColIndex);
            cases = results.getInt(casesColIndex);
            date = results.getString(dateColIndex);
            id = results.getLong(idColIndex);
            CovArray.add(new Covid(country, countryCode, province, city, cases, date, id));
        }
        printCursor(results);
    }

    public void printCursor(Cursor c){
        c.moveToFirst();
        while (!c.isAfterLast()){
            country = c.getString(c.getColumnIndex(CovidOpener.COL_COUNTRY));
            countryCode = c.getString(c.getColumnIndex(CovidOpener.COL_COUNTRYCODE));
            province = c.getString(c.getColumnIndex(CovidOpener.COL_PROVINCE));
            city = c.getString(c.getColumnIndex(CovidOpener.COL_CITY));
            cases = c.getInt(c.getColumnIndex(CovidOpener.COL_CASES));
            date = c.getString(c.getColumnIndex(CovidOpener.COL_DATE));
            id = c.getLong(c.getColumnIndex(CovidOpener.COL_ID));
            c.moveToNext();
        }
    }

    //Initializes the database
    public void initDatabase(){
        CovidOpener cpHelper = new CovidOpener(this);
        cdb = cpHelper.getWritableDatabase();
    }

    //Saves list items into the database
    public long insertDatabase(String country, String countryCode, String province, String city, int cases, String date){
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(CovidOpener.COL_COUNTRY, country);
        newRowValues.put(CovidOpener.COL_COUNTRYCODE, countryCode);
        newRowValues.put(CovidOpener.COL_PROVINCE, province);
        newRowValues.put(CovidOpener.COL_CITY, city);
        newRowValues.put(CovidOpener.COL_CASES, cases);
        newRowValues.put(CovidOpener.COL_DATE, date);
        return cdb.insert(CovidOpener.TABLE_NAME, null, newRowValues);
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
            return getItem(position).getId();
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