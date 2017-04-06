package com.example.bluehoclibrary.middleware.mom;

import android.os.Looper;

import com.example.bluehoclibrary.handler.ConnectionHandler;
import com.example.bluehoclibrary.handler.MessageHandler;

/**
 * Created by james on 3/3/17.
 */

public abstract class BTLooperThread extends Thread {
    public ConnectionHandler connectionHandler;
    public MessageHandler messageHandler;

    @Override
    public void run() {
        Looper.prepare();

        initHandler();

        Looper.loop();
    }

    public abstract void initHandler();
}
