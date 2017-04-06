package com.example.bluehoclibrary.manager;

import android.util.Log;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.rsa.PublicKey;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/1/17.
 */

public class BTMemberManager {
    private final static String TAG = "BTMemberManager";
    BTServerConnectionManager connectionManager;
    private PublicKey publicKey;

    public BTMemberManager(BTServerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public BTMemberManager(BTServerConnectionManager connectionManager, PublicKey publicKey) {
        this.connectionManager = connectionManager;
        this.publicKey = publicKey;
    }

    public ArrayList<BTMember> getMembers() {
        ArrayList<BTMember> members = new ArrayList<>();
        ArrayList<BTConnection> connections = connectionManager.getBTConnections();

        members.add(new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress, publicKey));

        Log.v(TAG, Integer.toString(connections.size()));

        for(int i = 0; i < connections.size(); i++) {
            members.add(connections.get(i).getMember());
        }

        return members;
    }

    public JSONArray getMembersJSONArray() {
        JSONArray membersArray = new JSONArray();
        ArrayList<BTConnection> connections = connectionManager.getBTConnections();

        if(publicKey == null) {
            membersArray.put(new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress).getMemberJSON());
        } else {
            membersArray.put(new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress, publicKey).getMemberJSON());
        }

        for(int i = 0; i < connections.size(); i++) {
            BTMember member = connections.get(i).getMember();
            membersArray.put(member.getMemberJSON());
        }

        return membersArray;
    }
}
