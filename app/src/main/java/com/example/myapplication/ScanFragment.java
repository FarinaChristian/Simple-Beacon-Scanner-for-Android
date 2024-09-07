package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class ScanFragment extends Fragment {

    private BeaconScanner beaconScanner;

    public ScanFragment() {
        // Required empty public constructor
    }

    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        beaconScanner=BeaconScanner.getInstance();
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        TextView a =view.findViewById(R.id.info);//this TextView shows the data of every scanned beacon
        Switch switchscan=view.findViewById(R.id.switch1);
        switchscan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchscan.setText("    Scan on");
                    beaconScanner.scan(true,a);
                }
                else {
                    switchscan.setText("    Scan off");
                    beaconScanner.scan(false,a);
                }
            }
        });
        return view;
    }
}