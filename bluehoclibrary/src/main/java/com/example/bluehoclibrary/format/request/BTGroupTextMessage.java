package com.example.bluehoclibrary.format.request;

import com.example.bluehoclibrary.format.base.BTBaseMessageFormat;
import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.model.BTMessage;
import com.example.bluehoclibrary.thread.ConnectedThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/16/17.
 */

public class BTGroupTextMessage extends BTBaseMessageFormat {
    private static final String METHOD = "group_text";

    private ArrayList<BTMember> to;
    private String content;

    private BTMessage message;

    public BTGroupTextMessage(String content) {
        super(METHOD);
        this.content = content;

        this.message = new BTMessage(to, content);

        JSONObject contentJSONObject = new JSONObject();

        try {
            contentJSONObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.setContent(contentJSONObject);
    }

    @Override
    public JSONObject getMessageJSON() {
        return super.getMessageJSON();
    }

    public BTMessage getBTMessage() {
        return message;
    }
}
