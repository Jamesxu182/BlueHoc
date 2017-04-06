package com.example.bluehoclibrary.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bluehoclibrary.configure.BTConfigure;

/**
 * Created by james on 3/1/17.
 */

public abstract class MessageHandler extends Handler implements BTConfigure {
    @Override
    public void handleMessage(Message msg) {
        switch(msg.what) {
            case New_Message:
                onNewMessage(msg.obj);
                break;
            default:
                onDefault(msg.obj);
                break;
        }
    }

    public void onNewMessage(Object object) {

    }

    public void onDefault(Object object) {

    }
}
