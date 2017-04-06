package com.example.bluehoclibrary.format.request;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.format.base.BTBaseMessageFormat;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.rsa.PublicKey;
import com.example.bluehoclibrary.rsa.RSA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 3/5/17.
 */

public class BTJoinMessage extends BTBaseMessageFormat {
    private final static String METHOD = "join";

    private PublicKey publicKey;

    public BTJoinMessage(ArrayList<BTMember> to, PublicKey publicKey) {
        super(METHOD, to);

        JSONObject member = new JSONObject();

        this.publicKey = publicKey;

        try {
            member.put("member", new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress, this.publicKey).getMemberJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.setContent(member);
    }


}
