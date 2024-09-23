package com.example.myapplication;

import android.os.RemoteException;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;


import java.util.Collection;
import java.util.Iterator;

//MAKE SURE YOU HAVE BLUETOOTH AND GEOLOCATION TURNED ON
public class BeaconScanner {
    //singleton
    private static BeaconScanner instance = null;

    //Class
    private BeaconManager mBeaconManager;
    private Region mRegion = new Region("region", null, null, null);


    //if you want to change beacon protocol, modify this method. For more information search "setBeaconLayout" on stackoverflow
    private BeaconScanner() {
        mBeaconManager = BeaconManager.getInstanceForApplication(MainActivity.getAppContext());
        mBeaconManager.getBeaconParsers().clear();
        // Detect the main identifier (UID) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // Detect the telemetry (TLM) frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v"));
        //Detect iBeacon frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //detect AltBeacon frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }

    public static BeaconScanner getInstance() {//singleton pattern, whenever you call it, it always returns the same object
        if (instance == null)
            instance = new BeaconScanner();
        return instance;
    }

    public void scan(boolean bind, TextView a) {
        a.setText("Scanning...");
        if (bind) {
            mBeaconManager.addRangeNotifier(new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                    if (beacons.size() > 0) {
                        Iterator<Beacon> beaconIterator = beacons.iterator();
                        while (beaconIterator.hasNext()) {
                            Beacon beacon = beaconIterator.next();
                            a.setText("Address: "+beacon.getBluetoothAddress()+"\nDistance: "+beacon.getDistance());
                        }
                    }
                }
            });
            mBeaconManager.startRangingBeacons(mRegion);
        } else {
            mBeaconManager.stopRangingBeacons(mRegion);
            mBeaconManager.removeAllRangeNotifiers();
            a.setText("Stop");
        }
    }

}

