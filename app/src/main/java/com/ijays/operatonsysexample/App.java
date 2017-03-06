package com.ijays.operatonsysexample;

import android.app.Application;
import android.os.Process;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ijays.operatonsysexample.utils.Utils;

/**
 * Created by ijaysdev on 16/5/18.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String processName = Utils.getProcessName(getApplicationContext(), Process.myPid());
        if (processName.equals("com.ijays,operaonsysexample")) {

            Fresco.initialize(getApplicationContext());
        }
    }
}
