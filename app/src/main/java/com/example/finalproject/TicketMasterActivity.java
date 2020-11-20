package com.example.finalproject;
/**
 * TicketMaster activity class allows the user to search the ticketmaster database and
 * populates it to a listVIew
 * @author Mike Hansen
 * @version 1.0
 * Date: 11/19/2020
 */


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Iterator;

public class TicketMasterActivity extends AppCompatActivity {
    private ImageButton search_IB;// Image button click to search
    private EditText cityName_ET;// Edit text used to insert city name
    private EditText radius_ET;// EditText used to insert radius
    private ProgressBar search_PB;// Progress bar updated by search progress
    private ArrayList<Event> eventArray = new ArrayList<>();//array of Events
    private ArrayList<String> tempArray = new ArrayList<>();// a temp list for demo 1
    private ListView chatLView;// A list view in this page
    MyListAdapter myAdapter = new MyListAdapter();// the adapter to update the list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);

        //Assigns all the xml item ids to their java variables
        search_IB = findViewById(R.id.search_IB);
        cityName_ET = findViewById(R.id.cityName_ET);
        radius_ET = findViewById(R.id.radius_ET);
        search_PB = findViewById(R.id.searchPB);
        chatLView = findViewById(R.id.chatLView);
        chatLView.setAdapter(myAdapter);

        //////////////////////////////////////// TOAST Just because it is required not sure where I want it for real ///////////////////////////////////
        Context context = getApplicationContext();
        CharSequence text = "Welcome to the ticket master search app";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text,duration);
        toast.show();



        /////////////////////////////////////// End of toast, Times up /////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////// building snackbar, cause its snack time ////////////////////////////////////////////////////////////////////
//
          Snackbar snack =  Snackbar.make(findViewById(R.id.rootView_tktMstr), "You can see my snackbar !",  Snackbar.LENGTH_LONG);

        ////////////////////////////////// End of Snackbar   /////////////////////////////////////////////////////////////////////////////////////////////


        /**
         * On click listener set to the search button, contacts Ticket master website , and calls the list adapter
         * no return
         */
        search_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB
                // https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=toronto&radius=100

                search_PB.setVisibility(View.VISIBLE);
                SearchTktMstr req = new SearchTktMstr();
                snack.show();
                req.execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=toronto&radius=100");
                tempArray.add("hi");
                myAdapter.notifyDataSetChanged();
            }
        });

        ///Allows user to delete a list item and pops up a alert box
        chatLView.setOnItemLongClickListener((parent, view, position, id) -> {
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.tktMstr_Alert))
                    .setMessage("would you like to delete row " + position + " ?")

                    .setPositiveButton("YES", (click, arg) -> {
                        tempArray.remove(position);
                        myAdapter.notifyDataSetChanged();

                    })
                    .setNegativeButton("NO", (click, arg) -> { })
                    .create().show();
            return true;

        });


    chatLView.setAdapter(myAdapter);
    }
/////////////////////////////////////////////////////////////// MyListAdapter Code bellow /////////////////////////////////////////////////////////


// this class will be heavily modified so I have not added complete metadata.
    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return tempArray.size();}

        public Object getItem(int position) { return "This is row " + position; }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.activity_tktmstr_lv, parent, false);

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.event_details_tv);
            tView.setText( getItem(position).toString() );

            //return it to be put in the table
            return newView;
        }
    }




//////////////////////////////////////////////////////////////  ASYNC CODE BELOW /////////////////////////////////////////////////////////////////


    private class SearchTktMstr extends AsyncTask<String,Integer,String>{
        /**]
         * SearchTktMstr uses AsyncTask to query the ticketmaster site to aquire event info
         * @param args URL's to be queried
         * @return null
         */
        @Override
        protected String doInBackground(String... args) {
            String JSONresult; //The results returnd from JSON query
            String line = null;// temp String used to hold Json data while reading site
            String name;// name of the event
            String type;// type of the event
            String url1;// url to the event on ticket master
            int min, max;// the min and max price for tickets
            String info;

            try {
                URL url = new URL(args[0]);
                HttpURLConnection jsonUrlConnection = (HttpURLConnection) url.openConnection();
                InputStream responseJson = jsonUrlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseJson, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                while((line = reader.readLine()) !=null){
                    sb.append(line +"\n");
//                    Log.d("LINE", line);
                }
                JSONresult = sb.toString();
                JSONObject tktMstJSON = new JSONObject(JSONresult);


                JSONObject embeded = tktMstJSON.getJSONObject("_embedded");
                JSONArray events = embeded.getJSONArray("events");
                JSONArray priceRanges ;
                Log.d("PLEASE FUCKING WORK", "WOROROROOROR");
                   for(int i =0; i<events.length();i++){
                       JSONObject obj = events.getJSONObject(i);
                       if(obj.has("priceRanges")) {
                           priceRanges = obj.getJSONArray("priceRanges");
                           min = priceRanges.getJSONObject(0).getInt("min");
                           max = priceRanges.getJSONObject(0).getInt("max");
                       }
                       else{
                           min = -1;
                       }

                       name = obj.getString("name");
                       type = obj.getString("type");
                       url1 = obj.getString("url");
                     //  info = obj.getString("info");


                       Log.d(" NAME",name);
                       Log.d(" TYPE",type);
                       Log.d(" URL",url1);
                       //Log.d("  INFO",info);

                }





            }
            catch (Exception e){
                Log.d("IN CATCH", e.toString());
            }
            finally {
                // close connection
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }





}