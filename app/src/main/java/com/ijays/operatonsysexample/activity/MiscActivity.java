package com.ijays.operatonsysexample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.utils.IOHelper;

import butterknife.Bind;

public class MiscActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_misc;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mToolbar.setTitle("CurrentI/O");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        showAllDefault();
        setOnListen();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        IOSchedulerStatus = IOHelper.getIOSchedulerStatusContent();
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message);
                        Thread.sleep(1000, 0);
                    } catch (Exception ep) {
                        ep.printStackTrace();
                    }

                }
            }
        }).start();


    }

    public static final int UPDATE_TEXT = 1;
    public static String IOSchedulerStatus = "...";
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    TextView showText = (TextView) findViewById(R.id.io_scheduler_status_content);
                    showText.setText(IOSchedulerStatus);
                    showAllDefault();
                    break;
                default:
                    break;
            }
        }
    };

    private void showAllDefault() {
        String curIOScheduler = IOHelper.getCurScheduler();
        TextView tvIOScheduler = (TextView) findViewById(R.id.io_scheduler_value);
        tvIOScheduler.setText(curIOScheduler);

        String curReadAhead = IOHelper.getCurReadAhead();
        TextView tvCurReadAhead = (TextView) findViewById(R.id.read_ahead_value);
        tvCurReadAhead.setText(curReadAhead);
    }

    private void setOnListen() {
        View vAvailableScheduler = findViewById(R.id.io_scheduler_value);
        vAvailableScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAvailableSchedulerDialog();
            }
        });

        View vReadAhead = findViewById(R.id.read_ahead_value);
        vReadAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetReadAheadDialog();
            }
        });

    }

    private void showSelectAvailableSchedulerDialog() {
        AlertDialog.Builder availableSchedulerDialog = new AlertDialog.Builder(this);
        availableSchedulerDialog.setTitle("Select Scheduler");
        availableSchedulerDialog.setNegativeButton("cancel", null);


        final String[] cpuAvailableFreq = IOHelper.getAvailableScheduler();

        availableSchedulerDialog.setItems(cpuAvailableFreq, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog ad = new AlertDialog.Builder(MiscActivity.this).setMessage(

                        "你选择的是：" + ": " + cpuAvailableFreq[which]).show();
                IOHelper.setCurScheduler(cpuAvailableFreq[which]);
            }
        });
        availableSchedulerDialog.create().show();
    }

    private void showSetReadAheadDialog() {
        final EditText mEditText = new EditText(this);
        AlertDialog.Builder readAheadDialog = new AlertDialog.Builder(this);
        readAheadDialog.setTitle("Read Ahead Cache Size");
        readAheadDialog.setView(mEditText);
        readAheadDialog.setNegativeButton("cancel", null);
        readAheadDialog.setPositiveButton("set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String setValue = mEditText.getText().toString();
                IOHelper.setCurReadAhead(setValue);
            }
        });

        readAheadDialog.create().show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_cpu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
