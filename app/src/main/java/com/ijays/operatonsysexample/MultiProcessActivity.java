package com.ijays.operatonsysexample;

import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ijays.operatonsysexample.utils.Utils;




/**
 * Created by ijaysdev on 16/5/13.
 */
public class MultiProcessActivity extends BaseActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_muliti_process;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Log.e("Mutil","songjie");
        Log.e("MUTI", Utils.getProcessName(getApplicationContext(), Process.myPid()));
    }
}
