package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * Activity_tktmstr_saved.java is class allows the user to examine and delete saved event items
 * from a database populated list view
 * @author Mike Hansen
 * @version 1.0
 * Date: 12/08/2020
 */

public class activity_tktmstr_saved extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar tBar;//Toolbar item
    private ArrayList<Event> savedEvents = new ArrayList<>();// a list of events used to populate
    private ListView eventListView;
    private Event event;// the event ovject used to pass and receive object from the list
    MyListAdapter myAdapter = new MyListAdapter();// listAdpter object declared bellow to help the list function
    private DatabaseHelper MyDatabaseHelper;// db helper item that allows interaction with the db
    private SQLiteDatabase db;// sql lite db conection
    protected Cursor results1;// a cursor that allows you to traverse the db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tktmstr_saved);

        eventListView = findViewById(R.id.chatLView1);
        eventListView.setAdapter(myAdapter);

        //// create toolbar nav bar
        tBar = findViewById(R.id.toolbar1);// set toolbar to the id of my toolbar in the ticket master xml
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.tktMstr_by_mh);

        DrawerLayout drawer = findViewById(R.id.drawr_layout1);// to be created in the xml for this file
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,tBar, R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view1);// a nav view object so the nav bar will show
        navigationView.setNavigationItemSelectedListener(this);

        //// End of Nav Bar section



        eventListView.setOnItemLongClickListener((parent, view, position, id) -> {
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            long id1 = savedEvents.get(position).getId();
            alertDialogBuilder.setTitle(getString(R.string.tktMstr_delete_saved))
                    .setMessage("")

                    .setPositiveButton(getString(R.string.tktMstr_Yes), (click, arg) -> {
                        savedEvents.remove(position);
                        deleteMsg(db,id);
                        myAdapter.notifyDataSetChanged();

                    })
                    //What the No button does:
                    .setNegativeButton(getString(R.string.tktMstr_cancel), (click, arg) -> { })



                    .create().show();
            return true;

        });



        MyDatabaseHelper = new DatabaseHelper(this);
        db = MyDatabaseHelper.getWritableDatabase();
        String [] columns = {MyDatabaseHelper.COL_ID, MyDatabaseHelper.COL_DATE,MyDatabaseHelper.COL_NAME,MyDatabaseHelper.COL_TYPE,MyDatabaseHelper.COL_URL, MyDatabaseHelper.COL_MIN,MyDatabaseHelper.COL_MAX};
        results1 = db.query(false,MyDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        int idColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_ID);// all of the below variables define the position of the collumn
        int nameColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_NAME);
        int typeColIndex= results1.getColumnIndex(MyDatabaseHelper.COL_TYPE);
        int  urlColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_URL);
        int maxColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_MAX);
        int minColIndex= results1.getColumnIndex(MyDatabaseHelper.COL_MIN);
        int dateColIndex=results1.getColumnIndex(MyDatabaseHelper.COL_DATE);

        while (results1.moveToNext()){
            long id = results1.getLong(idColIndex);
            String dbName = results1.getString(nameColIndex);
            String dbType = results1.getString(typeColIndex);
            String dbURL = results1.getString(urlColIndex);
            int bdMax = results1.getInt(maxColIndex);
            int bdMin = results1.getInt(minColIndex);
            String date = results1.getString(dateColIndex);


            savedEvents.add(new Event(dbName,dbType,dbURL,bdMin,bdMax,id,"",date));

            Log.d("IS there stuff", dbURL);
            myAdapter.notifyDataSetChanged();
        }


        // Database opening

    }


    /**
     * deleteMsg creates a sql delete string that is passed to the db as a query
     * @param db the database conection
     * @param id the id that the user want to delt
     */
    public void deleteMsg(SQLiteDatabase db, long id){
        String query = "DELETE FROM " + MyDatabaseHelper.TABLE_NAME + " WHERE " + MyDatabaseHelper.COL_ID + " = '" + id + "'";
        db.execSQL(query);


    }





    private class MyListAdapter extends BaseAdapter {
        @Override
        public Object getItem(int i) {
            return savedEvents.get(i);
        }

        @Override
        public int getCount() {
            return savedEvents.size();
        }

        @Override
        public long getItemId(int i) {
            return savedEvents.get(i).getId();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View newView = null;

            LayoutInflater inflater = getLayoutInflater();

            event = (Event) getItem(i);// ensures that the message is the right one

            newView = inflater.inflate(R.layout.tkt_mstr_saved_view, parent, false);

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.save_event_name_TV);
            TextView tViewName = newView.findViewById(R.id.save_event_Type_TV);
            TextView tViewDate = newView.findViewById(R.id.save_event_date_TV);
            TextView tViewURL = newView.findViewById(R.id.save_event_url_TV);
            TextView tViewPrice = newView.findViewById(R.id.save_event_price_TV);
            tViewDate.setText( "Date: " + ((Event) getItem(i)).getDate());
            tViewPrice.setText("Price :" + ((Event) getItem(i)).getPriceMin() + "$ " + " -  " + ((Event) getItem(i)).getPriceMax() + " $") ;
            tViewName.setText("Type: " +((Event) getItem(i)).getType());
            tViewURL.setText(((Event) getItem(i)).getURL());
            tView.setText( event.toString() );


            return newView;
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
    /*Creates the behaviour for the Navigation bar
     *@param item, a menu item, the behavior of each item set bellow
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent goToTicket = new Intent(this,TicketMasterActivity.class);
        Intent goToRecipe = new Intent(this,RecipeActivity.class);
        Intent goToAudio = new Intent(this, AudioDatabaseActivity.class);
        Intent goToCovid = new Intent(this, CovidData.class);
        switch (item.getItemId()){
            case R.id.toAudio_mi:
                startActivity(goToAudio);
                break;
            case R.id.toCovid_mi:
                startActivity(goToCovid);
                break;
            case R.id.toRecipe_mi:
                startActivity(goToRecipe);
                break;
            case R.id.toTicket_mi:
                startActivity(goToTicket);
                break;

        }
        return false;
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
                alertDialogBuilder.setTitle(R.string.ticket_master_help_title)
                        .setMessage(R.string.tktMst_help_saved)

                        .setPositiveButton(R.string.tktMstr_Yes, (click, arg) -> {
                            // save item to the db

                        })

                        .create().show();
                break;

        }
        return true;

    }


}