package com.example.bluehoclibrary.model;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.database.model.DBMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by james on 2/5/17.
 */

public class BTMessage {
    //private ArrayList<String> toAddresses;
    //private String fromAddress;
    private ArrayList<BTMember> toMembers;
    private BTMember fromMember;
    private Timestamp sendTime;
    private String content;

    private JSONObject messageJSON;

    public BTMessage(ArrayList<BTMember> toMembers, String content) {
        this.fromMember = new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
        this.toMembers = toMembers;
        this.sendTime = new Timestamp(new Date().getTime());
        this.content = content;
    }

    public BTMessage(ArrayList<BTMember> toMembers, String content, BTMember fromMember) {
        this.fromMember = fromMember;
        this.toMembers = toMembers;
        this.sendTime = new Timestamp(new Date().getTime());
        this.content = content;
    }

    public BTMessage(DBMessage message) {
        this.fromMember = new BTMember(message.getFrom());
        this.toMembers = new ArrayList<>();
        this.toMembers.add(new BTMember(message.getTo()));
        this.sendTime = new Timestamp(new Date().getTime());
        this.content = message.getContent();
    }

    public BTMessage(JSONObject messageJSON) {
//        this.messageJSON = messageJSON;
//        this.toMembers = new ArrayList<>();
//
//        try {
//            JSONArray messageJSONArray = messageJSON.getJSONArray("to");
//
//            for (int i = 0; i < messageJSONArray.length(); i++) {
//                this.toAddresses.add((String)messageJSONArray.get(i));
//            }
//
//            this.fromAddress = messageJSON.getString("from");
//            this.sendTime = Timestamp.valueOf(messageJSON.getString("send_time"));
//            this.content = messageJSON.getString("content");
//
//        } catch(JSONException e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<BTMember> getToMembers() {
        return toMembers;
    }

    public void setToMembers(ArrayList<BTMember> toMembers) {
        this.toMembers = toMembers;
    }

    public BTMember getFromMember() {
        return fromMember;
    }

    public void setFromMember(BTMember fromMember) {
        this.fromMember = fromMember;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageJSON() {
//        messageJSON = new JSONObject();
//
//        try {
//            messageJSON.put("method", "message");
//            messageJSON.put("from", fromAddress);
//
//            messageJSON.put("to", new JSONArray(toAddresses));
//            messageJSON.put("send_time", sendTime.toString());
//
//            messageJSON.put("content", content);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public JSONObject getMessaeJSON() {
        setMessageJSON();

        return messageJSON;
    }
}
