package com.example.bluehoclibrary.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.bluehoclibrary.configure.BTConfigure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by james on 12/20/16.
 */

public class ConnectedThread extends Thread implements BTConfigure {
    private final static String TAG = "ConnectedThread";

    private final BluetoothSocket socket;

    private final InputStream inputStream;
    private final OutputStream outputStream;

    private Handler messageHandler;
    private Handler connectionHandler;

    private BluetoothDevice remoteDevice;

    private boolean isStoppedByUser = false;

    public ConnectedThread(BluetoothSocket socket, Handler messageHandler, Handler connectionHandler) {
        this.socket = socket;
        this.remoteDevice = socket.getRemoteDevice();

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        this.messageHandler = messageHandler;
        this.connectionHandler = connectionHandler;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.inputStream = tmpIn;
        this.outputStream = tmpOut;
    }

    @Override
    public void run() {
        Log.v(TAG, "run() executes.");

        byte [] buffer = new byte[1024];
        int bufferRead = 0;

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                bufferRead = inputStream.read(buffer, 0, buffer.length);

                String message = new String(buffer, 0, bufferRead, Charset.forName("UTF-8"));

                messageHandler.obtainMessage(New_Message, message).sendToTarget();

                Log.v(TAG, message);

            } catch (IOException e) {
                e.printStackTrace();

                if(!isStoppedByUser) {
                    connectionHandler.obtainMessage(CONNECT_BREAK, getRemoteDevice().getAddress()).sendToTarget();
                }

                break;
            }
        }

        Log.v(TAG, "run() overs.");
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        Log.v(TAG, "write() executes.");
        try {
//            bufferedOutputStream.write(bytes);
//            bufferedOutputStream.flush();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        isStoppedByUser = true;
        Log.v(TAG, "cancel() executes.");
        try {
            if(socket != null) {
                socket.close();
            }

            if(inputStream != null) {
                inputStream.close();
            }

            if(outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothDevice getRemoteDevice() {
        return remoteDevice;
    }

}
