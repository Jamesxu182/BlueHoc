package com.example.bluehoclibrary.handler;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.example.bluehoclibrary.configure.BTConfigure;

/**
 * Created by james on 3/1/17.
 */

public abstract class ConnectionHandler extends Handler implements BTConfigure {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch(msg.what) {
            case CONNECT_SUCCESSFULLY:
                onConnectSuccessful(msg.obj);
                break;
            case CONNECT_FAILED:
                onConnectFailed(msg.obj);
                break;
            case CONNECT_BREAK:
                onConnectBreak(msg.obj);
                break;
            default:
                onDefault(msg.obj);
                break;
        }
    }

    public void onConnectSuccessful(Object object) {
        //Do nothing
    }

    public void onConnectFailed(Object object) {
        //Do nothing
    }

    public void onConnectBreak(Object object) {
        //Do nothing
    }

    public void onDefault(Object object) {
        //Do nothing
    }
}
