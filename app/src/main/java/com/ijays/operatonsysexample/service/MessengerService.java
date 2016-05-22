package com.ijays.operatonsysexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ijays.operatonsysexample.AppConstants;

/**
 * Created by ijaysdev on 16/5/17.
 */
public class MessengerService extends Service {
    private String mContent;

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConstants.MSG_FROM_CLIENT:
                    Messenger messenger = msg.replyTo;
                    Message sendMessage = Message.obtain(null, AppConstants.MSG_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString("send", mContent);
                    sendMessage.setData(bundle);
                    try {
                        messenger.send(sendMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
