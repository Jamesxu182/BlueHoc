package com.example.bluehoclibrary.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.bluehoclibrary.R;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.database.model.DBMessage;
import com.example.bluehoclibrary.filter.BTClientMessageFilter;
import com.example.bluehoclibrary.format.request.BTEncryptedTextMessage;
import com.example.bluehoclibrary.format.request.BTJoinMessage;
import com.example.bluehoclibrary.handler.ConnectionHandler;
import com.example.bluehoclibrary.handler.MessageHandler;
import com.example.bluehoclibrary.manager.base.BTClientPublicKeyManager;
import com.example.bluehoclibrary.manager.base.BTManager;
import com.example.bluehoclibrary.middleware.mom.BTLooperThread;
import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.rsa.PublicKey;
import com.example.bluehoclibrary.rsa.RSA;
import com.example.bluehoclibrary.service.BTClientService;
import com.example.bluehoclibrary.thread.ConnectThread;
import com.example.bluehoclibrary.thread.ConnectedThread;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by james on 2/27/17.
 */

public class BTClientManager extends BTManager {
    private static final String TAG = "BTClientManager";

    private ConnectedThread connectedThread;
    private BTConnection connection;
    private BluetoothDevice remoteDevice;

    private BTClientService bluetoothClientService;

    private BTBroadcastManager broadcastManager;

    private Vibrator vibrator;

    private ArrayList<BTMember> members;

    private RSA rsa;

    private BTClientPublicKeyManager publicKeyManagerManager;

    private DatabaseHelper databaseHelper;

//    private ConnectionHandler connectionHandler = new ConnectionHandler() {
//        @Override
//        public void onConnectSuccessful(Object object) {
//            super.onConnectSuccessful(object);
//            handleConnectSuccessfullyEvent((BluetoothSocket)object);
//        }
//
//        @Override
//        public void onConnectFailed(Object object) {
//            super.onConnectFailed(object);
//        }
//
//        @Override
//        public void onConnectBreak(Object object) {
//            super.onConnectBreak(object);
//            broadcastManager.broadcastConnectBreak(remoteDevice);
//            remoteDevice = null;
//            vibrator.vibrate(CONNECT_BREAK_VIBERATE_TIME);
//        }
//    };

//    private MessageHandler messageHandler = new MessageHandler() {
//        @Override
//        public void onNewMessage(Object object) {
//            super.onNewMessage(object);
//            Log.v(TAG, (String)object);
//
//            BTClientMessageFilter messageFilter = new BTClientMessageFilter((String)object) {
//                @Override
//                public ArrayList<BTMember> onGetNewMember(JSONArray membersJSONArray) {
//                    members = super.onGetNewMember(membersJSONArray);
//
//                    return members;
//                }
//            };
//
//            messageFilter.classifyMessage();
//
//            Log.v(TAG, Integer.toString(members.size()));
//        }
//    };

    private BTLooperThread looperThread;

