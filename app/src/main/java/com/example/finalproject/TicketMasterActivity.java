package com.example.finalproject;

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
    private ImageButton search_IB;
    private EditText cityName_ET;
    private EditText radius_ET;
    private ProgressBar search_PB;
    private ArrayList<Event> eventArray = new ArrayList<>();
    private ArrayList<String> tempArray = new ArrayList<>();
    private ListView chatLView;
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
//        Snackbar cheesyPoof = Snackbar.make()

        ////////////////////////////////// End of Snackbar   /////////////////////////////////////////////////////////////////////////////////////////////



        search_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB
                // https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=toronto&radius=100

                search_PB.setVisibility(View.VISIBLE);
                SearchTktMstr req = new SearchTktMstr();
                req.execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey=ZeH2TvddeHJytkYTsWA4F3GQ9gIWaVZB&city=toronto&radius=100");
                tempArray.add("hi");
                myAdapter.notifyDataSetChanged();
            }
        });
        ///Delete from list on long click add a
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
        @Override
        protected String doInBackground(String... args) {
            String JSONresult;
            String line = null;
            String name;
            String type;
            String url1;
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

                Log.d("PLEASE FUCKING WORK", "WOROROROOROR");
                   for(int i =0; i<events.length();i++){
                       JSONObject obj = events.getJSONObject(i);
                       name = obj.getString("name");
                       type = obj.getString("type");
                       url1 = obj.getString("url");
                       info = obj.getString("info");
                       Log.d(" NAME",name);
                       Log.d(" TYPE",type);
                       Log.d(" URL",url1);
                       Log.d("  INFO",info);

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