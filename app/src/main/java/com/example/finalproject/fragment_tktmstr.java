package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class fragment_tktmstr extends Fragment {
private Bundle dataFromActivity;
private AppCompatActivity parentActivity;
private TextView nameTV, typeTV,urlTV,priceTV,dateTV;
private ImageView img;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        dataFromActivity = getArguments();

        View results = inflater.inflate(R.layout.fragment_tktmstr,container,false);
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
        priceTV.setText(eventPriceMIN + "$" + " - " + eventPriceMAX + "$");



        return results;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone

    }
}