package com.example.bluehoclibrary.database.model;

import com.example.bluehoclibrary.model.BTMember;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 2/25/17.
 */

public class DBMember {
    private long member_id;
    private String address;
    private String name;
    private BTMember member;

    public DBMember() {
        ;
    }

    public DBMember(String name, String address) {
        this.address = address;
        this.name = name;
        this.member = new BTMember(name, address);
    }

    public DBMember(BTMember member) {
        this.member = member;
        this.address = member.getAddress();
        this.name = member.getName();
    }

    public DBMember(JSONObject jsonObject) {
        try {
            this.address = jsonObject.getString("address");
            this.name = jsonObject.getString("name");
            this.member = new BTMember(name, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DBMember(long member_id, String address, String name) {
        this.member_id = member_id;
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMemberId() {
        return member_id;
    }

    public void setMemberId(long member_id) {
        this.member_id = member_id;
    }

    public BTMember getMember() {
        return member;
    }
}
