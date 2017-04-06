package com.example.bluehoclibrary.service.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by james on 2/23/17.
 */

public class BTBaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showTextOnToast(String content) {
        Toast.makeText(getBaseContext(), content, Toast.LENGTH_LONG).show();
    }
}
