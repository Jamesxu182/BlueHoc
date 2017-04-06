package com.example.bluehoclibrary.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.service.BTClientService;
import com.example.bluehoclibrary.service.BTServerService;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class BTServerController extends BTBaseController implements BTConfigure {
    public BTServerController(Activity activity) {
        super(activity);
    }

    public void startServerService() {
        type = SERVER_TYPE;
        Intent intent = new Intent(activity, BTServerService.class);

        activity.startService(intent);
    }

    public void checkMembersInGroup() {
        Intent intent = new Intent(activity, BTServerService.class);

        intent.setAction("member");

        activity.startService(intent);
    }

    @Override
    public void sendTo(BTMember member, String content) {
        Intent intent = new Intent(activity, BTServerService.class);
        intent.setAction("text");
        Bundle bundle = new Bundle();
        bundle.putString("address", member.getAddress());
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void multicast(ArrayList<BTMember> members, String content) {
        Intent intent = new Intent(activity, BTServerService.class);
        intent.setAction("text_multicast");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", members);
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void sendEncryptedMessageTo(BTMember member, String content) {
        Intent intent = new Intent(activity, BTServerService.class);
        intent.setAction("text_encrypt");
        Bundle bundle = new Bundle();
        bundle.putParcelable("member", member);
        bundle.putString("content", content);
        intent.putExtras(bundle);

        activity.startService(intent);
    }

    @Override
    public void sendGroupTextMessageTo(String content) {
        Intent intent = new Intent(activity, BTServerService.class);

        intent.setAction("group_text");

        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        intent.putExtras(bundle);

        activity.startService(intent);
    }

    public void stopServerService() {
        Intent intent = new Intent(activity, BTServerService.class);

        activity.stopService(intent);
        type = null;
    }

    @Override
    public void startService() {
        startServerService();
    }

    @Override
    public void stopService() {
        stopServerService();
    }
}
