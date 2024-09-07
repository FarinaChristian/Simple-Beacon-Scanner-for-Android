package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button button = view.findViewById(R.id.button);
        TextView sas= view.findViewById(R.id.sas);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(sas.getText().equals("Hi"))//this button modifies the Textview
                    sas.setText("Hello");
                else
                    sas.setText("Hi");
            }
        });

        return view;
    }
}