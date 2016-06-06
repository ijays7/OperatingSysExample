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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.utils.IOHelper;

import butterknife.Bind;

public class MiscActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.io_scheduler_value)
    TextView mIOScheduler;
    @Bind(R.id.support_bt)
    Button mSupportIOButton;
    @Bind(R.id.io_tip)
    TextView mIOTips;

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
        setOnClick();

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
                    break;
                default:
                    break;
            }
        }
    };

    private void showAllDefault() {
        String curIOScheduler = IOHelper.getCurScheduler();
        mIOScheduler.setText(curIOScheduler);
        String[] cpuAvailableFreq = IOHelper.getAvailableScheduler();
        setWord(cpuAvailableFreq);

        if (curIOScheduler.equals("cfq")) {
            mIOTips.setText(getString(R.string.cfq_tip));
        } else if (curIOScheduler.equals("noop")) {
            mIOTips.setText(getString(R.string.noop_tip));
        } else if (curIOScheduler.equals("deadline")) {
            mIOTips.setText(getString(R.string.deadline_tip));
        } else if (curIOScheduler.equals("row")) {
            mIOTips.setText(getString(R.string.row_tip));
        }
    }

    private void setWord(String[] cpuAvailableFreq) {
        String content = "";
        for (int i = 0; i < cpuAvailableFreq.length; i++) {
            content += cpuAvailableFreq[i] + " ";
        }
//        mSupportedIO.setText(content);
    }

    private void setOnClick() {
        mIOScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAvailableSchedulerDialog();
            }
        });
        mSupportIOButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectAvailableSchedulerDialog();
            }
        });
    }

    private void showSelectAvailableSchedulerDialog() {
        AlertDialog.Builder availableSchedulerDialog = new AlertDialog.Builder(this);
        availableSchedulerDialog.setTitle("Supported Scheduler");
        availableSchedulerDialog.setNegativeButton("cancel", null);


        final String[] cpuAvailableFreq = IOHelper.getAvailableScheduler();

        availableSchedulerDialog.setItems(cpuAvailableFreq, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog ad = new AlertDialog.Builder(MiscActivity.this).setMessage(

                        "你选择的是：" + cpuAvailableFreq[which]).show();
            }
        });
        availableSchedulerDialog.create().show();
    }


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
