package com.example.bluehoclibrary.configure;

import java.util.UUID;

/**
 * Created by james on 2/23/17.
 */

public interface BTConfigure {
    String NAME = "BlueHoc";
    UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Connection Handler
    int CONNECT_SUCCESSFULLY = 1;
    int CONNECT_FAILED = 2;
    int CONNECT_BREAK = 3;

    // Message Handler
    int New_Message = 1;

    // Device TYPE
    String SERVER_TYPE = "SERVER";
    String CLIENT_TYPE = "CLIENT";

    // Connection Action
    String CONNECT_ACTION = "android.intent.action.CONNECT_ACTION";

    String CONNECT_SUCCESSFULLY_ACTON_STATE = "successful";
    String CONNECT_FAILED_ACTION_STATE = "failed";
    String CONNECT_NEW_CONNECTION_ACTION_STATE = "new_connection";
    String CONNECT_BREAK_ACTION_STATE = "break";

    // Message Action
    String NEW_MESSAGE_ACTION = "android.intent.action.NEW_MESSAGE_ACTION";

    String NEW_MESSAGE_COME_ACTION = "new_message";

    // Members Action
    String BROADCAST_MEMBERS_ACTION = "android.intent.action.BROADCAST_MEMBERS_ACTION";

    int CONNECT_BREAK_VIBERATE_TIME = 200;

    int MY_LOCATION_PERMISSIONS_REQUEST_CODE = 1;
}
