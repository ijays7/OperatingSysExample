package com.ijays.operatonsysexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ijays.operatonsysexample.IPassDataAidl;

/**
 * Created by ijaysdev on 16/5/20.
 */
public class AIDLService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    private IBinder iBinder = new IPassDataAidl.Stub() {
        @Override
        public String passData(String content) throws RemoteException {
            return content;
        }
    };
}
