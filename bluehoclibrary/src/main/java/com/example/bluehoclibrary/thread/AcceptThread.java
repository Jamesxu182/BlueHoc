package com.example.bluehoclibrary.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.bluehoclibrary.configure.BTConfigure;
import java.io.IOException;

/**
 * Created by james on 12/20/16.
 */

public class AcceptThread extends Thread implements BTConfigure {
    private static final String TAG = "AcceptThread";

    private final BluetoothServerSocket serverSocket;
    private final BluetoothAdapter bluetoothAdapter;

    private Handler handler;

    public AcceptThread(Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSocket = tmp;

        this.handler = handler;
    }

    public void run() {
        Log.v(TAG, "run() executes.");

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                //manageConnectedSocket(socket);
                handler.obtainMessage(CONNECT_SUCCESSFULLY, socket).sendToTarget();
            }
        }

        Log.v(TAG, "run() overs.");
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        Log.v(TAG, "cancel() executes.");

        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
