package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CovidFragment extends ListFragment {
    
    AppCompatActivity parentActivity;
    Bundle dataFromCovidActivity;
    //ArrayList<Covid> Dates = new ArrayList<>();
    FragmentAdapter fadapt = new FragmentAdapter();
    SQLiteDatabase cdb;
    String date;
    ArrayList<Covid> test = new ArrayList<>();

    /**
     *Where the fragment is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("s", getActivity().toString());
        View res = inflater.inflate(R.layout.activity_covid_fragment,container,false);

        dataFromCovidActivity = getArguments();
        date = dataFromCovidActivity.getString("passdate");
        queryDate(date);

        ListView dateDataList = res.findViewById(android.R.id.list);

        dateDataList.setAdapter(fadapt);
        fadapt.notifyDataSetChanged();
        return res;
    }

    /**
     * Will return and adaptor for the frafment
     * @return fadapt
     */
    public FragmentAdapter getAdaptor(){
        return fadapt;
    }

    /**
     * Will query for data based on what date you selected
     * @param date date
     */
    public void queryDate(String date) {
        CovidOpener cpHelper = new CovidOpener(getActivity());
        cdb = cpHelper.getWritableDatabase();

        String[] columns = {CovidOpener.COL_COUNTRY, CovidOpener.COL_COUNTRYCODE, CovidOpener.COL_PROVINCE, CovidOpener.COL_CITY, CovidOpener.COL_CASES, CovidOpener.COL_DATE, CovidOpener.COL_ID};

        Cursor results = cdb.rawQuery("select * from COVID_DATA where DATE like ?", new String[]{date});

        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);
        int countryCodeColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRYCODE);
        int provinceColIndex = results.getColumnIndex(CovidOpener.COL_PROVINCE);
        int cityColIndex = results.getColumnIndex(CovidOpener.COL_CITY);
        int casesColIndex = results.getColumnIndex(CovidOpener.COL_CASES);
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int idColIndex = results.getColumnIndex(CovidOpener.COL_ID);

        Log.i("printdate", date);
        if (!test.isEmpty()){
            test.clear();
        }
        while (results.moveToNext()) {
            String country = results.getString(countryColIndex);
            String countryCode = results.getString(countryCodeColIndex);
            String province = results.getString(provinceColIndex);
            String city = results.getString(cityColIndex);
            int cases = results.getInt(casesColIndex);
            date = results.getString(dateColIndex);
            long id = results.getLong(idColIndex);
            Log.i("printarray", country + province + city + cases + date);
            test.add(new Covid(country, countryCode, province, city, cases, date, id));

        }
        fadapt.notifyDataSetChanged();
        printCursor(results);
    }

    /**
     * Will print out database results, used for log
     * @param c cursor
     */
    public void printCursor(Cursor c){

        c.moveToFirst();
        while (!c.isAfterLast()){
            String country = c.getString(c.getColumnIndex(CovidOpener.COL_COUNTRY));
            String countryCode = c.getString(c.getColumnIndex(CovidOpener.COL_COUNTRYCODE));
            String province = c.getString(c.getColumnIndex(CovidOpener.COL_PROVINCE));
            String city = c.getString(c.getColumnIndex(CovidOpener.COL_CITY));
            int cases = c.getInt(c.getColumnIndex(CovidOpener.COL_CASES));
            String date = c.getString(c.getColumnIndex(CovidOpener.COL_DATE));
            long id = c.getLong(c.getColumnIndex(CovidOpener.COL_ID));
            Log.i("print","Results" + country + province + city + cases + date);
            c.moveToNext();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    public class FragmentAdapter extends BaseAdapter{

        /**
         * Gets the size of the array
         * @return The number of elements
         */
        @Override
        public int getCount() {
            return test.size();
        }
        //Returns elements in the list based on the array size


        /**
         * Gets the position of the item in the array
         * @param position position of the item
         * @return the item at position in the arrays
         */
        @Override
        public Covid getItem(int position) {
            return test.get(position);
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