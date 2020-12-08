package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CovidFragment extends Fragment {
    
    AppCompatActivity parentActivity;
    Bundle dataFromCovidActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View res = inflater.inflate(R.layout.activity_covid_fragment,container,false);
        TextView fragmenttv = res.findViewById(R.id.ftv);
        fragmenttv.setText("Hello");
        return res;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }
}