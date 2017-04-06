package com.example.bluehoclibrary.service;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.bluehoclibrary.format.request.BTGroupTextMessage;
import com.example.bluehoclibrary.format.request.BTTextMessage;
import com.example.bluehoclibrary.manager.BTClientManager;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.service.base.BTBaseService;

import java.util.ArrayList;

/**
 * Created by james on 12/28/16.
 */

public class BTClientService extends BTBaseService {
    private final String TAG = "BTClientService";

    private BTClientManager clientManager;

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate() executes.");
        super.onCreate();

        clientManager = new BTClientManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand() executes.");

        String action = intent.getAction();
        if(action != null) {
            switch (action) {
                case "connect":
                    handleConnectRequest(intent);
                    Log.v(TAG, action);
                    break;
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
                    handleMulticastTexttRequest(intent);
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
        clientManager.stopThreads();
    }

    public void handleConnectRequest(Intent intent) {
        Bundle bundle = intent.getExtras();

        BluetoothDevice device = bundle.getParcelable("device");

        clientManager.connect(device);
    }

    private void handleCheckMembersRequest(Intent intent) {
        clientManager.broadcastGroupMembers();
    }

    private void handleSendTextRequest(Intent intent) {
        Log.v(TAG, "handleSendRequest() executes.");

        Bundle bundle = intent.getExtras();

        String address = bundle.getString("address");
        String content = bundle.getString("content");

        clientManager.sendTo(address, content);
    }

    private void handleSendTextEncryptRequest(Intent intent) {
        Log.v(TAG, "handleSendTextEncryptRequest() executes.");

        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            BTMember member = bundle.getParcelable("member");
            String raw_content = bundle.getString("content");

            clientManager.sendEncryptedTextTo(member, raw_content);
        }
    }

    private void handleMulticastTexttRequest(Intent intent) {
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            ArrayList<BTMember> members = bundle.getParcelableArrayList("members");
            String raw_content = bundle.getString("content");

            clientManager.multicast(new ArrayList<String>(), new BTTextMessage(members, raw_content).getMessageJSON().toString());
        }
    }

    private void handleGroupTextRequest(Intent intent) {
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            String raw_content = bundle.getString("content");

            Log.v(TAG, raw_content);

            clientManager.broadcast(new BTGroupTextMessage(raw_content).getMessageJSON().toString());
        }
    }
}
