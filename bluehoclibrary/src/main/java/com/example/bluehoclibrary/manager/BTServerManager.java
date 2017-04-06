package com.example.bluehoclibrary.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.bluehoclibrary.R;
import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.database.model.DBMessage;
import com.example.bluehoclibrary.filter.BTServerMessageFilter;
import com.example.bluehoclibrary.format.request.BTEncryptedTextMessage;
import com.example.bluehoclibrary.format.request.BTGroupMessage;
import com.example.bluehoclibrary.format.request.BTMemberMessage;
import com.example.bluehoclibrary.handler.ConnectionHandler;
import com.example.bluehoclibrary.handler.MessageHandler;
import com.example.bluehoclibrary.manager.base.BTManager;
import com.example.bluehoclibrary.middleware.mom.BTLooperThread;
import com.example.bluehoclibrary.model.BTConnection;
import com.example.bluehoclibrary.model.BTGroup;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.rsa.PublicKey;
import com.example.bluehoclibrary.rsa.RSA;
import com.example.bluehoclibrary.service.BTServerService;
import com.example.bluehoclibrary.thread.AcceptThread;
import com.example.bluehoclibrary.thread.ConnectedThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class BTServerManager extends BTManager implements BTConfigure {
    private static String TAG = "BTServerManager";

    private AcceptThread acceptThread;
    private BTServerService bluetoothServerService;

    private BTServerConnectionManager connectionManager;

    private BTBroadcastManager broadcastManager;

    private BTMemberManager memberManager;

    private Vibrator vibrator;

    private RSA rsa;

    private DatabaseHelper databaseHelper;

//    private ConnectionHandler connectionHandler = new ConnectionHandler() {
//        @Override
//        public void onConnectSuccessful(Object object) {
//            super.onConnectSuccessful(object);
//            Toast.makeText(bluetoothServerService.getBaseContext(), "Connect Successfully", Toast.LENGTH_LONG).show();
//            connected((BluetoothSocket)object);
//        }
//
//        @Override
//        public void onConnectBreak(Object object) {
//            super.onConnectBreak(object);
//            broadcastManager.broadcastConnectBreak(connectionManager.getConnectionByAddress(object.toString()).getRemoteDevice());
//            connectionManager.removeConnectionByAddress(object.toString());
//            vibrator.vibrate(CONNECT_BREAK_VIBERATE_TIME);
//        }
//    };

    private BTLooperThread looperThread;

//    private MessageHandler messageHandler = new MessageHandler() {
//        @Override
//        public void onNewMessage(Object object) {
//            super.onNewMessage(object);
//            Log.v(TAG, (String)object);
//        }
//    };

    public BTServerManager(final BTServerService serverService) {
        looperThread = new BTLooperThread() {
            @Override
            public void initHandler() {
                connectionHandler = new ConnectionHandler() {
                    @Override
                    public void onConnectSuccessful(Object object) {
                        super.onConnectSuccessful(object);
                        Toast.makeText(bluetoothServerService.getBaseContext(), "Connect Successfully", Toast.LENGTH_LONG).show();
                        connected((BluetoothSocket)object);
                    }

                    @Override
                    public void onConnectBreak(Object object) {
                        super.onConnectBreak(object);
                        broadcastManager.broadcastConnectBreak(connectionManager.getConnectionByAddress(object.toString()).getRemoteDevice());
                        connectionManager.removeConnectionByAddress(object.toString());
                        vibrator.vibrate(CONNECT_BREAK_VIBERATE_TIME);
                        broadcast(new BTMemberMessage(null, memberManager).getMessageJSON().toString());
                    }
                };

                messageHandler = new MessageHandler() {
                    @Override
                    public void onNewMessage(final Object object) {
                        super.onNewMessage(object);
                        Log.v(TAG, (String)object);

                        BTServerMessageFilter messageFilter = new BTServerMessageFilter(BTServerManager.this, (String)object) {

                            @Override
                            public void onJoinMessageCome(JSONObject object) {
                                super.onJoinMessageCome(object);
                                try {
                                    JSONObject from = object.getJSONObject("from");
                                    BTMember member = new BTMember(from);
                                    databaseHelper.createMember(new DBMember(member));
                                    connectionManager.getConnectionByAddress(from.getString("address")).getMember().setPublicKey(new PublicKey(object.getJSONObject("content").getJSONObject("member").getJSONObject("public_key")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                broadcast(new BTGroupMessage(null, new BTGroup(memberManager.getMembers())).getMessageJSON().toString());
                            }

                            @Override
                            public void onEncryptedTextComeToServer(String encrypted_content) {
                                super.onEncryptedTextComeToServer(encrypted_content);

                                String decrypted_content = decryptTextMessage(encrypted_content);
                                Log.v(TAG, decrypted_content);
                                try {
                                    JSONObject messageJSON = new JSONObject((String)object);
                                    messageJSON.getJSONObject("content").remove("content");
                                    messageJSON.getJSONObject("content").put("content", decrypted_content);
                                    databaseHelper.createMessage(new DBMessage((messageJSON)));
                                    broadcastManager.broadcastNewMessage(messageJSON.toString());
                                    block();
                                    BTServerManager.this.notify(decrypted_content);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onTextMessageCome(JSONObject object) {
                                super.onTextMessageCome(object);
                            }

                            @Override
                            public void onTextMessageComeToServer(JSONObject object) {
                                super.onTextMessageComeToServer(object);
                                databaseHelper.createMessage(new DBMessage((object)));
                            }

                            public String decryptTextMessage(String encrypted_content) {
                                Log.v(TAG, "Decrypt with D: " + rsa.getPrivateKey().getD() + " N: " + rsa.getPrivateKey().getN());

                                Log.d(TAG, "encrypted_content: " + encrypted_content);

                                String content = null;

                                content = new String(rsa.decrypt(RSA.hexToBytes(encrypted_content)));
                                Log.d(TAG, "encrypted_content: " + encrypted_content);

                                return content;
                            }

                            @Override
                            public void onGroupTextMessageCome(JSONObject groupTextMessageObject) {
                                super.onGroupTextMessageCome(groupTextMessageObject);

                                broadcast(groupTextMessageObject.toString());
                            }
                        };

                        messageFilter.classifyMessage();

                        Log.v(TAG, Boolean.toString(messageFilter.getIsBlocked()));

                        if(!messageFilter.getIsBlocked()) {
                            Log.v(TAG, (String)object);
                            broadcastManager.broadcastNewMessage((String)object);
                        }
                    }
                };
            }
        };

        looperThread.start();
        rsa = new RSA();
        this.bluetoothServerService = serverService;
        this.connectionManager = new BTServerConnectionManager();
        this.broadcastManager = new BTBroadcastManager(serverService);
        this.memberManager = new BTMemberManager(connectionManager, rsa.getPublicKey());
        this.databaseHelper = new DatabaseHelper(serverService.getBaseContext());
        this.databaseHelper.createMember(new DBMember(new BTMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress)));
        this.vibrator = (Vibrator) serverService.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void accept() {
        acceptThread = new AcceptThread(looperThread.connectionHandler);
        acceptThread.start();
    }

    public void connected(BluetoothSocket socket) {
        ConnectedThread connectedThread = new ConnectedThread(socket, looperThread.messageHandler, looperThread.connectionHandler);
        connectedThread.start();
        connectionManager.addNewConnection(new BTConnection(connectedThread));
        broadcastManager.broadcastGetNewConnection(socket.getRemoteDevice());
        //broadcast(new BTMemberMessage(null, memberManager).getMessageJSON().toString());
        //broadcast(new BTGroupMessage(null, new BTGroup(memberManager.getMembers())).getMessageJSON().toString());
    }

    public void stopThreads() {
        if(acceptThread != null) {
            acceptThread.cancel();
        }

        if(connectionManager.getNumOfConnections() > 0) {
            connectionManager.stopConnectedThreads();
        }
    }

    @Override
    public void sendTo(String address, String content) {
        for(BTConnection connection : connectionManager.getBTConnections()) {
            if(connection.getAddress().equals(address)) {
                connection.getConnectedThread().write(content.getBytes());
            }
        }
    }

    public void sendEncryptedTextTo(BTMember member, String raw_content) {
        for(BTConnection connection : connectionManager.getBTConnections()) {
            if(connection.getAddress().equals(member.getAddress())) {
                PublicKey publicKey = connection.getMember().getPublicKey();
                Log.v(TAG, "Encrypt with E" + publicKey.getE() + " N: " + publicKey.getN());
                String encrypted_content = RSA.bytesToHex(new RSA(publicKey).encrypt(raw_content.getBytes()));
                Log.d(TAG, "encrypt_content: " + encrypted_content);
                ArrayList<BTMember> toMembers = new ArrayList<>();
                toMembers.add(member);
                connection.getConnectedThread().write(new BTEncryptedTextMessage(toMembers, encrypted_content).getMessageJSON().toString().getBytes());
            }
        }
    }

    @Override
    public void multicast(ArrayList<String> addresses, String content) {
        for(String address : addresses) {
            for(BTConnection connection : connectionManager.getBTConnections()) {
                Log.v(TAG, address + " VS " + connection.getAddress());
                if(address.equals(connection.getAddress())) {
                    connection.getConnectedThread().write(content.getBytes());
                }
            }
        }
    }

    @Override
    public void broadcast(String content) {
        for(BTConnection connection : connectionManager.getBTConnections()) {
            connection.getConnectedThread().write(content.getBytes());
        }
    }

    public void broadcastGroupMembers() {
        Log.v(TAG, Integer.toString(memberManager.getMembers().size()));
        broadcastManager.broadcastGroupMembers(memberManager.getMembers());
    }

    public BTServerService getBTServerService() {
        return bluetoothServerService;
    }

    public void notify(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(bluetoothServerService)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("My notification")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) bluetoothServerService.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }
}
