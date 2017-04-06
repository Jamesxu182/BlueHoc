package com.example.bluehoclibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluehoclibrary.configure.BTConfigure;

/**
 * Created by james on 3/1/17.
 */

public abstract class ConnectionBroadcastReceiver extends BroadcastReceiver implements BTConfigure {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action != null) {
            if(CONNECT_ACTION.equals(CONNECT_ACTION)) {
                Bundle bundle = intent.getExtras();

                switch(bundle.getString("state")) {
                    case CONNECT_SUCCESSFULLY_ACTON_STATE:
                        onConnectSuccessfully(bundle);
                        break;
                    case CONNECT_NEW_CONNECTION_ACTION_STATE:
                        onNewConnection(bundle);
                        break;
                    case CONNECT_FAILED_ACTION_STATE:
                        onConnectFailed(bundle);
                        break;
                    case CONNECT_BREAK_ACTION_STATE:
                        onConnectionBreak(bundle);
                        break;
                    default:
                        onDefault(bundle);
                        break;
                }
            }
        }
    }

    public void onConnectSuccessfully(Bundle bundle) {
        // Do nothing
    }

    public void onConnectFailed(Bundle bundle) {
        // Do nothing
    }

    public void onConnectionBreak(Bundle bundle) {
        // Do nothing
    }

    public void onNewConnection(Bundle bundle) {
        // Do nothing
    }

    public void onDefault(Bundle bundle) {
        // Do nothing
    }
}
