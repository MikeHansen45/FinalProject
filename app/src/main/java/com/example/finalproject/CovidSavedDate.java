package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;

public class CovidSavedDate extends MainActivity  {

    DateListAdapter DateAdt = new DateListAdapter();
    //ArrayList<String> Datess = new ArrayList();
    ArrayList<Covid> Dates = new ArrayList();
    Covid cov;
    CovidFragment cf;
    SQLiteDatabase cdb;
    String country;
    String countryCode;
    String province;
    String city;
    int cases;
    String date;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_saved_date);

        ListView datelist = findViewById(R.id.datelv);
        Button backbutton = findViewById(R.id.gobackbtn);

        loadDataFromCovidDatabase();
        //loadDatesFromDatabase();
        datelist.setAdapter(DateAdt);
        //loadDataFromCovidDatabase();
        //queryDate();

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

        backbutton.setOnClickListener(click -> {

            Intent goback = new Intent(CovidSavedDate.this, CovidData.class);
            startActivity(goback);
        });

        datelist.setOnItemClickListener((parent, view, position, id) -> {
            //queryDate(Datess.get(position));
            Bundle dateDataToPass = new Bundle();
            dateDataToPass.putString("passdate", Dates.get(position).getDate());
        cf = new CovidFragment();
        cf.setArguments(dateDataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cfragment, cf)
                .commit();

            cf.getAdaptor().notifyDataSetChanged();
    });

        datelist.setOnItemLongClickListener((parent, view, position, id) -> {
            Snackbar snack = Snackbar.make(datelist,"The data on screen will be cleared", Snackbar.LENGTH_LONG)
                    .setAction("ACCEPT", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*deleteDate(cov);
                    Dates.remove(position);
                    DateAdt.notifyDataSetChanged();*/
                }
            });
            snack.show();
            return true;
        });
    }

    /*public void deleteDate(Covid m){
        cdb.delete(CovidOpener.TABLE_NAME,CovidOpener.COL_DATE + "= ?", new String[] {String.toString(m.getDate())});
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem helpButton = menu.findItem(R.id.menu1);
        helpButton.setVisible(true);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void loadDataFromCovidDatabase() {
        CovidOpener cpHelper = new CovidOpener(this);
        cdb = cpHelper.getWritableDatabase();

        String[] columns = {CovidOpener.COL_COUNTRY, CovidOpener.COL_COUNTRYCODE, CovidOpener.COL_PROVINCE, CovidOpener.COL_CITY, CovidOpener.COL_CASES, CovidOpener.COL_DATE, CovidOpener.COL_ID};

        Cursor results = cdb.query(true, CovidOpener.TABLE_NAME, columns, null, null, CovidOpener.COL_DATE, null, null, null);

        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);
        int countryCodeColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRYCODE);
        int provinceColIndex = results.getColumnIndex(CovidOpener.COL_PROVINCE);
        int cityColIndex = results.getColumnIndex(CovidOpener.COL_CITY);
        int casesColIndex = results.getColumnIndex(CovidOpener.COL_CASES);
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(CovidOpener.COL_ID);

        while (results.moveToNext()) {
            country = results.getString(countryColIndex);
            countryCode = results.getString(countryCodeColIndex);
            province = results.getString(provinceColIndex);
            city = results.getString(cityColIndex);
            cases = results.getInt(casesColIndex);
            date = results.getString(dateColIndex);
            id = results.getLong(idColIndex);
            //Log.i("DATESSS", date);

            /*for (int i = 0; Dates.size() > i; i++) {
                Log.i("Dates?", Dates.get(i).getDate());
                if (!Dates.get(i).getDate().contains(date)) {*/
                    Dates.add(new Covid(null, null, null, null, 0, date, 0));
                }
            //}
        //}
        printCursor(results);
    }

    /*public void loadDatesFromDatabase() {
        CovidOpener cpHelper = new CovidOpener(this);
        cdb = cpHelper.getWritableDatabase();

        String[] datecolumn = {CovidOpener.COL_DATE};

        Cursor dateResults = cdb.query(false, CovidOpener.TABLE_NAME, datecolumn, null, null, null, null, null, null);

        int dateColIndex = dateResults.getColumnIndex(CovidOpener.COL_DATE);

        while (dateResults.moveToNext()) {
            date = dateResults.getString(dateColIndex);
            if (Datess.contains(date)) {
                Datess.add(date);
            }
        }
    }*/

    /**
     * This will query the database and filter the dates
     *
     * @param date Filtered dates to display them in a listview
     */
    //Will filter the results by date still container
    public void queryDate(String date) {
        ArrayList<Covid> Dates = new ArrayList<>();
        CovidOpener cpHelper = new CovidOpener(this);
        cdb = cpHelper.getWritableDatabase();

        String[] columns = {CovidOpener.COL_COUNTRY, CovidOpener.COL_COUNTRYCODE, CovidOpener.COL_PROVINCE, CovidOpener.COL_CITY, CovidOpener.COL_CASES, CovidOpener.COL_DATE, CovidOpener.COL_ID};
        //Cursor results = cdb.query(true, CovidOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        Cursor results = cdb.rawQuery("select * from TABLE_NAME where COL_DATE = date", new String[]{CovidOpener.TABLE_NAME, date});

        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);
        int countryCodeColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRYCODE);
        int provinceColIndex = results.getColumnIndex(CovidOpener.COL_PROVINCE);
        int cityColIndex = results.getColumnIndex(CovidOpener.COL_CITY);
        int casesColIndex = results.getColumnIndex(CovidOpener.COL_CASES);
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(CovidOpener.COL_ID);

        while (results.moveToNext()) {
            country = results.getString(countryColIndex);
            countryCode = results.getString(countryCodeColIndex);
            province = results.getString(provinceColIndex);
            city = results.getString(cityColIndex);
            cases = results.getInt(casesColIndex);
            date = results.getString(dateColIndex);
            id = results.getLong(idColIndex);
            Dates.add(new Covid(country, countryCode, province, city, cases, date, id));
        }
    }

    public void printCursor(Cursor c) {
        c.moveToFirst();
        while (!c.isAfterLast()) {
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

    class DateListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Dates.size();
        }
        //Returns elements in the list based on the array size

        @Override
        public Covid getItem(int position) {
            return Dates.get(position);
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
            getCov.setText(getItem(position).getDate());
            return covview;
        }
    }
}
