package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class fragment_tktmstr extends Fragment {
private Bundle dataFromActivity;
private AppCompatActivity parentActivity;
private TextView nameTV, typeTV,urlTV,priceTV,dateTV;
private Button cancelBT, saveBT;
private ImageView img;

//private DatabaseHelper MyDatabaseHelper; //creates database helper object
public DatabaseHelper MyDatabaseHelper;
private SQLiteDatabase db;// sqlLite db object
protected Cursor results1; // creates a cursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        dataFromActivity = getArguments();

        View results = inflater.inflate(R.layout.fragment_tktmstr,container,false);
        Context context = results.getContext();
        String eventName = dataFromActivity.getString("NAME");
        nameTV = results.findViewById(R.id.tktmst_frag_name);
        nameTV.setText(eventName);

        String eventType = dataFromActivity.getString("TYPE");
        typeTV = results.findViewById(R.id.tktmst_frag_type);
        typeTV.setText(eventType);

        String eventDate = dataFromActivity.getString("DATE");
        dateTV = results.findViewById(R.id.tktmst_frag_date);
        dateTV.setText("Date:" + eventDate);

        double eventPriceMIN = dataFromActivity.getDouble("MIN");
        double eventPriceMAX = dataFromActivity.getDouble("MAX");
        priceTV = results.findViewById(R.id.tktmst_frag_price);
        priceTV.setText("Price" + eventPriceMIN + "$" + " - " + eventPriceMAX + "$");

        String eventURL = dataFromActivity.getString("URL");
        urlTV = results.findViewById(R.id.tktmst_frag_URL);
        urlTV.setText(eventURL);

        saveBT= results.findViewById(R.id.tktmstr_frag_save_BT);
        cancelBT=results.findViewById(R.id.tktmstr_frag_cancel_BT);
        saveBT.setVisibility(View.VISIBLE);
        cancelBT.setVisibility(View.VISIBLE);
        ////////////////// DATABASE SET UP /////////////////////////
        MyDatabaseHelper = new DatabaseHelper(context);
        db = MyDatabaseHelper.getWritableDatabase();

        String [] columns = {MyDatabaseHelper.COL_ID, MyDatabaseHelper.COL_DATE, MyDatabaseHelper.COL_NAME,MyDatabaseHelper.COL_TYPE,MyDatabaseHelper.COL_URL, MyDatabaseHelper.COL_MIN,MyDatabaseHelper.COL_MAX};
        results1 = db.query(false,MyDatabaseHelper.TABLE_NAME, columns,  null, null, null, null, null, null);
//        int idColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_ID);
//        int nameColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_NAME);
//        int typeColIndex= results1.getColumnIndex(MyDatabaseHelper.COL_TYPE);
//        int  urlColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_URL);
//        int maxColIndex = results1.getColumnIndex(MyDatabaseHelper.COL_MAX);
//        int minColIndex= results1.getColumnIndex(MyDatabaseHelper.COL_MIN);
//        int dateCol= results1.getColumnIndex(MyDatabaseHelper.COL_DATE);

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTV.setText("");
                dateTV.setText("");
                priceTV.setText("");
                typeTV.setText("");
                urlTV.setText("");
                cancelBT.setVisibility(View.GONE);
                saveBT.setVisibility(View.GONE);
            }
        });

        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues newRowValues = new ContentValues();
                newRowValues.put(MyDatabaseHelper.COL_NAME, eventName);
                newRowValues.put(MyDatabaseHelper.COL_TYPE, eventType);
                newRowValues.put(MyDatabaseHelper.COL_URL, eventURL);
                newRowValues.put(MyDatabaseHelper.COL_MAX, eventPriceMAX);
                newRowValues.put(MyDatabaseHelper.COL_MIN, eventPriceMIN);
                newRowValues.put(MyDatabaseHelper.COL_DATE,eventDate);
                long newId = db.insert(MyDatabaseHelper.TABLE_NAME, null, newRowValues);
                nameTV.setText("");
                dateTV.setText("");
                priceTV.setText("");
                typeTV.setText("");
                urlTV.setText("");
                CharSequence text = "Event saved";// change to french
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text,duration);
                toast.show();
                cancelBT.setVisibility(View.GONE);
                saveBT.setVisibility(View.GONE);


            }
        });



        return results;
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



        //context will either be FragmentExample for a tablet, or EmptyActivity for phone

    }
}