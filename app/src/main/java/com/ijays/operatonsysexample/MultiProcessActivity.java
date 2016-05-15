package com.ijays.operatonsysexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

import com.ijays.operatonsysexample.utils.Utils;

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
        String passdata = intent.getStringExtra("pass_data");
        mPassData.setText(passdata);
        mProcessName.setText(Utils.getProcessName(getApplicationContext(), Process.myPid()));
    }
}
