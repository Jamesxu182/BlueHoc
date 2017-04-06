package com.example.bluehoclibrary.service;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.format.request.BTGroupTextMessage;
import com.example.bluehoclibrary.format.request.BTTextMessage;
import com.example.bluehoclibrary.manager.BTServerManager;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.service.base.BTBaseService;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by james on 12/28/16.
 */

public class BTServerService extends BTBaseService {
    private final static String TAG = "BluetoothServerService";

    private BTServerManager serverManager;

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate() executes.");
        super.onCreate();

        serverManager = new BTServerManager(this);
        serverManager.accept();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand() executes.");

        String action = intent.getAction();
        if(action != null) {
            switch(action) {
                case "member":
                    handleCheckMembersRequest(intent);
                    break;
                case "text":
                    handleSendTextRequest(intent);
                    break;
                case "text_encrypt":
                    handleSendTextEncryptRequest(intent);
                    break;
                case "text_multicast":
                    handleMulticastTextRequest(intent);
                    break;
                case "group_text":
                    handleGroupTextRequest(intent);
                    break;
                default:
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy() executes.");
        super.onDestroy();
        serverManager.stopThreads();
    }

    public void handleCheckMembersRequest(Intent intent) {
        serverManager.broadcastGroupMembers();
    }

    public void handleSendTextRequest(Intent intent) {
        Log.v(TAG, "handleSendRequest() executes.");

        Bundle bundle = intent.getExtras();

        String address = bundle.getString("address");
        String content = bundle.getString("content");

        Log.v(TAG, content);

        serverManager.sendTo(address, content);
    }

    public void handleSendTextEncryptRequest(Intent intent) {
        Log.v(TAG, "handleSendTextEncryptRequest() executes.");

        Bundle bundle = intent.getExtras();

        BTMember member = bundle.getParcelable("member");
        String raw_content = bundle.getString("content");

        serverManager.sendEncryptedTextTo(member, raw_content);
    }

    private void handleMulticastTextRequest(Intent intent) {
        Log.v(TAG, "handleMulticastTextRequest() executes.");

        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            ArrayList<BTMember> members = bundle.getParcelableArrayList("members");
            String raw_content = bundle.getString("content");

            ArrayList<String> addresses = new ArrayList<>();

            for(BTMember member : members) {
                addresses.add(member.getAddress());
            }

            serverManager.multicast(addresses, new BTTextMessage(members, raw_content).getMessageJSON().toString());
        }
    }

    private void handleGroupTextRequest(Intent intent) {
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            String raw_content = bundle.getString("content");

            serverManager.broadcast(new BTGroupTextMessage(raw_content).getMessageJSON().toString());
        }
    }
}
