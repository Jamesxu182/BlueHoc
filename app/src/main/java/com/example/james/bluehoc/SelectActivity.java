package com.example.james.bluehoc;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluehoclibrary.configure.BTConfigure;
import com.example.bluehoclibrary.controller.BTClientController;
import com.example.bluehoclibrary.controller.BTServerController;
import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.receiver.ConnectionBroadcastReceiver;
import com.example.bluehoclibrary.receiver.MessageBroadcastReceiver;
import com.example.bluehoclibrary.service.base.BTBaseService;
import com.example.james.bluehoc.adapter.MainPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;

public class SelectActivity extends AppCompatActivity implements BTConfigure {
    private final String TAG = "SelectActivity";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private MainPagerAdapter pagerAdapter;

    private BTBaseController controller;

    private FloatingActionButton upFloatingActionButton;

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;

    private TextView serverNameTextView;
    private TextView serverAddressTextView;

    private Button quitButton;
    private Button groupButton;

    private ConnectionBroadcastReceiver connectionBroadcastReceiver = new ConnectionBroadcastReceiver() {

        @Override
        public void onConnectionBreak(Bundle bundle) {
            super.onConnectionBreak(bundle);
            if(CLIENT_TYPE.equals(BTBaseController.type)) {
                switchToMainActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        getSupportActionBar().setTitle(BTBaseController.myBluetoothName);

        init();
    }

    public void initReceiver() {
        IntentFilter connectionIntentFilter = new IntentFilter(BTConfigure.CONNECT_ACTION);
        registerReceiver(connectionBroadcastReceiver, connectionIntentFilter);
    }

    public void initController() {
        if(BTConfigure.SERVER_TYPE.equals(BTBaseController.type)) {
            controller = new BTServerController(this);
        } else if(BTConfigure.CLIENT_TYPE.equals(BTBaseController.type)) {
            controller = new BTClientController(this);
        }
    }

    private void initViewPager() {
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), bundle);
            ArrayList<BTMember> members = bundle.getParcelableArrayList("members");
            BTMember server = members.get(0);
            serverNameTextView.setText(server.getName());
            serverAddressTextView.setText(server.getAddress());
        } else {
            pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        }

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initTabBar() {
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ;
            }
        });
    }

    private void initFloatingActionButton() {
        upFloatingActionButton = (FloatingActionButton)findViewById(R.id.group_floating_action_button);

        upFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }


    private void initBottomSheet() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        //bottomSheetBehavior.setPeekHeight(443);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        serverNameTextView = (TextView)findViewById(R.id.group_name_text_view);
        serverAddressTextView = (TextView)findViewById(R.id.group_address_text_view);

        quitButton = (Button)findViewById(R.id.group_quit_button);
        groupButton = (Button)findViewById(R.id.group_chat_button);

        if(BTBaseController.type.equals(SERVER_TYPE)) {
            serverNameTextView.setText(BTBaseController.myBluetoothName);
            serverAddressTextView.setText(BTBaseController.myBluetoothAddress);
        }


        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitAlertDialog();
            }
        });

        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToGroupActivity();
            }
        });
    }

    private void init() {
        initReceiver();

        initController();
        initTabBar();

        initBottomSheet();
        initFloatingActionButton();

        initViewPager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy executed.");

        unregisterReceiver(connectionBroadcastReceiver);

        controller.stopService();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        showQuitAlertDialog();
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void switchToGroupActivity() {
        Intent intent = new Intent(this, GroupChatActivity.class);

        startActivity(intent);
    }

    private void showQuitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning");

        builder.setMessage("Are you sure to quit this Bluetooth group");

        // positive button
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
