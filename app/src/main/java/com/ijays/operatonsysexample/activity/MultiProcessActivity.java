package com.ijays.operatonsysexample.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.ijays.operatonsysexample.AppConstants;
import com.ijays.operatonsysexample.IPassDataAidl;
import com.ijays.operatonsysexample.service.AIDLService;
import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.model.PassDataModel;
import com.ijays.operatonsysexample.service.MessengerService;
import com.ijays.operatonsysexample.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import butterknife.Bind;


/**
 * Created by ijaysdev on 16/5/13.
 */
public class MultiProcessActivity extends BaseActivity {
    @Bind(R.id.process_name)
    TextView mProcessName;
    @Bind(R.id.pass_data)
    TextView mPassData;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    //记录当前进入的跳转类型
    private int mCurrentType;
    private String mContent;
    private PrintWriter mPrintWriter;
    private Messenger mService;
    private IPassDataAidl mPassDataAidl;
    /**
     * Messenger的service绑定
     */
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
    private ServiceConnection mAidlConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPassDataAidl = IPassDataAidl.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private Messenger mGetMsgMessenger = new Messenger(new MessengerHandler());

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1234:
                    mPassData.setText(mContent);
                    break;
                case 0x02:
                    try {
                        mPassData.setText(mPassDataAidl.passData(mContent));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_muliti_process;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        mToolbar.setTitle("MultiProcess");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDefaultDisplayHomeAsUpEnabled(true);
        }

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
        mContent = intent.getStringExtra(AppConstants.PASS_DATA);

        switch (mCurrentType) {
            case AppConstants.INTENT_METHOD:
                String passData = intent.getStringExtra(AppConstants.PASS_DATA);
                mPassData.setText(passData);

                break;
            case AppConstants.SHARED_FILE_METHOD:
                readDataWithSharingFile();

                break;
            case AppConstants.MESSENGER_METHOD:
                Intent newIntent = new Intent(this, MessengerService.class);
                bindService(newIntent, mConnection, Context.BIND_AUTO_CREATE);
                break;
            case AppConstants.CONTENT_PROVIDER_METHOD:
                //从content provider中获取数据
                String content = null;
                Uri uri = Uri.parse("content://com.ijays.operatonsysexample.provider/pass");
                Cursor cursor = getContentResolver().query(uri, new String[]{"_id", "content"}, null, null, null);
                while (cursor.moveToNext()) {
                    content = cursor.getString(1);
                }
                mPassData.setText(content);
                cursor.close();
                break;
            case AppConstants.SOCKET_METHOD:
                new Thread() {
                    @Override
                    public void run() {

                        connectTCPServer();
                    }
                }.start();
                break;
            case AppConstants.AIDL_METHOD:
                Intent bindAIDL = new Intent(this, AIDLService.class);
                bindService(bindAIDL, mAidlConnection, Context.BIND_AUTO_CREATE);
                mHandler.sendEmptyMessageDelayed(0x02, 500);
                break;
            default:
                break;
        }
    }

    /**
     * 连接到tcp socket服务器并接收来自服务器的消息
     */
    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 9000);
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException e) {
//                SystemClock.sleep(1000);
                e.printStackTrace();
            }
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!MultiProcessActivity.this.isFinishing()) {
                String msg = br.readLine();
                if (msg != null) {
                    mHandler.obtainMessage(1234, msg).sendToTarget();
                }
            }
            if (socket != null) {
                socket.close();
            }
            if (mPrintWriter != null) {
                mPrintWriter.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mCurrentType == AppConstants.MESSENGER_METHOD) {
            unbindService(mConnection);
        }else if (mCurrentType==1234){
            unbindService(mAidlConnection);
        }
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
