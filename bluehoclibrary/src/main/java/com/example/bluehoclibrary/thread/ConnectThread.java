package com.example.bluehoclibrary.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import com.example.bluehoclibrary.configure.BTConfigure;
import java.io.IOException;

/**
 * Created by james on 12/20/16.
 */

public class ConnectThread extends Thread implements BTConfigure {
    private static final String TAG = "ConnectThread";

    private final BluetoothSocket socket;
    private final BluetoothDevice device;

    private final BluetoothAdapter bluetoothAdapter;

    private Handler handler;

    public ConnectThread(BluetoothDevice device, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.handler = handler;

        BluetoothSocket tmp = null;
        this.device = device;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket = tmp;
    }

    public void run() {
        Log.v(TAG, "run() executes.");

        // Cancel discovery because it will slow down the connection
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            connectException.printStackTrace();
            try {
                socket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        // manageConnectedSocket(mmSocket);
        Log.v(TAG, "Connect Successfully.");
        Log.v(TAG, "Name: " + socket.getRemoteDevice().getName() + " Address: " + socket.getRemoteDevice().getAddress());
        handler.obtainMessage(1, socket).sendToTarget();

        Log.v(TAG, "run() over.");
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) { }
    }
}
