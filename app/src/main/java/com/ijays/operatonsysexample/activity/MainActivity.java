package com.ijays.operatonsysexample.activity;


import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ijays.operatonsysexample.AppConstants;
import com.ijays.operatonsysexample.provider.PassDataDbHelper;
import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.model.PassDataModel;
import com.ijays.operatonsysexample.service.TCPServerService;
import com.ijays.operatonsysexample.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import butterknife.Bind;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    @Bind(R.id.multi_process)
    Button mMultiProcess;
    @Bind(R.id.share_file)
    Button mShareFile;
    @Bind(R.id.content_provider)
    Button mContentProviderBt;
    @Bind(R.id.messagener)
    Button mMessenger;
    @Bind(R.id.socketIPC)
    Button mSocketIpc;
    @Bind(R.id.process_name)
    TextView mProcessName;
    @Bind(R.id.pass_data)
    EditText mPassData;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.test)
    Button mTest;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private boolean isStartService;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mProcessName.setText(Utils.getProcessName(getApplicationContext(), Process.myPid()));
        mMultiProcess.setOnClickListener(this);
        mShareFile.setOnClickListener(this);
        mContentProviderBt.setOnClickListener(this);
        mMessenger.setOnClickListener(this);
        mSocketIpc.setOnClickListener(this);
        mTest.setOnClickListener(this);
        mFab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.multi_process:
                if (canPassData(view)) {
                    jumpToMultiProcess(AppConstants.INTENT_METHOD);
                }
                break;
            case R.id.share_file:
                if (canPassData(view)) {
                    passDataBySharingFile();
                }
                break;
            case R.id.test:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 0x9090);
                break;
            case R.id.content_provider:
                ContentValues values = new ContentValues();
                values.put("content", mPassData.getText().toString().trim());
                SQLiteDatabase db = new PassDataDbHelper(this).getWritableDatabase();
                db.insert(PassDataDbHelper.PASS_TABLE_NAME, null, values);
                jumpToMultiProcess(AppConstants.CONTENT_PROVIDER_METHOD);

                break;
            case R.id.messagener:
                if (canPassData(view)) {
//                    Intent startServiceIntent=new Intent(this,Messagerservice.class);
//                    startService(startServiceIntent);
                    jumpToMultiProcess(AppConstants.MESSENGER_METHOD);
                }
                break;
            case R.id.socketIPC:
                if (canPassData(view)) {
                    Intent service = new Intent(this, TCPServerService.class);
//                    startService(service);
                    bindService(service, connection, Context.BIND_AUTO_CREATE);
                    isStartService = true;
                    jumpToMultiProcess(123);
                }
                break;
            default:
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TCPServerService service = ((TCPServerService.MyBinder) iBinder).getService();
            if (TextUtils.isEmpty(mPassData.getText().toString()))
                service.setContent(mPassData.getText().toString().trim());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    /**
     * 跳转至MultiProcess页面
     */
    private void jumpToMultiProcess(int jumpType) {
        Intent intent = new Intent(MainActivity.this, MultiProcessActivity.class);
        intent.putExtra(AppConstants.PASS_DATA, mPassData.getText().toString().trim());
        intent.putExtra(AppConstants.JUMP_TYPE, jumpType);
        startActivity(intent);
    }

    private void passDataBySharingFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PassDataModel model = new PassDataModel(mPassData.getText().toString().trim());
                File dir = new File(AppConstants.FILE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File cacheFile = new File(AppConstants.CACHE_FILE);
                ObjectOutputStream objectOutputStream = null;
                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cacheFile));
                    objectOutputStream.writeObject(model);
                    Intent intent = new Intent(MainActivity.this, MultiProcessActivity.class);
                    intent.putExtra(AppConstants.JUMP_TYPE, AppConstants.SHARED_FILE_METHOD);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 是否能够传递数据
     *
     * @param view
     */
    private boolean canPassData(View view) {
        String content = mPassData.getText().toString().trim();
        if (content != null && content.length() > 0) {
            return true;

        } else {
            Snackbar.make(view, "请输入需要传输的数据", Snackbar.LENGTH_SHORT).setAction("Action", null)
                    .show();
            return false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this,PicExploreActivity.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (isStartService) {
            unbindService(connection);
        }
        super.onDestroy();
    }
}
