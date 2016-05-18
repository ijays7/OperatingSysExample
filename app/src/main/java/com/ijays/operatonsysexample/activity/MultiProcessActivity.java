package com.ijays.operatonsysexample.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.widget.TextView;

import com.ijays.operatonsysexample.AppConstants;
import com.ijays.operatonsysexample.service.Messagerservice;
import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.model.PassDataModel;
import com.ijays.operatonsysexample.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import butterknife.Bind;


/**
 * Created by ijaysdev on 16/5/13.
 */
public class MultiProcessActivity extends BaseActivity {
    @Bind(R.id.process_name)
    TextView mProcessName;
    @Bind(R.id.pass_data)
    TextView mPassData;

    //记录当前进入的跳转类型
    private int mCurrentType;
    private String mContent;
    private Messenger mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message msg = Message.obtain(null, AppConstants.MSG_FROM_CLIENT);
            msg.replyTo = mGetMsgMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private Messenger mGetMsgMessenger = new Messenger(new MessengerHandler());

    @Override
    protected int getContentViewId() {
        return R.layout.activity_muliti_process;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        Intent intent = getIntent();
        mProcessName.setText(Utils.getProcessName(getApplicationContext(), Process.myPid()));
        setShownData(intent);
    }

    /**
     * 根据传入的type显示数据
     *
     * @param intent
     */
    private void setShownData(Intent intent) {
        mCurrentType = intent.getIntExtra(AppConstants.JUMP_TYPE, 1);

        switch (mCurrentType) {
            case AppConstants.INTENT_METHOD:
                String passData = intent.getStringExtra(AppConstants.PASS_DATA);
                mPassData.setText(passData);

                break;
            case AppConstants.SHARED_FILE_METHOD:
                readDataWithSharingFile();

                break;
            case AppConstants.MESSENGER_METHOD:
                mContent=intent.getStringExtra(AppConstants.PASS_DATA);
                Intent newIntent = new Intent(this, Messagerservice.class);
                bindService(newIntent, mConnection, Context.BIND_AUTO_CREATE);
                break;
        }
    }

    /**
     * 从共享文件中读取信息
     */
    private void readDataWithSharingFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cacheFile = new File(AppConstants.CACHE_FILE);
                if (cacheFile.exists()) {
                    PassDataModel model = null;
                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(new FileInputStream(cacheFile));
                        model = (PassDataModel) objectInputStream.readObject();
                        mPassData.setText(model.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mCurrentType == 123)
            unbindService(mConnection);
        super.onDestroy();
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.MSG_FROM_SERVER:
                    Bundle bundle = msg.getData();
                    mPassData.setText(mContent);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
