package com.example.james.bluehoc.fragment;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.BTClientController;
import com.example.bluehoclibrary.controller.BTServerController;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.receiver.ConnectionBroadcastReceiver;
import com.example.bluehoclibrary.receiver.MemberBroadcastReceiver;
import com.example.bluehoclibrary.receiver.MessageBroadcastReceiver;
import com.example.james.bluehoc.ChatActivity;
import com.example.james.bluehoc.R;
import com.example.james.bluehoc.adapter.BTMemberListCustomArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    private ArrayList<BTMember> members;

    private ListView membersListView;
    private BTMemberListCustomArrayAdapter memberListViewAdapter;

    private BTBaseController controller;

    private TextView serverNameTextView;
    private TextView serverAddressTextView;

    private ConnectionBroadcastReceiver connectionBroadcastReceiver = new ConnectionBroadcastReceiver() {
        @Override
        public void onNewConnection(Bundle bundle) {
            super.onNewConnection(bundle);

            controller.checkMembersInGroup();
        }

        @Override
        public void onConnectionBreak(Bundle bundle) {
            super.onConnectionBreak(bundle);

            BluetoothDevice device = bundle.getParcelable("device");

            controller.checkMembersInGroup();

            Toast.makeText(getContext(), "Connection break with " + device.getName() + ", " + device.getAddress(), Toast.LENGTH_LONG).show();

            controller.checkMembersInGroup();
        }
    };

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver() {
        @Override
        public void onNewMessageCome(Bundle bundle) {
            super.onNewMessageCome(bundle);
//            Log.v(TAG, bundle.getString("raw"));
            try {
                JSONObject json = new JSONObject(bundle.getString("raw"));
                switch(json.getString("method")) {
                    case "member":
                        controller.checkMembersInGroup();
                        break;
                    case "group":
                        handleGroupRequest(json);
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void handleGroupRequest(JSONObject json) {
            members = new ArrayList<>();
            JSONObject contentObject = null;
            try {
                contentObject = json.getJSONObject("content");
                JSONArray membersJSONArray = contentObject.getJSONObject("group").getJSONArray("members");

                for(int i = 0; i < membersJSONArray.length(); i++) {
                    JSONObject memberJSONObject = membersJSONArray.getJSONObject(i);

                    members.add(new BTMember(memberJSONObject.getString("name"), memberJSONObject.getString("address")));
                }

                for(int i = 0; i < members.size(); i++) {
                    BTMember member = members.get(i);
                    if(member.getAddress().equals(BTBaseController.myBluetoothAddress)) {
                        members.remove(member);
                    }
                }

                updateMemberList(members);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private MemberBroadcastReceiver memberBroadcastReceiver = new MemberBroadcastReceiver() {
        @Override
        public void onGetMembers(ArrayList<BTMember> members) {

            for(int i = 0; i < members.size(); i++) {
                BTMember member = members.get(i);
                if(member.getAddress().equals(BTBaseController.myBluetoothAddress)) {
                    members.remove(member);
                }
            }

            updateMemberList(members);
        }
    };

    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initController();
        initReceivers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            members = bundle.getParcelableArrayList("members");

            for(int i = 0; i < members.size(); i++) {
                BTMember member = members.get(i);
                if(member.getAddress().equals(BTBaseController.myBluetoothAddress)) {
                    members.remove(member);
                }
            }
        } else {
            members = new ArrayList<>();
        }

        membersListView = (ListView)view.findViewById(R.id.members_list_view);
        memberListViewAdapter = new BTMemberListCustomArrayAdapter(getContext(), R.layout.custom_member_list_layout, members);
        membersListView.setAdapter(memberListViewAdapter);

        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTMember member = (BTMember)parent.getItemAtPosition(position);
                switchToChatActivityWithBTMember(member);
            }
        });

        membersListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // click
        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(membersListView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                    BTMember member = (BTMember)parent.getItemAtPosition(position);
                    switchToChatActivityWithBTMember(member);
                } else {
                    membersListView.setItemChecked(position, true);
                }
            }
        });

        membersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                membersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                membersListView.setItemsCanFocus(false);

                membersListView.setItemChecked(position, true);

                return true;
            }
        });

        membersListView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked) {
                    memberListViewAdapter.toggleSelection(position);
                } else {
                    memberListViewAdapter.toggleSelection(position);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.selection_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.send_menu_item:
                        handleSendEvent();
                        mode.finish();
                        break;
                    default:
                        break;
                }

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if(membersListView.getChoiceMode() != ListView.CHOICE_MODE_SINGLE) {
                    membersListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }
                memberListViewAdapter.removeSelection();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(connectionBroadcastReceiver);
        getActivity().unregisterReceiver(messageBroadcastReceiver);
        getActivity().unregisterReceiver(memberBroadcastReceiver);
    }

    public void initController() {
        if(BTConfigure.SERVER_TYPE.equals(BTBaseController.type)) {
            controller = new BTServerController(getActivity());
        } else if(BTConfigure.CLIENT_TYPE.equals(BTBaseController.type)) {
            controller = new BTClientController(getActivity());
        }
    }

    public void updateMemberList(ArrayList<BTMember> newMembers) {
        memberListViewAdapter  = new BTMemberListCustomArrayAdapter(getContext(), R.layout.custom_member_list_layout, newMembers);
        membersListView.setAdapter(memberListViewAdapter);
    }

    public void initReceivers() {
        IntentFilter connectionIntentFilter = new IntentFilter(BTConfigure.CONNECT_ACTION);
        getActivity().registerReceiver(connectionBroadcastReceiver, connectionIntentFilter);

        IntentFilter messageIntentFilter = new IntentFilter(BTConfigure.NEW_MESSAGE_ACTION);
        getActivity().registerReceiver(messageBroadcastReceiver, messageIntentFilter);

        IntentFilter memberIntentFilter = new IntentFilter(BTConfigure.BROADCAST_MEMBERS_ACTION);
        getActivity().registerReceiver(memberBroadcastReceiver, memberIntentFilter);
    }

    private void switchToChatActivityWithBTMember(BTMember member) {
        ArrayList<BTMember> members = new ArrayList<>();
        members.add(member);

        switchToChatActivityWithBTMembers(members);
    }

    private void switchToChatActivityWithBTMembers(ArrayList members) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", members);
        intent.putExtras(bundle);

        getActivity().startActivity(intent);
    }

    private void handleSendEvent() {
        SparseBooleanArray selected = memberListViewAdapter.getSelectedIds();
        ArrayList<BTMember> selectedMembers = new ArrayList<>();

        for(int i = 0; i < selected.size(); i++) {
            int position = selected.keyAt(i);
            if(selected.get(position)) {
                selectedMembers.add(memberListViewAdapter.getItem(position));
            }
        }

        switchToChatActivityWithBTMembers(selectedMembers);
    }
}
