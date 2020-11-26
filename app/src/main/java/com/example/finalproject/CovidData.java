package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CovidData extends AppCompatActivity {

    CovListAdapter CovAdt = new CovListAdapter();
    ArrayList<Covid> CovArray = new ArrayList();
    Covid c;
    String country;
    String province;
    String city;
    int cases;
    SharedPreferences cvprefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_data);

        Button Covidbtn = findViewById(R.id.Covbtn);
        Button CovSearchbtn = findViewById(R.id.Resultbtn);
        ListView Covidlist = findViewById(R.id.Covlist);
        EditText edt = findViewById(R.id.cvedt);
        Covidlist.setAdapter(CovAdt);

        cvprefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String cvsavedString = cvprefs.getString("ReserveName", "def");

        CovSearchbtn.setOnClickListener(click -> {
            //Snackbar.make(Covidbtn, "Hello", Snackbar.LENGTH_LONG).show();//Creates snackbar with long lenght
            //Toast.makeText(this, "Hello - Toast", Toast.LENGTH_SHORT).show();//Creates toast with short duration
            CovidQuery cq = new CovidQuery();
            cq.execute("https://api.covid19api.com/country/" + edt.getText() + "/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00Z");
            CovAdt.notifyDataSetChanged();

            SharedPreferences.Editor editor = cvprefs.edit();
            String stringToSave = edt.getText().toString();
            editor.putString("ReserveName", stringToSave);
            editor.commit();
        });

        Covidbtn.setOnClickListener(click -> {
            Snackbar.make(Covidbtn, "Hello", Snackbar.LENGTH_LONG).show();//Creates snackbar with long lenght
            Toast.makeText(this, "Hello - Toast", Toast.LENGTH_SHORT).show();//Creates toast with short duration
        });


        Covidlist.setOnItemClickListener((parent, view, position, id) -> {
            //Creating alert
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Covid data")
                    .setMessage("This is covid data")
                    .setNegativeButton("Cancel", (click, arg) -> {})//Cancels the alert
                    .create().show();
        });

        /*CovidQuery cq = new CovidQuery();
        cq.execute("https://api.covid19api.com/country/CANADA/status/confirmed/live?from=2020-10-14T00:00:00Z&to=2020-10-15T00:00:00ZV");
        CovAdt.notifyDataSetChanged();*/
        Covidlist.setAdapter(CovAdt);
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
                /*JSONArray jarray = covobj.getJSONArray("");
                int i = 0;

                while (i<jarray.length()) {
                    JSONObject jo = jarray.getJSONObject(i);
                    c.country = jo.getString("Country");
                    c.province = jo.getString("Province");
                    c.city = jo.getString("City");
                    c.cases = jo.getInt("Cases");
                    i++;*/
                //https://www.semicolonworld.com/question/47685/get-jsonarray-without-array-name
                for(int i=0;i<covobj.length();i++){
                    JSONObject obj = covobj.getJSONObject(i);
                country = obj.getString("Country");
                province = obj.getString("Province");
                city = obj.getString("City");
                cases = obj.getInt("Cases");
                    CovArray.add(new Covid(country, province, city, cases));
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