package com.example.bluehoclibrary.format.request;

import com.example.bluehoclibrary.format.base.BTBaseMessageFormat;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.model.BTMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/4/17.
 */

public class BTTextMessage extends BTBaseMessageFormat {
    private static final String METHOD = "text";

    private ArrayList<BTMember> to;
    private String content;

    private BTMessage message;

    public BTTextMessage(ArrayList<BTMember> to, String content) {
        super(METHOD, to);
        this.to = to;
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
