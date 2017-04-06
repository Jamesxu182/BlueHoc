package com.example.bluehoclibrary.filter;

import android.util.Log;

import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.database.model.DBMessage;
import com.example.bluehoclibrary.manager.BTClientManager;
import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/2/17.
 */

public abstract class BTClientMessageFilter {
    private String raw;
    private JSONObject object;
    public BTClientManager clientManager;

    private boolean isBlock = false;

    public BTClientMessageFilter(BTClientManager clientManager, String raw) {
        this.raw = raw;
        try {
            object = new JSONObject(raw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.clientManager = clientManager;
    }

    public void classifyMessage() {
        isBlock = false;
        try {
            switch(object.getString("method")) {
                case "group":
                    onGroupMessageCome(object.getJSONObject("content").getJSONObject("group"));
                    break;
                case "member":
                    onGetNewMember(object.getJSONObject("content").getJSONArray("members"));
                    break;
                case "text":
                    onTextCome(object.getJSONObject("content").getString("content"));
                    break;
                case "encrypted_text":
                    onEncryptedTextCome(object.getJSONObject("content").getString("content"));
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

    public ArrayList<BTMember> onGetNewMember(JSONArray membersJSONArray) {
        ArrayList <BTMember> members = new ArrayList<>();

        for(int i = 0; i < membersJSONArray.length(); i++) {
            try {
                JSONObject memberJSONObject = membersJSONArray.getJSONObject(i);
                members.add(new BTMember(memberJSONObject.getString("name"), memberJSONObject.getString("address")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return members;
    }

    public void onTextCome(String content) {
        clientManager.notify(content);
    }

    public void onEncryptedTextCome(String encrypted_content) {

    }

    public void onGroupMessageCome(JSONObject groupObject) {

    }

    public void onGroupTextMessageCome(JSONObject groupTextMessageObject) {

    }

    public void block() {
        isBlock = true;
    }

    public boolean getIsBlock() {
        return isBlock;
    }
}