    public BTClientManager(final BTClientService clientService) {
        looperThread = new BTLooperThread() {
            @Override
            public void initHandler() {
                    connectionHandler = new ConnectionHandler() {
                    @Override
                    public void onConnectSuccessful(Object object) {
                        super.onConnectSuccessful(object);
                        handleConnectSuccessfullyEvent((BluetoothSocket)object);
                    }

                    @Override
                    public void onConnectFailed(Object object) {
                        super.onConnectFailed(object);
                    }

                    @Override
                    public void onConnectBreak(Object object) {
                        super.onConnectBreak(object);
                        broadcastManager.broadcastConnectBreak(remoteDevice);
                        remoteDevice = null;
                        vibrator.vibrate(CONNECT_BREAK_VIBERATE_TIME);
                    }
                };

                messageHandler = new MessageHandler() {
                    @Override
                    public void onNewMessage(final Object object) {
                        super.onNewMessage(object);
                        Log.v(TAG, (String)object);

                        BTClientMessageFilter messageFilter = new BTClientMessageFilter(BTClientManager.this, (String)object) {
                            @Override
                            public ArrayList<BTMember> onGetNewMember(JSONArray membersJSONArray) {
                                members = super.onGetNewMember(membersJSONArray);
                                return members;
                            }

                            @Override
                            public void onEncryptedTextCome(String encrypted_content) {
                                super.onEncryptedTextCome(encrypted_content);

                                String decrypted_content = decryptTextMessage(encrypted_content);;
                                Log.v(TAG, decrypted_content);
                                try {
                                    JSONObject messageJSON = new JSONObject((String)object);
                                    messageJSON.getJSONObject("content").remove("content");
                                    messageJSON.getJSONObject("content").put("content", decrypted_content);
                                    databaseHelper.createMessage(new DBMessage((messageJSON)));
                                    broadcastManager.broadcastNewMessage(messageJSON.toString());
                                    block();

                                    BTClientManager.this.notify(decrypted_content);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onTextCome(String content) {
                                super.onTextCome(content);
                                try {
                                    JSONObject messageJSON = new JSONObject((String)object);
                                    databaseHelper.createMessage(new DBMessage((messageJSON)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onGroupMessageCome(JSONObject groupObject) {
                                super.onGroupMessageCome(groupObject);
                                try {
                                    JSONArray membersJSONArray = groupObject.getJSONArray("members");
                                    for(int i = 0; i < membersJSONArray.length(); i++) {
                                        JSONObject memberJSONObject = membersJSONArray.getJSONObject(i);
                                        BTMember member = new BTMember(memberJSONObject);
                                        databaseHelper.createMember(new DBMember(member));
                                        publicKeyManagerManager.addNewPublicKey(memberJSONObject.getString("address"), new PublicKey(memberJSONObject.getJSONObject("public_key")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        messageFilter.classifyMessage();

                        if(!messageFilter.getIsBlock()) {
                            broadcastManager.broadcastNewMessage((String)object);
                        }
                    }

                    public String decryptTextMessage(String encrypted_content) {
                        Log.v(TAG, "Decrypt with D: " + rsa.getPrivateKey().getD() + " N: " + rsa.getPrivateKey().getN());

                        Log.d(TAG, "encrypted_content: " + encrypted_content);

                        String content = null;

                        content = new String(rsa.decrypt(RSA.hexToBytes(encrypted_content)));
                        Log.d(TAG, "encrypted_content: " + encrypted_content);

                        return content;
                    }
                };
            }
        };

        looperThread.start();

        this.bluetoothClientService = clientService;
        this.broadcastManager = new BTBroadcastManager(clientService);
        this.vibrator = (Vibrator) clientService.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.rsa = new RSA();
        this.databaseHelper = new DatabaseHelper(clientService.getBaseContext());
        this.databaseHelper.createMember(new DBMember(new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress)));
        publicKeyManagerManager = new BTClientPublicKeyManager();

        members = new ArrayList<>();
    }

    public void connect(BluetoothDevice device) {
        new ConnectThread(device, looperThread.connectionHandler).start();
    }

    public void connected(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket, looperThread.messageHandler, looperThread.connectionHandler);
        connection = new BTConnection(connectedThread);
        remoteDevice = connectedThread.getRemoteDevice();
        connectedThread.start();
    }

    public void stopThreads() {
        if(connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
            connection = null;
        }
    }

    public void handleConnectSuccessfullyEvent(BluetoothSocket socket) {
        bluetoothClientService.showTextOnToast("Connection with " + socket.getRemoteDevice().getAddress());
        connected(socket);
        broadcastManager.broadcastConnectSuccessfully();
        ArrayList<BTMember> to = new ArrayList<>();
        to.add(new BTMember(socket.getRemoteDevice()));
        sendTo(socket.getRemoteDevice().getAddress(), new BTJoinMessage(to, rsa.getPublicKey()).getMessageJSON().toString());
    }

    public BluetoothDevice getRemoteDevice() {
        return remoteDevice;
    }

    public ArrayList<BTMember> getMembers() {
        return members;
    }

    public void broadcastGroupMembers() {
        broadcastManager.broadcastGroupMembers(members);
    }

    @Override
    public void sendTo(String address, String content) {
        Log.v(TAG, content);
        connectedThread.write(content.getBytes());
    }

    public void sendEncryptedTextTo(BTMember member, String raw_content) {
            PublicKey publicKey = publicKeyManagerManager.getPublicKeyWithAddress(member.getAddress());
            Log.v(TAG, "Encrypt with E" + publicKey.getE() + " N: " + publicKey.getN());
            String encrypted_content = RSA.bytesToHex(new RSA(publicKey).encrypt(raw_content.getBytes()));
            Log.d(TAG, "encrypt_content: " + encrypted_content);
            ArrayList<BTMember> toMemebers = new ArrayList<>();
            toMemebers.add(member);
            connection.getConnectedThread().write(new BTEncryptedTextMessage(toMemebers, encrypted_content).getMessageJSON().toString().getBytes());
    }

    @Override
    public void multicast(ArrayList<String> addresses, String content) {
        connectedThread.write(content.getBytes());
    }

    @Override
    public void broadcast(String content) {
        connectedThread.write(content.getBytes());
    }

    public void notify(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(bluetoothClientService)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("My notification")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) bluetoothClientService.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }

    public Service getClientService() {
        return bluetoothClientService;
    }
}
