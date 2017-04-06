package com.example.bluehoclibrary.model;

import android.bluetooth.BluetoothDevice;

import com.example.bluehoclibrary.thread.ConnectedThread;

/**
 * Created by james on 2/27/17.
 */

public class BTConnection {
    private String name;
    private String address;
    private BluetoothDevice device;
    private ConnectedThread connectedThread;
    private BTMember member;

    public BTConnection(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;

        this.device = connectedThread.getRemoteDevice();
        this.member = new BTMember(device);
        this.name = this.device.getName();
        this.address = this.device.getAddress();
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

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public BluetoothDevice getRemoteDevice() {
        return connectedThread.getRemoteDevice();
    }

    public BTMember getMember() {
        return member;
    }
}
