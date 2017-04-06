package com.example.bluehoclibrary.manager;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTMember;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class BTBroadcastManager implements BTConfigure {
    private Service service;

    public BTBroadcastManager(Service service) {
        this.service = service;
    }

    public void broadcastConnectSuccessfully() {
        Intent intent = new Intent(CONNECT_ACTION);

        Bundle bundle = new Bundle();
        bundle.putString("state", CONNECT_SUCCESSFULLY_ACTON_STATE);

        intent.putExtras(bundle);

        service.sendBroadcast(intent);
    }

    public void broadcastGetNewConnection(BluetoothDevice device) {
        Intent intent = new Intent(CONNECT_ACTION);

        Bundle bundle = new Bundle();
        bundle.putString("state", CONNECT_NEW_CONNECTION_ACTION_STATE);
        bundle.putParcelable("device", device);
        intent.putExtras(bundle);

        service.sendBroadcast(intent);
    }

    public void broadcastConnectBreak(BluetoothDevice device) {
        Intent intent = new Intent(CONNECT_ACTION);

        Bundle bundle = new Bundle();
        bundle.putString("state", CONNECT_BREAK_ACTION_STATE);
        bundle.putParcelable("device", device);
        intent.putExtras(bundle);

        service.sendBroadcast(intent);
    }

    public void broadcastNewMessage(String content) {
        Intent intent = new Intent(NEW_MESSAGE_ACTION);

        Bundle bundle = new Bundle();
        bundle.putString("state", NEW_MESSAGE_COME_ACTION);
        bundle.putString("raw", content);
        intent.putExtras(bundle);

        service.sendBroadcast(intent);
    }

    public void broadcastGroupMembers(ArrayList<BTMember> members) {
        Intent intent = new Intent(BROADCAST_MEMBERS_ACTION);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", members);
        intent.putExtras(bundle);

        service.sendBroadcast(intent);
    }
}
