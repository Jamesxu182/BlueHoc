package com.example.james.bluehoc;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.BTClientController;
import com.example.bluehoclibrary.controller.BTServerController;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.model.BTMessage;
import com.example.bluehoclibrary.receiver.MessageBroadcastReceiver;
import com.example.james.bluehoc.adapter.BTMessageListViewArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity implements BTConfigure {
    private final static String TAG = "GroupChatActivity";

    private ListView groupChatListView;
    private EditText groupMessageEditText;
    private Button groupMessageSendButton;

    private BTMessageListViewArrayAdapter messageListViewArrayAdapter;

    private BTBaseController bluetoothController;

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver() {
        @Override
        public void onNewMessageCome(Bundle bundle) {
            super.onNewMessageCome(bundle);
            try {
                JSONObject json = new JSONObject(bundle.getString("raw"));

                Log.v(TAG, json.toString());

                switch (json.getString("method")) {
                    case "group_text":
                        onGroupTextMessageCome(json);
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void onGroupTextMessageCome(JSONObject json) {
            try {
                BTMember fromMember = new BTMember(json.getJSONObject("from"));
                String content = json.getJSONObject("content").getString("content");
                BTMessage newMessage = new BTMessage(new ArrayList<BTMember>(), content, fromMember);

                addNewMessage(newMessage);
                Log.v(TAG, content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        initBroadcastReceiver();
        initBTController();
        initGroupChatListView();
        initGroupMessageSendButton();
        initGroupMessageEditText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageBroadcastReceiver);
    }

    private void initBroadcastReceiver() {
        IntentFilter messageIntentFilter = new IntentFilter(NEW_MESSAGE_ACTION);
        registerReceiver(messageBroadcastReceiver, messageIntentFilter);
    }

    private void initBTController() {
        if(BTBaseController.type == SERVER_TYPE) {
            bluetoothController = new BTServerController(this);
        } else if(BTBaseController.type == CLIENT_TYPE) {
            bluetoothController = new BTClientController(this);
        }
    }

    private void initGroupChatListView() {
        groupChatListView = (ListView)findViewById(R.id.group_chat_list_view);
        messageListViewArrayAdapter = new BTMessageListViewArrayAdapter(getBaseContext(), R.layout.message_right, new ArrayList<BTMessage>());
        groupChatListView.setAdapter(messageListViewArrayAdapter);
    }

    private void initGroupMessageSendButton() {
        groupMessageSendButton = (Button)findViewById(R.id.group_message_send_button);

        groupMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = groupMessageEditText.getText().toString();

                BTMessage newMessage = new BTMessage(null, content);

                bluetoothController.sendGroupTextMessageTo(content);

                // clear edit text
                groupMessageEditText.setText("");

                if(BTBaseController.type == SERVER_TYPE) {
                    addNewMessage(newMessage);
                }
            }
        });
    }

    private void initGroupMessageEditText() {
        groupMessageEditText = (EditText)findViewById(R.id.group_message_edit_text);
    }

    public void addNewMessage(BTMessage message) {
        messageListViewArrayAdapter.add(message);
        messageListViewArrayAdapter.notifyDataSetChanged();
        goToListViewBottom();
    }

    private void goToListViewBottom() {
        groupChatListView.post(new Runnable() {
            @Override
            public void run() {
                groupChatListView.setSelection(groupChatListView.getCount() - 1);
            }
        });
    }
}
