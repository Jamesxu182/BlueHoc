package com.example.bluehoclibrary.manager.base;

import com.example.bluehoclibrary.configure.BTConfigure;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public abstract class BTManager implements BTConfigure {
    public abstract void sendTo(String address, String content);

    public abstract void multicast(ArrayList<String> addresses, String content);

    public abstract void broadcast(String content);
}
