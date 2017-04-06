package com.example.bluehoclibrary.model;

import android.bluetooth.BluetoothDevice;

import com.example.bluehoclibrary.controller.base.BTBaseController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/5/17.
 */

public class BTGroup {
    private String groupName;
    private ArrayList<BTMember> members;
    private BTMember server;
    private BluetoothDevice serverDevice;

    public BTGroup(BluetoothDevice serverDevice) {
        this.serverDevice = serverDevice;
        this.server = new BTMember(serverDevice);
    }

    public BTGroup(ArrayList<BTMember> members) {
        this.members = members;
        this.server = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.groupName = server.getName();
    }

    public BTGroup(ArrayList<BTMember> members, String groupName) {
        this.members = members;
        this.server = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.groupName = groupName;
    }

    public ArrayList<BTMember> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<BTMember> members) {
        this.members = members;
    }

    public BTMember getServer() {
        return server;
    }

    public void setServer(BTMember server) {
        this.server = server;
    }

    public JSONObject getBTGroupJSON() {
        JSONObject groupJSONObject = new JSONObject();
        JSONArray membersJSONArray = new JSONArray();

        for(BTMember member : members) {
            membersJSONArray.put(member.getMemberJSON());
        }

        try {
            groupJSONObject.put("group_name", groupName);
            groupJSONObject.put("server", new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress).getMemberJSON().toString());
            groupJSONObject.put("members", membersJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return groupJSONObject;
    }

    public BluetoothDevice getServerDevice() {
        return serverDevice;
    }

    public void setServerDevice(BluetoothDevice serverDevice) {
        this.serverDevice = serverDevice;
    }

    public String getServerName() {
        return this.server.getName();
    }

    public String getServerAddress() {
        return this.server.getAddress();
    }
}
