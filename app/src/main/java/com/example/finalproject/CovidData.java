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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    String country;
    String countryCode;
    String province;
    String city;
    String date;
    long id;
    int cases;
    SharedPreferences cvprefs = null;
    SQLiteDatabase cdb;
    ProgressBar pb;

    /**
     *Where the activity is initialized, loads the view, data will be saved in this method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data);

        pb = findViewById(R.id.Covpgbar);
        pb.setVisibility(View.VISIBLE);

        Button Covidbtn = findViewById(R.id.Covbtn);
        ImageButton CovSearchbtn = findViewById(R.id.Resultbtn);
        Button Gotobtn = findViewById(R.id.gotobtn);
        Button clearbtn = findViewById(R.id.clearbtn);
        ListView Covidlist = findViewById(R.id.Covlist);
        EditText edt = findViewById(R.id.cvedt);
        EditText edtd = findViewById(R.id.dateedt);
        EditText edtt = findViewById(R.id.timeedt);
        EditText edtd2 = findViewById(R.id.dateedt2);
        EditText edtt2 = findViewById(R.id.timeedt2);
        Covidlist.setAdapter(CovAdt);

        //Initializes and loads the database
        initDatabase();
        loadDataFromCovidDatabase();

        cvprefs = getSharedPreferences("Input_Data", Context.MODE_PRIVATE);
        String cvsavedString = cvprefs.getString("COUNTRY", "");
        String d1savedString = cvprefs.getString("FROMDATE", "");
        String t1savedString = cvprefs.getString("FROMTIME", "");
        String d2savedString = cvprefs.getString("TODATE", "");
        String t2savedString = cvprefs.getString("TOTIME", "");
        edt.setText(cvsavedString);
        edtd.setText(d1savedString);
        edtt.setText(t1savedString);
        edtd2.setText(d2savedString);
        edtt2.setText(t2savedString);

        //Set the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("Covid-19 ver.1.0");
        myToolbar.setSubtitle("Marek La Roche");
        setSupportActionBar(myToolbar);

        //For NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        /*
        Welcome
        To started get search results start by entering a country and the dates into the edit texts
        The results will have a default date if only the country is entered
        You can save the results by date by clicking on the save button or saving the item
        The clear button will clear all the information on the page
        To go to you can go see your saved data by clicking on "go to saved data"

        The data here is stored in dates
        Click on a date to view results
        You can delete a date by long clicking on it
        To go back to the previous page press "go back"
         */

        CovSearchbtn.setOnClickListener(click -> {
            //Declare and execute a search query
            CovArray.clear();
            String ec = edt.getText().toString();
            String ed1 = edtd.getText().toString();
            String ed2 = edtd2.getText().toString();
            String et1 = edtt.getText().toString();
            String et2 = edtt2.getText().toString();
            CovidQuery cq = new CovidQuery();
            if (ec.isEmpty()) {
            cq.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");}
            else if (ed1.isEmpty() && et1.isEmpty() || ed2.isEmpty() && et2.isEmpty()){
            cq.execute("https://api.covid19api.com/country/"+ec+"/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");}
            else{ cq.execute("https://api.covid19api.com/country/"+ec+"/status/confirmed/live?from="+ed1+"T"+et1+"Z&to="+ed2+"T"+et2+"Z");}
            //cq.execute("https://api.covid19api.com/country/" +edt.getText()+ "/status/confirmed/live?from=" +edtd.getText()+ "T" +edtt.getText()+ "Z&to=" +edtd2.getText()+ "T" +edtt2.getText()+ "Z");

            CovAdt.notifyDataSetChanged();

            //Saves the query from the edit text into the file
            SharedPreferences.Editor editor = cvprefs.edit();
            String stringToSave = edt.getText().toString();
            String stringToSave1 = edtd.getText().toString();
            String stringToSave2 = edtt.getText().toString();
            String stringToSave3 = edtd2.getText().toString();
            String stringToSave4 = edtt2.getText().toString();
            editor.putString("COUNTRY", stringToSave);
            editor.putString("FROMDATE", stringToSave1);
            editor.putString("FROMTIME", stringToSave2);
            editor.putString("TODATE", stringToSave3);
            editor.putString("TOTIME", stringToSave4);
            editor.apply();
        });

       Covidbtn.setOnClickListener(click -> {
           Toast.makeText(this, getString(R.string.csaveToastAll), Toast.LENGTH_SHORT).show();
           for (int i=0; CovArray.size() > i; i++){
           insertDatabase(CovArray.get(i).getCountry(),CovArray.get(i).getCountryCode(),CovArray.get(i).getProvince(),CovArray.get(i).getCity(),CovArray.get(i).getCases(),CovArray.get(i).getDate());
           }
        });


        Covidlist.setOnItemClickListener((parent, view, position, id) -> {
            //Creating alert
            Covid cvpos = CovArray.get(position);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Information")
                    .setMessage(getString(R.string.cinfo1)+" "+cvpos.cases+" "+getString(R.string.cinfo2)+" "+cvpos.country+", "+cvpos.displayCityProvince()+" "+getString(R.string.cinfo3)+" "+cvpos.convertDate())
                    .setNegativeButton((getString(R.string.calertBtnClose)), (click, arg) -> {})
                    .setPositiveButton((getString(R.string.calertBtnSave)), (click, arg) -> {
                        Toast.makeText(this, getString(R.string.csaveToastDate), Toast.LENGTH_LONG).show();
                        for (int i=0; CovArray.size() > i; i++){
                        insertDatabase(CovArray.get(i).getCountry(),CovArray.get(i).getCountryCode(), CovArray.get(i).getProvince(), CovArray.get(i).getCity(), CovArray.get(i).getCases(), CovArray.get(position).getDate());

                    }})
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
            Snackbar snack = Snackbar.make(clearbtn,getString(R.string.ctoast1), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ctoastBtn1), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CovArray.clear();
                            edt.setText("");edtd.setText("");edtd2.setText("");edtt.setText("");edtt2.setText("");
                        }
                    });
            snack.show();
        });
    }

    /**
     * Creates the toolbar menu
     * @param menu Object used to create the toolbar
     * @return true
     */
   @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
       MenuItem helpButton = menu.findItem(R.id.menu1);
       helpButton.setVisible(true);
       return true;
    }

    /**
     * Listener for items in the nav menu
     * @param item items in the drawer
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        return super.onNavigationItemSelected(item);
    }

    /**
     * Listener for items in the toolbar
     * @param item items on the toolbar menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.menu1) {
            AlertDialog.Builder alertCovidHelp = new AlertDialog.Builder(this);

            alertCovidHelp.setTitle(getString(R.string.chelpButtonTitle))
                    .setMessage(getString(R.string.chelpButton1))
                    .setNegativeButton((getString(R.string.calertBtnClose)), (click, arg) -> {})
                    .create().show();
        }
        return true;
    }

    public class CovidQuery extends AsyncTask<String, Integer, String>{

        /**
         *Will create the connection and run the url to fetch data and add to the array
         * @param strings What will be ran
         * @return null
         */
        public String doInBackground(String... strings) {
            publishProgress(0);
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
                    publishProgress(10);
                countryCode = obj.getString("CountryCode");
                province = obj.getString("Province");
                    publishProgress(30);
                city = obj.getString("City");
                cases = obj.getInt("Cases");
                    publishProgress(60);
                date = obj.getString("Date");
                    CovArray.add(new Covid(country, countryCode, province, city, cases, date, id));
                }
                publishProgress(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Called during the doInBackground method
         * @param value the amount of progress on the bar
         */
    public void onProgressUpdate(Integer ... value){
        pb.setVisibility(View.VISIBLE);
        pb.setProgress(value[0]);
    }

        /**
         * Called after the doInBackground method
         * @param fromDoInBackground what will be returned from doInBackground
         */
    public void onPostExecute(String fromDoInBackground){
            CovAdt.notifyDataSetChanged();
            pb.setVisibility(View.INVISIBLE);
    }
    }

    /**
     * This method will load all the data from the database, called in onCreate
     */
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

    /**
     * This will initialize the database before items can be loaded
     */
    public void initDatabase(){
        CovidOpener cpHelper = new CovidOpener(this);
        cdb = cpHelper.getWritableDatabase();
    }

    /**
     * Saves all the paramaters into the database
     * @param country country row to be saved
     * @param countryCode country code row to be saved
     * @param province province row to be saved
     * @param city city row to be saved
     * @param cases cases row to be saved
     * @param date date row to be saved
     * @return true
     */
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

        /**
         * Gets the size of the array
         * @return The number of elements
         */
        @Override
        public int getCount() {
            return CovArray.size();
        }
        //Returns elements in the list based on the array size

        /**
         * Gets the position of the item in the array
         * @param position position of the item
         * @return the item at position in the arrays
         */
        @Override
        public Covid getItem(int position) {
            return CovArray.get(position);
        }//Item returned in the list

        /**
         *
         * @param position position of the item
         * @return the item position in the database
         */
        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        /**
         *
         * @param position The item that will be returned
         * @param convertView The view that will be displayed
         * @param parent ViewGroup
         * @return view
         */
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