package com.example.finalproject;
/**
 * TicketMaster activity class allows the user to search the ticketmaster database and
 * populates it to a listVIew
 * @author Mike Hansen
 * @version 1.0
 * Date: 11/19/2020
 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
    private SearchView cityName_ET;// Edit text used to insert city name
    private EditText radius_ET;// EditText used to insert radius
    private ProgressBar search_PB;// Progress bar updated by search progress
    private ArrayList<Event> eventArray = new ArrayList<>();//array of Events
    //private ArrayList<String> tempArray = new ArrayList<>();// a temp list for demo 1
    private ListView chatLView;// A list view in this page
    private Event eventObj;// used to add events to the list and database;
    MyListAdapter myAdapter = new MyListAdapter();// the adapter to update the list
    Toolbar tBar;//Toolbar item

    // Database variables

    private SQLiteDatabase db;
    private DatabaseHelper MyDatabaseHelper;
    protected Cursor results;

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

        //////////////////////////////////// Loading Database components /////////////////////////////

        MyDatabaseHelper = new DatabaseHelper(this);
        db = MyDatabaseHelper.getWritableDatabase();
        String [] columns = {MyDatabaseHelper.COL_ID, MyDatabaseHelper.COL_NAME,MyDatabaseHelper.COL_TYPE,MyDatabaseHelper.COL_URL};
        results = db.query(false,MyDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //////////////////////////////////////// TOAST Just because it is required not sure where I want it for real ///////////////////////////////////
        Context context = getApplicationContext();
        CharSequence text = "Welcome to the ticket master search app";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text,duration);
        toast.show();



        /////////////////////////////////////// End of toast, Times up /////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////// Adding toolbar to page and defining toolbar behaviour//////////////////////////////////////////////////
        tBar = findViewById(R.id.toolbar);// set toolbar to the id of my toolbar in the ticket master xml
        setSupportActionBar(tBar);







        ////////////////////////////////////End of Toolbar ///////////////////////////////////////////////////////////////////////////////////////////


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

                myAdapter.notifyDataSetChanged();
            }
        });

        ///Allows user to save item to the database
        chatLView.setOnItemLongClickListener((parent, view, position, id) -> {
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.tktMstr_Alert))
                    .setMessage("Would you like to save this event: " + eventArray.get(position).getName() + " ?" + "\n \n" +
                            "Event Type: " + eventArray.get(position).getType() + "\n \n"  +
                            "Event URL: " + eventArray.get(position).getURL() + "\n \n" +
                            "The price range is: " + eventArray.get(position).getPriceMin() + " to " + eventArray.get(position).getPriceMax() + " $")

                    .setPositiveButton("YES", (click, arg) -> {
                    // save item to the db
//                        ContentValues newRowValues = new ContentValues();
//                        newRowValues.put(MyDatabaseHelper.COL_NAME,eventArray.get(position).getName());
//                        newRowValues.put(MyDatabaseHelper.COL_TYPE,eventArray.get(position).getType());
//                        newRowValues.put(MyDatabaseHelper.COL_URL,eventArray.get(position).getURL());
//                        newRowValues.put(MyDatabaseHelper.COL_MAX,eventArray.get(position).getPriceMax());
//                        newRowValues.put(MyDatabaseHelper.COL_MIN,eventArray.get(position).getPriceMin());
//                        long newId = db.insert(MyDatabaseHelper.TABLE_NAME,null,newRowValues);
                        eventArray.remove(position);


//                        tempArray.remove(position);
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

        public int getCount() { return eventArray.size();}

        public Object getItem(int position) { return eventArray.get(position); }

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
            int searchUpdate;// used to populate the progress bar
            String info;
            eventArray.clear();
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

                   for(int i =0; i<events.length();i++){
                       JSONObject obj = events.getJSONObject(i);
                       if(obj.has("priceRanges")) {
                           priceRanges = obj.getJSONArray("priceRanges");
                           min = priceRanges.getJSONObject(0).getInt("min");
                           max = priceRanges.getJSONObject(0).getInt("max");
                       }
                       else{
                           min = -1;
                           max = -1;
                       }

                       name = obj.getString("name");
                       type = obj.getString("type");
                       url1 = obj.getString("url");

//
//                       Log.d(" NAME",name);
//                       Log.d(" TYPE",type);
//                       Log.d(" URL",url1);
//                       Log.d("MIN", String.valueOf(min));
//                       Log.d("MAX", String.valueOf(max));
                       eventArray.add(new Event(name,type,url1,min,max) );// adds each event to the list
                       Log.d("I", String.valueOf(i));
                       Log.d("LENGTH", String.valueOf(events.length()));
                       searchUpdate = (i*100/events.length()) ;
                       publishProgress(searchUpdate);

                }





            }
            catch (Exception e){
                Log.d("IN CATCH", e.toString());
            }
            finally {
                publishProgress(100);
                // close connection
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            Log.d("OnProgressUpdate", String.valueOf(args[0]));
            search_PB.setProgress(args[0]);
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            search_PB.setVisibility(View.GONE);
            search_PB.setProgress(0);
        }
    }


    @Override
    /**
     * onCreateOpetion(Menu menu) inflates the ticket_master_menu.xml to be used as a menu item for this view
     * @param menu a menu item
     *
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ticket_master_menu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.help_item:
                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.tktMstr_help_title))
                        .setMessage("Enter a city name, and the radius around the city you would like to search." +
                                "\npress the search icon and a list of events will appear in the list below." +
                                "\n long click on an event to see more details. If the event interests you you can save it " +
                                "by clicking on the yes button. Once the event is saved it will disapear from the list.")

                        .setPositiveButton("YES", (click, arg) -> {
                            // save item to the db

                        })

                        .create().show();
                break;

        }
        return true;

    }
}