package com.example.bluehoclibrary.format.request;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.format.base.BTBaseMessageFormat;
import com.example.bluehoclibrary.model.BTGroup;
import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/5/17.
 */

public class BTGroupMessage extends BTBaseMessageFormat {
    private final static String METHOD = "group";

    public BTGroupMessage(ArrayList<BTMember> to, BTGroup group) {
        super(METHOD, to);

        JSONObject groupJSONObject = new JSONObject();

        try {
            groupJSONObject.put("group", group.getBTGroupJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.setContent(groupJSONObject);
    }
}
