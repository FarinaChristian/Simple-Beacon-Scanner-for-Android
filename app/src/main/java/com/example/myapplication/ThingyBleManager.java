package com.example.myapplication;

import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

public class ThingyBleManager{
    TextView a=null;
    boolean scanning=false;

    private static ThingyBleManager instance;

    private final Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bluetoothGatt;
    private BleListener listener;
    // l'handler serve per mandare messaggi ai thread, in questo caso mando messaggi al thread principale (getMainLooper)
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Handler rssiHandler = new Handler(Looper.getMainLooper());
    private static final long RSSI_UPDATE_INTERVAL = 500; // mezzo secondo

    /* ===== CALLBACK INTERFACE ===== */
    public interface BleListener {
        void onStatusChanged(String status);
    }

    private ThingyBleManager(Context ctx) {
        context = ctx.getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    //singleton pattern
    public static synchronized ThingyBleManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThingyBleManager(context);
        }
        return instance;
    }

    public void setListener(BleListener listener) {
        this.listener = listener;
    }

    /* ================= SCAN ================= */

    public void startScan() {
        if (scanning){return;}
        scanning=true;
        if (listener != null) {
            listener.onStatusChanged("Scanning...");
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {return;}
        bleScanner.startScan(scanCallback);
        a.setText("Scanning...");
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {return;}
            if (device.getName() != null && device.getName().contains("Thingy")) {
                bleScanner.stopScan(this);
                scanning=false;
                if (listener != null) {listener.onStatusChanged("Connecting...");}
                connect(device);
                a.setText(""+device.getName());
            }
        }
    };

    /* ================= CONNECT ================= */

    private void connect(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {return;}
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    /* ================= GATT ================= */

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                startRssiUpdates(gatt);
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                a.setText("BEEP");
                if (listener != null) {
                    listener.onStatusChanged("DISCONNECTED");
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(rssi>-80){
                    mainHandler.post(() -> {a.setText("RSSI: " + rssi);});// dico al thread principale di eseguire il runnable (codice nelle parentesi)
                                                                          // lo esegue quando può, il post lo mette in coda
                }
                else{
                    mainHandler.post(() -> {a.setText("BEEP");});
                }
            }
        }

        private void startRssiUpdates(BluetoothGatt gatt) {
            rssiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (gatt != null) {
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {return;}
                        gatt.readRemoteRssi();
                        rssiHandler.postDelayed(this, RSSI_UPDATE_INTERVAL); // lo eseguo ogni tot tempo
                    }
                }
            });
        }
    };

    /* ================= CLEANUP ================= */
    public void close() {
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {return;}
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    //I set the textView to show the beacons
    public void setTextView(TextView t){a=t;}

    public boolean getScanOnOff(){return scanning;}

    public void stopScan(){
        if(!scanning){return;}
        scanning=false;
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {return;}
        bleScanner.stopScan(scanCallback);
        a.setText("Stop");
    }
}
