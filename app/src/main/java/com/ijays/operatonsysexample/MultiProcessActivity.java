package com.ijays.operatonsysexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

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

    @Override
    protected int getContentViewId() {
        return R.layout.activity_muliti_process;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        Intent intent = getIntent();
        setShownData(intent);
        mProcessName.setText(Utils.getProcessName(getApplicationContext(), Process.myPid()));

    }

    /**
     * 根据传入的type显示数据
     * @param intent
     */
    private void setShownData(Intent intent) {
        int jumpType = intent.getIntExtra(AppConstants.JUMP_TYPE, 1);
        switch (jumpType){
            case AppConstants.INTENT_METHOD:
                String passData = intent.getStringExtra("pass_data");
                mPassData.setText(passData);

                break;
            case AppConstants.SHARED_FILE_METHOD:
                readDataWithSharingFile();

                break;
        }
    }

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
}
