package com.example.bluehoclibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluehoclibrary.model.BTMember;

import java.util.ArrayList;

/**
 * Created by james on 3/2/17.
 */

public abstract class MemberBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        ArrayList<BTMember> members = bundle.getParcelableArrayList("members");

        onGetMembers(members);
    }

    public abstract void onGetMembers(ArrayList<BTMember> members);
}
