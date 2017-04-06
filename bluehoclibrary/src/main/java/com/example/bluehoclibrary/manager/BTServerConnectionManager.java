package com.example.bluehoclibrary.manager;

import android.util.Log;

import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTMember;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class BTServerConnectionManager {
    private static final String TAG = "ConnectionManager";
    private ArrayList<BTConnection> connections;

    public BTServerConnectionManager() {
        connections = new ArrayList<>();
    }

    public ArrayList<BTConnection> getBTConnections() {
        return connections;
    }

    public void addNewConnection(BTConnection newBTConnection) {
        connections.add(newBTConnection);
    }

    public int getNumOfConnections() {
        return connections.size();
    }

    public BTConnection getConnectionByAddress(String address) {

        Log.v(TAG, "looking for " + address);

        for(BTConnection connection : connections) {

            Log.v(TAG, connection.getAddress());

            if(connection.getAddress().equals(address)) {
                return connection;
            }
        }

        return null;
    }

    public ArrayList<BTMember> getConnectedMembers() {
//        ArrayList<BTMember> members = new ArrayList<>();
//
//        for(BTConnection connection : connections) {
//            members.add(new BTMember(connection.getName(), connection.getAddress()));
//        }

        ArrayList<BTMember> members = new ArrayList<>();

        for(int i = 0; i < connections.size(); i++) {
            BTConnection connection = connections.get(i);
            members.add(new BTMember(connection.getName(), connection.getAddress()));
        }

        for(int i = 0; i < connections.size(); i++) {
            BTConnection connection = connections.get(i);
            Log.v(TAG, new BTMember(connection.getName(), connection.getAddress()).getMemberJSON().toString());
        }

        return members;
    }

    public void removeConnectionByAddress(String address) {
//        for(BTConnection connection : connections) {
//            if(connection.getAddress() == address) {
//                connections.remove(connection);
//            }
//        }

        for(int i = 0; i < connections.size(); i++) {
            if(connections.get(i).getAddress().equals(address)) {
                connections.remove(i);
            }
        }
    }

    public void stopConnectedThreads() {
        for(BTConnection connection : connections) {
            connection.getConnectedThread().cancel();
        }

        connections.clear();
    }

    public ArrayList<BTMember> getAllMembers() {
        ArrayList<BTMember> members = new ArrayList<>();

        for(int i = 0; i < connections.size(); i++) {
            BTConnection connection = connections.get(i);
            members.add(new BTMember(connection.getRemoteDevice()));
        }

        return members;
    }
}
