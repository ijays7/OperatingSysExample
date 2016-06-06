package com.ijays.operatonsysexample.activity;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
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
    @Bind(R.id.messenger)
    Button mMessenger;
    @Bind(R.id.bt_aidl)
    Button mAidlIPC;
    @Bind(R.id.socketIPC)
    Button mSocketIpc;
    @Bind(R.id.process_name)
    TextView mProcessName;
    @Bind(R.id.pass_data)
    EditText mPassData;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab_menu_red)
    FloatingActionMenu mFabMenu;
    @Bind(R.id.fab1)
    com.github.clans.fab.FloatingActionButton mFab1;
    @Bind(R.id.fab2)
    com.github.clans.fab.FloatingActionButton mFab2;
    @Bind(R.id.fab3)
    com.github.clans.fab.FloatingActionButton mFab3;
    @Bind(R.id.fab4)
    com.github.clans.fab.FloatingActionButton mFab4;
    @Bind(R.id.fab5)
    com.github.clans.fab.FloatingActionButton mFab5;

    private boolean mIsStartService;
    private boolean mIsOpenMenu;//是否打开了菜单

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
        mAidlIPC.setOnClickListener(this);
        mSocketIpc.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);
        mFab3.setOnClickListener(this);
        mFab4.setOnClickListener(this);
        mFab5.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFabMenu.setClosedOnTouchOutside(true);
        mFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    mIsOpenMenu = true;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab1:
                mFabMenu.close(false);
                startActivity(new Intent(MainActivity.this, PicExploreActivity.class));
                break;
            case R.id.fab2:
                mFabMenu.close(false);
                startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                break;
            case R.id.fab3:
                mFabMenu.close(false);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(intent, 0x9090);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.fab4:
                mFabMenu.close(false);
                startActivity(new Intent(MainActivity.this, MiscActivity.class));
                break;
            case R.id.fab5:
                mFabMenu.close(false);
                startActivity(new Intent(MainActivity.this, SafetyActivity.class));
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
            case R.id.content_provider:
                if (canPassData(view)) {
                    ContentValues values = new ContentValues();
                    values.put("content", mPassData.getText().toString().trim());
                    SQLiteDatabase db = new PassDataDbHelper(this).getWritableDatabase();
                    db.insert(PassDataDbHelper.PASS_TABLE_NAME, null, values);
                    jumpToMultiProcess(AppConstants.CONTENT_PROVIDER_METHOD);
                }
                break;
            case R.id.messenger:
                if (canPassData(view)) {
//                    Intent startServiceIntent=new Intent(this,MessengerService.class);
//                    startService(startServiceIntent);
                    jumpToMultiProcess(AppConstants.MESSENGER_METHOD);
                }
                break;
            case R.id.bt_aidl:
                if (canPassData(view)) {
                    jumpToMultiProcess(AppConstants.AIDL_METHOD);
                }

                break;
            case R.id.socketIPC:
                if (canPassData(view)) {
                    Intent service = new Intent(this, TCPServerService.class);
//                    startService(service);
                    bindService(service, connection, Context.BIND_AUTO_CREATE);
                    mIsStartService = true;
                    jumpToMultiProcess(AppConstants.SOCKET_METHOD);
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
//            Snackbar.make(view, "请输入需要传输的数据", Snackbar.LENGTH_SHORT).setAction("Action", null)
//                    .show();
            Toast.makeText(MainActivity.this, "请输入需要传输的数据", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(MainActivity.this, PicExploreActivity.class));

        } else if (id == R.id.nav_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(intent, 0x9090);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (id == R.id.nav_safety) {
            startActivity(new Intent(MainActivity.this, SafetyActivity.class));
        } else if (id == R.id.nav_io) {
            startActivity(new Intent(MainActivity.this, MiscActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 0x9090:
////                if (resultCode == RESULT_OK) {//是否选择，没选择就不会继续
////                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
////                    Log.e("SONGJIE", uri.toString());
////                    String[] proj = {MediaStore.Images.Media.DATA};
////                    ContentResolver resolver = getContentResolver();
////                    String fileType = resolver.getType(uri);
////                    Log.e("SONGJIE", fileType);
////                    Cursor cursor = resolver.query(uri, null, null, null, null);
////                    if (cursor.moveToFirst()) {
////                        String path = cursor.getString(cursor.getColumnIndex("_data"));
////                        Log.e("SONGJIE", path + "");
////                    }
////                    Cursor actualimagecursor = getContentResolver().query(uri, proj, null, null, null);
////                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
////                    actualimagecursor.moveToFirst();
////                    String img_path = actualimagecursor.getString(actual_image_column_index);
////                    //File file = new File(img_path);
////                    Toast.makeText(MainActivity.this, img_path, Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }

        super.onActivityResult(requestCode, resultCode, data);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mIsOpenMenu) {
            mFabMenu.close(false);
            mIsOpenMenu = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mIsStartService) {
            unbindService(connection);
        }
        super.onDestroy();
    }
}
