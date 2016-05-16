package com.ijays.operatonsysexample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by ijaysdev on 16/5/13.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        init(savedInstanceState);
    }

    /**
     * 初始化操作
     */
    protected void init(Bundle savedInstanceState) {
    }

    /**
     * 获取布局id
     * @return
     */
    protected int getContentViewId() {
        return 0;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
