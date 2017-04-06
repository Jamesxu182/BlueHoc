package com.example.bluehoclibrary.controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.service.BTClientService;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class BTClientController extends BTBaseController implements BTConfigure {
    private final static String TAG = "BTClientController";

    public BTClientController(Activity activity) {
        super(activity);
    }

    public void startClientService() {
        type = CLIENT_TYPE;
        Intent intent = new Intent(activity, BTClientService.class);

        activity.startService(intent);
    }

    public void connectToDevice(BluetoothDevice device) {
        Intent intent = new Intent(activity, BTClientService.class);

        intent.setAction("connect");
        Bundle bundle = new Bundle();
        //bundle.putString("action", "connect");
        bundle.putParcelable("device", device);

        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void sendTo(BTMember member, String content) {
        Intent intent = new Intent(activity, BTClientService.class);
        intent.setAction("text");
        Bundle bundle = new Bundle();
        bundle.putString("address", member.getAddress());
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    public void multicast(ArrayList<BTMember> members, String content) {
        Intent intent = new Intent(activity, BTClientService.class);
        intent.setAction("text_multicast");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", members);
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    public void broadcast(ArrayList<BTMember> members, String content) {
        Intent intent = new Intent(activity, BTClientService.class);
        intent.setAction("text_broadcast");
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void sendEncryptedMessageTo(BTMember member, String content) {
        Intent intent = new Intent(activity, BTClientService.class);
        intent.setAction("text_encrypt");
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        bundle.putParcelable("member", member);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void sendGroupTextMessageTo(String content) {
        Intent intent = new Intent(activity, BTClientService.class);

        intent.setAction("group_text");

        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        intent.putExtras(bundle);

        activity.startService(intent);
    }

    public void checkMembersInGroup() {
        Intent intent = new Intent(activity, BTClientService.class);

        intent.setAction("member");

        activity.startService(intent);
    }

    public void stopClientService() {
        Intent intent = new Intent(activity, BTClientService.class);

        activity.stopService(intent);
        type = null;
    }


    @Override
    public void startService() {
        startClientService();
    }

    @Override
    public void stopService() {
        stopClientService();
    }
}
