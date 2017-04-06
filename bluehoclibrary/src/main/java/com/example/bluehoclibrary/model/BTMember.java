package com.example.bluehoclibrary.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.rsa.PublicKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 2/27/17.
 */

public class BTMember implements Parcelable{
    private String name;
    private String address;
    private PublicKey publicKey;

    public BTMember(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public BTMember(String name, String address, PublicKey publicKey) {
        this.name = name;
        this.address = address;
        this.publicKey = publicKey;
    }

    public BTMember(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.address = json.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public BTMember(BluetoothDevice device) {
        this.name = device.getName();
        this.address = device.getAddress();
    }

    public BTMember(DBMember member) {
        this.name = member.getName();
        this.address = member.getAddress();
    }

    public BTMember(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);

        this.name = data[0];
        this.address = data[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {name, address});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BTMember createFromParcel(Parcel in) {
            return new BTMember(in);
        }

        public BTMember[] newArray(int size) {
            return new BTMember[size];
        }
    };

    public JSONObject getMemberJSON() {
        JSONObject memberObject = new JSONObject();

        try {
            memberObject.put("name", name);
            memberObject.put("address", address);
            JSONObject publicKeyObject = new JSONObject();
            if(publicKey != null) {
                publicKeyObject.put("E", publicKey.getE().toString());
                publicKeyObject.put("N", publicKey.getN().toString());
                memberObject.put("public_key", publicKeyObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return memberObject;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
