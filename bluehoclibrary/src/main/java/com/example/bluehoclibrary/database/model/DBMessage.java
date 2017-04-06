package com.example.bluehoclibrary.database.model;

import com.example.bluehoclibrary.controller.base.BTBaseController;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by james on 2/25/17.
 */

public class DBMessage {
    private long message_id;
    private DBMember from;
    private DBMember to;
    private String content;
    private Timestamp timestamp;

    public DBMessage() {
        ;
    }

    public DBMessage(JSONObject jsonObject) {
        try {
            this.from = new DBMember(jsonObject.getJSONObject("from"));
            this.to = new DBMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress);
            this.content = jsonObject.getJSONObject("content").getString("content");
            this.timestamp = Timestamp.valueOf(jsonObject.getString("timestamp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DBMessage(DBMember from, DBMember to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public DBMessage(long message_id, DBMember from, DBMember to, String content) {
        this.message_id = message_id;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public long getMessageId() {
        return message_id;
    }

    public void setMessageId(long message_id) {
        this.message_id = message_id;
    }

    public DBMember getFrom() {
        return from;
    }

    public void setFrom(DBMember from) {
        this.from = from;
    }

    public DBMember getTo() {
        return to;
    }

    public void setTo(DBMember to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
