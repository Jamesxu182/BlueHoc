package com.example.bluehoclibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluehoclibrary.configure.BTConfigure;

/**
 * Created by james on 3/2/17.
 */

public abstract class MessageBroadcastReceiver extends BroadcastReceiver implements BTConfigure {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action != null) {
            if(NEW_MESSAGE_ACTION.equals(action)) {
                Bundle bundle = intent.getExtras();

                switch(bundle.getString("state")) {
                    case NEW_MESSAGE_COME_ACTION:
                        onNewMessageCome(bundle);
                        break;
                    default:
                        onDefault(bundle);
                        break;
                }
            }
        }
    }

    public void onDefault(Bundle bundle) {
        //Do nothing
    }

    public void onNewMessageCome(Bundle bundle) {
        //Do nothing
    }
}
