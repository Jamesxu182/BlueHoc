package com.example.bluehoclibrary.format.base;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by james on 3/1/17.
 */

public class BTBaseMessageFormat {
    private JSONObject messageJSON;
    private String method;
    private BTMember from;
    private ArrayList<BTMember> to;
    private Timestamp timestamp;
    private JSONObject content;

    public BTBaseMessageFormat() {
        this.from = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public BTBaseMessageFormat(String method, ArrayList<BTMember> to) {
        this.to = to;
        this.method = method;

        this.from = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public BTBaseMessageFormat(String method) {
        this.method = method;

        this.from = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public BTBaseMessageFormat(String method, ArrayList<BTMember> to, BTMember from) {
        this.to = to;
        this.method = method;

        this.from = from;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public BTBaseMessageFormat(String method, ArrayList<BTMember> to, JSONObject content) {
        this.from = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.to = to;
        this.content = content;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public BTBaseMessageFormat(String method, ArrayList<BTMember> to, JSONObject content, BTMember from) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public ArrayList<BTMember> getTo() {
        return to;
    }

    public void setTo(ArrayList<BTMember> to) {
        this.to = to;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject getMessageJSON() {
        messageJSON = new JSONObject();

        try {
            messageJSON.put("method", this.method);
            messageJSON.put("from", this.from.getMemberJSON());
            if(to != null) {
                JSONArray tempJSONArray = new JSONArray();
                for(BTMember member : to) {
                    tempJSONArray.put(member.getMemberJSON());
                }
                messageJSON.put("to", tempJSONArray);
            } else {
                messageJSON.put("to", "all");
            }
            messageJSON.put("timestamp", this.timestamp);
            messageJSON.put("content", this.content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageJSON;
    }
}
