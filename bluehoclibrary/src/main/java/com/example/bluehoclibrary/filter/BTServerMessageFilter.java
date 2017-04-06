package com.example.bluehoclibrary.filter;

import android.util.Log;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.manager.BTServerManager;
import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 3/2/17.
 */

public abstract class BTServerMessageFilter {
    private final static String TAG = "BTServerMessageFilter";
    private String raw;
    private JSONObject object;
    private BTServerManager serverManager;

    private boolean isBlock = true;

    public BTServerMessageFilter(BTServerManager serverManager, String raw) {
        this.raw = raw;
        try {
            object = new JSONObject(raw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.serverManager = serverManager;
    }

    public void classifyMessage() {
        this.isBlock = false;
        try {
            switch(object.getString("method")) {
                case "text":
                    onTextMessageCome(object);
                    break;
                case "join":
                    onJoinMessageCome(object);
                    break;
                case "encrypted_text":
                    onEncryptedTextCome(object);
                    break;
                case "group_text":
                    onGroupTextMessageCome(object);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onTextMessageCome(JSONObject object) {
        try {
            JSONArray membersJSONArray = object.getJSONArray("to");
            isBlock = true;

            for(int i = 0; i < membersJSONArray.length(); i++) {
                BTMember member = new BTMember(membersJSONArray.getJSONObject(i));
                Log.v(TAG, BTBaseController.myBluetoothAddress + " VS " + member.getAddress());
                if(BTBaseController.myBluetoothAddress.equals(member.getAddress())) {
                    serverManager.notify(object.getJSONObject("content").getString("content"));
                    onTextMessageComeToServer(object);
                    isBlock = false;
                } else {
                    Log.v(TAG, member.getName());
                    serverManager.sendTo(member.getAddress(), object.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onJoinMessageCome(JSONObject object) {
        // Do nothing
    }


    public void onEncryptedTextCome(JSONObject object) {
        try {
            JSONArray membersJSONArray = object.getJSONArray("to");

            for(int i = 0; i < membersJSONArray.length(); i++) {
                BTMember member = new BTMember(membersJSONArray.getJSONObject(i));
                Log.v(TAG, BTBaseController.myBluetoothAddress + " VS " + member.getAddress());
                if(BTBaseController.myBluetoothAddress.equals(member.getAddress())) {
                    onEncryptedTextComeToServer(object.getJSONObject("content").getString("content"));
                } else {
                    Log.v(TAG, member.getName());
                    serverManager.sendTo(member.getAddress(), object.toString());
                    block();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onTextMessageComeToServer(JSONObject object)  {
        //
    }

    public void onEncryptedTextComeToServer(String encrypted_content) {
        //
    }

    public void onGroupTextMessageCome(JSONObject groupTextMessageObject) {

    }

    public void block() {
        isBlock = true;
    }

    public boolean getIsBlocked() {
        return isBlock;
    }
}
