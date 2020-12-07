package com.example.finalproject;
/**
 * TicketMaster activity class allows the user to search the ticketmaster database and
 * populates it to a listVIew
 * @author Mike Hansen
 * @version 1.0
 * Date: 11/19/2020
 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
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

public class TicketMasterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageButton search_IB;// Image button click to search
    private EditText cityName_ET;// Edit text used to insert city name
    private EditText radius_ET;// EditText used to insert radius
    private Button toTicketSaved;
    private ProgressBar search_PB;// Progress bar updated by search progress
    private ArrayList<Event> eventArray = new ArrayList<>();//array of Events
    //private ArrayList<String> tempArray = new ArrayList<>();// a temp list for demo 1
    private ListView chatLView;// A list view in this page
    private Event eventObj;// used to add events to the list and database;
    MyListAdapter myAdapter = new MyListAdapter();// the adapter to update the lisEd
    Toolbar tBar;//Toolbar item
    Toolbar lBar;//Toolbar item for nav bar
    SharedPreferences prefs = null;// A shared preferenc item
    Bitmap image =null;

    // Database variables

    private SQLiteDatabase db;
    private DatabaseHelper MyDatabaseHelper; //creates database helper object
    protected Cursor results; // creates a cursor;

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

        //populating the shared preferences in the radius and city edit texts
        prefs = getSharedPreferences("tktMstrShardPref",Context.MODE_PRIVATE);
        String savedCity = prefs.getString("city", "");
        String savedRadius = prefs.getString("radius", "");
        cityName_ET.setText(savedCity);
        radius_ET.setText(savedRadius);


        ////// Fragment //////
        boolean isTablet = findViewById(R.id.fragmentLocation_tkt) !=null; //isTablet is a boolean var that checkes to see if fragment lcation is load which is an id in the 720dp activity_ticket_master


        //// button to go to saved events ///////////////////////////////
        toTicketSaved = findViewById(R.id.toTktSaved);
        Intent savedPage = new Intent(this,activity_tktmstr_saved.class);

        toTicketSaved.setOnClickListener(v -> {startActivity(savedPage);});

        //////////////////////////////////// Loading Database components /////////////////////////////

        MyDatabaseHelper = new DatabaseHelper(this);
        db = MyDatabaseHelper.getWritableDatabase();
        String [] columns = {MyDatabaseHelper.COL_ID, MyDatabaseHelper.COL_NAME,MyDatabaseHelper.COL_TYPE,MyDatabaseHelper.COL_URL, MyDatabaseHelper.COL_MIN,MyDatabaseHelper.COL_MAX };
        results = db.query(false,MyDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        int idColIndex = results.getColumnIndex(MyDatabaseHelper.COL_ID);
        int nameColIndex = results.getColumnIndex(MyDatabaseHelper.COL_NAME);
        int typeColIndex= results.getColumnIndex(MyDatabaseHelper.COL_TYPE);
        int  urlColIndex = results.getColumnIndex(MyDatabaseHelper.COL_URL);
        int maxColIndex = results.getColumnIndex(MyDatabaseHelper.COL_MAX);
        int minColIndex= results.getColumnIndex(MyDatabaseHelper.COL_MIN);
        int dateCol= results.getColumnIndex(MyDatabaseHelper.COL_DATE);


//        while (results.moveToNext()){
//            String dbName = results.getString(nameColIndex);
//            String dbType = results.getString(typeColIndex);
//            String dbURL = results.getString(urlColIndex);
//            int bdMax = results.getInt(maxColIndex);
//            int bdMin = results.getInt(minColIndex);
//
//            eventArray.add(new Event(dbName,dbType,dbURL,bdMin,bdMax,0,"","SAVED"));
//
//        }


        //////////////////////////////////////// TOAST Just because it is required not sure where I want it for real ///////////////////////////////////
        Context context = getApplicationContext();
        CharSequence text = "Welcome to the ticket master search app";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text,duration);
        toast.show();



        /////////////////////////////////////// End of toast, Times up /////////////////////////////////////////////////////////////////////////////////


        /////////////////////////////////////// Adding toolbar and nav bar to page//////////////////////////////////////////////////
        tBar = findViewById(R.id.toolbar);// set toolbar to the id of my toolbar in the ticket master xml
        setSupportActionBar(tBar);

        //  creating drawer layout
        DrawerLayout drawer = findViewById(R.id.drawr_layout);// to be created in the xml for this file
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,tBar, R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);// to be created in the xml of this file
        navigationView.setNavigationItemSelectedListener(this);



        ////////////////////////////////////End of Toolbar ///////////////////////////////////////////////////////////////////////////////////////////




        /**
         * On click listener set to the search button, contacts Ticket master website , and calls the list adapter
         * no return
         */
        search_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getCity = null;
                String getRadius = null;
                String url = null;
                String date=null;

                getCity =  cityName_ET.getText().toString();
                getRadius = radius_ET.getText().toString();


                Log.d("CITY_NAME:  ",  getCity);
                Log.d("Radius:  ",  getRadius);


                if(getCity.matches("") || getRadius.matches("")){

                    /////////////////////////////////// building snackbar, cause its snack time ////////////////////////////////////////////////////////////////////
//
                    Snackbar snack =  Snackbar.make(findViewById(R.id.rootView_tktMstr), "p",  Snackbar.LENGTH_LONG);
                    snack.show();

                    ////////////////////////////////// End of Snackbar   /////////////////////////////////////////////////////////////////////////////////////////////

                }
                //apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB
                // https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=toronto&radius=100
                else {
                    saveSharedPrefs(getCity,getRadius);
                    url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=" + getCity + "&radius=" + getRadius;
                    search_PB.setVisibility(View.VISIBLE);
                    SearchTktMstr req = new SearchTktMstr();

                    req.execute(url);

                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        // create a save, delete and cancel button
        ///Allows user to save item to the database
        chatLView.setOnItemLongClickListener((parent, view, position, id) -> {
            Bitmap temp =null;
            LayoutInflater li= getLayoutInflater();
            View thisRow =  li.inflate(R.layout.alert_image_layout, null, false);
            ImageView image =thisRow.findViewById(R.id.theimage);
            try{

                URL tempImgURL =new URL(eventArray.get(position).getImgURL());
                temp = getIcon(tempImgURL);
                ImageLoader il = new ImageLoader(image, tempImgURL);
                il.execute();
            }

            catch (Exception e){
                Log.d("DUCK", "DUCK");
            }
//
            //          image.setImageBitmap(temp);

            ///////////// setting up bundle to pass to the new fragment //////
            Bundle dataToPass = new Bundle(); //bundel object to hold event data from the on click
            dataToPass.putString("NAME",eventArray.get(position).getName());
            dataToPass.putString("TYPE",eventArray.get(position).getType());
            dataToPass.putDouble("MIN",eventArray.get(position).getPriceMin());
            dataToPass.putDouble("NAME",eventArray.get(position).getName());
            dataToPass.putString("NAME",eventArray.get(position).getName());

            if(isTablet){
                fragment_tktmstr dFragment = new fragment_tktmstr();//creates an object of my frag class
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation_tkt,dFragment).commit();

            }
            else {
                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.tktMstr_Alert))
                        .setMessage("Would you like to save this event: " + eventArray.get(position).getName() + " ?" + "\n \n" +
                                "Event Type: " + eventArray.get(position).getType() + "\n \n" +
                                "Event URL: " + eventArray.get(position).getURL() + "\n \n " +
                                "Date: " + eventArray.get(position).getDate() + "\n \n" +
                                "The price range is: " + eventArray.get(position).getPriceMin() + " to " + eventArray.get(position).getPriceMax() + " $")

                        .setPositiveButton("YES", (click, arg) -> {
                            eventArray.get(position).setSaved("Saved");
                            // save item to the db
                            ContentValues newRowValues = new ContentValues();
                            newRowValues.put(MyDatabaseHelper.COL_NAME, eventArray.get(position).getName());
                            newRowValues.put(MyDatabaseHelper.COL_TYPE, eventArray.get(position).getType());
                            newRowValues.put(MyDatabaseHelper.COL_URL, eventArray.get(position).getURL());
                            newRowValues.put(MyDatabaseHelper.COL_MAX, eventArray.get(position).getPriceMax());
                            newRowValues.put(MyDatabaseHelper.COL_MIN, eventArray.get(position).getPriceMin());
                            //newRowValues.put(MyDatabaseHelper.COL_DATE,eventArray.get(position).getDate());
                            long newId = db.insert(MyDatabaseHelper.TABLE_NAME, null, newRowValues);
                            //eventArray.get(position).setID(newId);


//                        tempArray.remove(position);
                            myAdapter.notifyDataSetChanged();

                        })


                        .setNeutralButton("Delete", (click, arg) -> {

                            eventArray.remove(position);

                            myAdapter.notifyDataSetChanged();

                        })
                        .setNegativeButton("Cancel", (click, arg) -> {
                        })
                        .setView(thisRow)
                        .create().show();

            }
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
            String imgURL;//the url to the promotional image of the event
            int min, max;// the min and max price for tickets
            int searchUpdate;// used to populate the progress bar
            String date;

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
                JSONArray dateArray;
                JSONObject dateObj;
                JSONObject dateObj1;
                JSONArray img;

                for(int i =0; i<events.length();i++){
                    JSONObject obj = events.getJSONObject(i);
                    if(obj.has("images")){
                        img = obj.getJSONArray("images");
                        imgURL = img.getJSONObject(0).getString("url");
                    }
                    else{
                        imgURL = "";
                    }
                    if(obj.has("priceRanges")) {
                        priceRanges = obj.getJSONArray("priceRanges");
                        min = priceRanges.getJSONObject(0).getInt("min");
                        max = priceRanges.getJSONObject(0).getInt("max");
                    }
                    else{
                        min = -1;
                        max = -1;
                    }
                    if(obj.has("dates")){
                        dateObj = obj.getJSONObject("dates");
                        dateObj1 = dateObj.getJSONObject("start");
                        date = dateObj1.getString("localDate");

                    }
                    else{date="";}


                    name = obj.getString("name");
                    type = obj.getString("type");
                    url1 = obj.getString("url");

                    Log.d("Date date date date:",date);
//                       Log.d(" TYPE",type);
//                       Log.d(" URL",url1);
//                       Log.d("MIN", String.valueOf(min));
//                       Log.d("MAX", String.valueOf(max));
                    eventArray.add(new Event(name,type,url1,min,max,0,imgURL,date) );// adds each event to the list
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
//            Log.d("OnProgressUpdate", String.valueOf(args[0]));
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
    /**
     * creates a help file on the by creating an alert menu
     * @param Item: is a the page menu item
     * @return returns true.
     */
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

    @Override
    /*Creates the behaviour for the Navigation bar
     *@param item, a menu item, the behavior of each item set bellow
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    /**
     * Allows the search EditText boxes to populate data from the last search
     * @param city: the name of the city that was last searched
     * @param radius: the radius used in the last search
     */
    private void saveSharedPrefs(String city, String radius){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("city",city);
        edit.putString("radius",radius);
        edit.commit();
    }


    private Bitmap getIcon(URL imgURL){
        Log.i("IN GET IMAGE","START");
        HttpURLConnection imgConn = null;
        try{
            imgConn = (HttpURLConnection) imgURL.openConnection();
            imgConn.connect();
            int imgResponseCode = imgConn.getResponseCode();
            if(imgResponseCode == 200){
                image = BitmapFactory.decodeStream(imgConn.getInputStream());
                return image;
            }
            else{
                return null;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        finally{
            imgConn.disconnect();

        }



    }
    public class ImageLoader extends AsyncTask<Void, Void, Bitmap>
    {
        ImageView i;
        URL s;
        public ImageLoader(ImageView anIV, URL source)
        {
            i = anIV;
            s = source;
        }
        public Bitmap  doInBackground(Void ... v){
            try {
                return getIcon(s);
            }catch(Exception e){

            }
            return null;
        }

        public void onPostExecute(Bitmap b)
        {
            i.setImageBitmap(b);

        }
    }


}