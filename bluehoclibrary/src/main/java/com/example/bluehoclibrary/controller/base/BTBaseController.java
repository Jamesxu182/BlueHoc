package com.example.bluehoclibrary.controller.base;

import android.app.Activity;
import android.content.Context;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.model.BTMember;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public abstract class BTBaseController implements BTConfigure {
    public Activity activity;

    public static String type;

    public static String myBluetoothName;
    public static String myBluetoothAddress;

    public BTBaseController(Activity activity) {
        this.activity = activity;
        initDatabase(activity.getBaseContext());
    }

    public abstract void sendTo(BTMember member, String content);

    public abstract void multicast(ArrayList<BTMember> members, String content);

    public static void setType(String deviceType) {
        type = deviceType;
    }

    public static void setMyBluetoothName(String name) {
        myBluetoothName = name;
    }

    public static void setMyBluetoothAddress(String address) {
        myBluetoothAddress = address;
    }

    public abstract void startService();

    public abstract void stopService();

    public abstract void checkMembersInGroup();

    public abstract void sendEncryptedMessageTo(BTMember member, String content);

    public abstract void sendGroupTextMessageTo(String content);

    public void initDatabase(Context context) {
        new DatabaseHelper(context);
    }
}
