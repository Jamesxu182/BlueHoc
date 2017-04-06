package com.example.james.bluehoc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.BTServerController;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTGroup;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.receiver.MessageBroadcastReceiver;
import com.example.james.bluehoc.adapter.BTGroupsRecyclerViewAdapter;
import com.example.james.bluehoc.behaviour.AddFloatingActionButtonBehaviour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BTConfigure {
    private static final String TAG = "GroupsFragment";
    private static final int DISCOVER_TIME = 5;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private boolean isJump = false;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private BluetoothAdapter bluetoothAdapter;

    private List<BTGroup> groups;

    private List<BluetoothDevice> bluetoothDevices;
    private FloatingActionButton addFloatingActionButton;

    private final BroadcastReceiver discoverBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG, action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v(TAG, "Name: " + device.getName() + " Address: " + device.getAddress());
                bluetoothDevices.add(device);
                groups.add(new BTGroup(device));
                recyclerView.setAdapter(new BTGroupsRecyclerViewAdapter(groups, MainActivity.this));
            }
        }
    };

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver() {
        @Override
        public void onNewMessageCome(Bundle bundle) {
            super.onNewMessageCome(bundle);
            Log.v(TAG, bundle.getString("raw"));

            try {
                JSONObject json = new JSONObject(bundle.getString("raw"));

                switch(json.getString("method")) {
                    case "member":
                        break;
                    case "group": {
                        if(!isJump) {
                            handleGroupRequest(json);
                            isJump = true;
                        }
                        break;
                    }
                    case "text":
                        break;
                    default:
                        break;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void handleGroupRequest(JSONObject json) {
            ArrayList<BTMember> members = new ArrayList<>();
            JSONObject contentObject = null;
            try {
                contentObject = json.getJSONObject("content");
                JSONArray membersJSONArray = contentObject.getJSONObject("group").getJSONArray("members");

                for(int i = 0; i < membersJSONArray.length(); i++) {
                    JSONObject memberJSONObject = membersJSONArray.getJSONObject(i);

                    Log.v(TAG, "Name: " + memberJSONObject.getString("name") + ", Address: " + memberJSONObject.getString("address"));

                    members.add(new BTMember(memberJSONObject.getString("name"), memberJSONObject.getString("address")));
                }

                switchToSelectActivityWithMembers(members);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTBaseController.setMyBluetoothAddress(android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), "bluetooth_address"));
        BTBaseController.setMyBluetoothName(BluetoothAdapter.getDefaultAdapter().getName());

        getSupportActionBar().setTitle(BTBaseController.myBluetoothName);

        init();

        if (bluetoothAdapter == null) {
            Log.v(TAG, "The devices is not support Bluetooth.");
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        isJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoverBroadcastReceiver);
        unregisterReceiver(messageBroadcastReceiver);
    }

    private void initGroups() {
        groups = new ArrayList<BTGroup>();
    }

    private void initReceiver() {
        IntentFilter discoveryIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoverBroadcastReceiver, discoveryIntentFilter);

        IntentFilter messageIntentFilter = new IntentFilter(BTConfigure.NEW_MESSAGE_ACTION);
        registerReceiver(messageBroadcastReceiver, messageIntentFilter);
    }

    private void initRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkPermissions();

                if(bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                groups = new ArrayList<>();
                discoverSurroundingDevices();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView)findViewById(R.id.groups_recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new BTGroupsRecyclerViewAdapter(groups, this));
    }

    private void initActionFloatingButton() {
        addFloatingActionButton = (FloatingActionButton)findViewById(R.id.add_floating_action_button);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) addFloatingActionButton.getLayoutParams();
        params.setBehavior(new AddFloatingActionButtonBehaviour());
        addFloatingActionButton.setLayoutParams(params);

        addFloatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BTServerController(MainActivity.this).startServerService();
                switchToSelectActivity();
            }
        });
    }

    private void initBluetooth() {
        bluetoothDevices = new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void init() {
        initBluetooth();
        initGroups();
        initReceiver();

        initBluetooth();

        initRefreshLayout();
        initRecyclerView();
        initActionFloatingButton();
    }

    public void discoverSurroundingDevices() {
        Toast.makeText(getBaseContext(), "discovering...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int elapsed = 0;

                bluetoothAdapter.startDiscovery();

                while(elapsed < DISCOVER_TIME) {
                    try {
                        Thread.sleep(1000);
                        elapsed++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                bluetoothAdapter.cancelDiscovery();
                Log.v(TAG, "discovery cancel");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void switchToSelectActivity() {
        Intent intent = new Intent(this, SelectActivity.class);

        startActivity(intent);
    }

    private void switchToSelectActivityWithMembers(ArrayList<BTMember> members) {
        Intent intent = new Intent(this, SelectActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("members", members);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void checkPermissions() {
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            // do nothing
        } else if(locationPermissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSIONS_REQUEST_CODE);
        } else {
            // do nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_LOCATION_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if(swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if(bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                }

                break;
            }
            default:
                break;
        }
    }
}
