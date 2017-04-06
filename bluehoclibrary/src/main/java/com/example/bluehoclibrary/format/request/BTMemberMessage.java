package com.example.bluehoclibrary.format.request;

import android.util.Log;

import com.example.bluehoclibrary.format.base.BTBaseMessageFormat;
import com.example.bluehoclibrary.manager.BTMemberManager;
import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/2/17.
 */

public class BTMemberMessage extends BTBaseMessageFormat {
    private static final String METHOD = "member";

    public BTMemberMessage(ArrayList<BTMember> to, BTMemberManager memberManager) {
        super(METHOD, to);

        JSONObject members = new JSONObject();

        try {
            members.put("members", memberManager.getMembersJSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.setContent(members);
    }

    public BTMemberMessage(ArrayList<BTMember> to, BTMemberManager memberManager, BTMember from) {
        super(METHOD, to, from);

        JSONObject members = new JSONObject();

        try {
            members.put("members", memberManager.getMembersJSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.setContent(members);
    }

    @Override
    public JSONObject getMessageJSON() {
        return super.getMessageJSON();
    }
}
