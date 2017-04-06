package com.example.james.bluehoc;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.BTClientController;
import com.example.bluehoclibrary.controller.BTServerController;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.database.model.DBMessage;
import com.example.bluehoclibrary.format.request.BTTextMessage;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.model.BTMessage;
import com.example.bluehoclibrary.receiver.MessageBroadcastReceiver;
import com.example.james.bluehoc.adapter.BTMessageListViewArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements BTConfigure {
    private static final String TAG = "ChatActivity";
    private ArrayList<BTMember> members;

    private ListView messageListView;
    private BTMessageListViewArrayAdapter messageListViewArrayAdapter;

    private Button messageSendButton;
    private EditText messageEditText;
    private Switch messageEncryptSwitch;

    private BTBaseController bluetoothController;

    private DatabaseHelper databaseHelper;

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver() {
        @Override
        public void onNewMessageCome(Bundle bundle) {
            super.onNewMessageCome(bundle);
            try {
                JSONObject json  = new JSONObject(bundle.getString("raw"));

                switch(json.getString("method")) {
                    case "text":
                        handleNewText(json);
                        break;
                    case "encrypted_text":
                        handleNewEncryptedText(json);
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void handleNewText(JSONObject json) {
            Log.v(TAG, json.toString());
            try {
                JSONArray membersJSONArray = json.getJSONArray("to");
                ArrayList<BTMember> newMembers = new ArrayList<>();

                for(int i = 0; i < members.size(); i++) {
                    BTMember member = members.get(i);
                    if(member.getAddress().equals(json.getJSONObject("from").getString("address"))) {
                        for(int j = 0; j < membersJSONArray.length(); j++) {
                            newMembers.add(new BTMember(membersJSONArray.getJSONObject(j)));
                            Log.v("TAG", member.getAddress() + " VS " + BTBaseController.myBluetoothAddress);
                        }

                        addNewMessage(new BTMessage(newMembers, json.getJSONObject("content").getString("content"), new BTMember(json.getJSONObject("from"))));

                        Toast.makeText(getBaseContext(), new Timestamp(new Date().getTime()).toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void handleNewEncryptedText(JSONObject json) {
            handleNewText(json);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseHelper = new DatabaseHelper(getBaseContext());


        if(BTBaseController.type == SERVER_TYPE) {
            bluetoothController = new BTServerController(ChatActivity.this);
        } else if(BTBaseController.type == CLIENT_TYPE) {
            bluetoothController = new BTClientController(ChatActivity.this);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            members = bundle.getParcelableArrayList("members");
        } else {
            finish();
        }

        getSupportActionBar().setTitle(BTBaseController.myBluetoothName);

        messageEditText = (EditText)findViewById(R.id.message_edit_text);

        messageEncryptSwitch = (Switch)findViewById(R.id.message_encrypt_switch);

        if(members.size() == 1) {
            messageEncryptSwitch.setChecked(false);
            messageEncryptSwitch.setClickable(true);
        } else {
            messageEncryptSwitch.setChecked(false);
            messageEncryptSwitch.setClickable(false);
        }

        messageListView = (ListView)findViewById(R.id.chat_list_view);
        messageListViewArrayAdapter = new BTMessageListViewArrayAdapter(getBaseContext(), R.layout.message_right, getHistoricalMessages());
        messageListView.setAdapter(messageListViewArrayAdapter);

        goToListViewBottom();

        messageSendButton = (Button)findViewById(R.id.message_send_button);
        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = messageEditText.getText().toString();

                BTMessage newMessage = new BTMessage(members, content);

                if(members.size() == 1) {
                    if(messageEncryptSwitch.isChecked()) {
                        bluetoothController.sendEncryptedMessageTo(members.get(0), content);
                    } else {
                        bluetoothController.sendTo(members.get(0), new BTTextMessage(members, content).getMessageJSON().toString());
                    }
                } else {
                    bluetoothController.multicast(members, content);
                }

                // clear edit text
                messageEditText.setText("");

                addNewMessage(newMessage);

                for(BTMember member : members) {
                    databaseHelper.createMessage(new DBMessage(
                            new DBMember(BTBaseController.myBluetoothName, BTBaseController.myBluetoothAddress),
                            new DBMember(member),
                            content));
                }

                Toast.makeText(getBaseContext(), new Timestamp(new Date().getTime()).toString(), Toast.LENGTH_LONG).show();
            }
        });


        IntentFilter messageIntentFilter = new IntentFilter(NEW_MESSAGE_ACTION);
        registerReceiver(messageBroadcastReceiver, messageIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageBroadcastReceiver);
    }

    public void addNewMessage(BTMessage message) {
        messageListViewArrayAdapter.add(message);
        messageListViewArrayAdapter.notifyDataSetChanged();
        goToListViewBottom();
    }

    private void goToListViewBottom() {
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                messageListView.setSelection(messageListView.getCount() - 1);
            }
        });
    }

    private ArrayList<BTMessage> getHistoricalMessages() {
        ArrayList<BTMessage> oldMessages = new ArrayList<>();

        for(BTMember member : members) {
            List<DBMessage> historicalMessages = databaseHelper.getMessagesByAddress(member.getAddress());

            for(DBMessage historicalMessage : historicalMessages) {
                oldMessages.add(new BTMessage(historicalMessage));
            }
        }

        return oldMessages;
    }
}
