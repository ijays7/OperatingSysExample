package com.ijays.operatonsysexample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.utils.Utils;


import java.io.File;

import butterknife.Bind;

public class TakePhotoActivity extends BaseActivity {
    private static final int REQUEST_CAMERA_2 = 0x02;
    private static final int REQUEST_CROP_2 = 0x03;

    @Bind(R.id.bt_start_camera)
    Button mStartCamera;
    @Bind(R.id.iv_show)
    ImageView mImageView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private Uri mPhotoUri;
    //图片路径
    private String mFilePath;
    private boolean isTaked;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_take_photo;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        mToolbar.setTitle("拍照");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilePath = Utils.generatePth();
                startCamera2(view);
            }
        });
    }

    public void startCamera2(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoUri = Uri.fromFile(new File(mFilePath));
        //更改拍照路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, REQUEST_CAMERA_2);
    }

    /**
     * 返回回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA_2:
                if (resultCode == RESULT_OK) {
                    setImageToView();
                    isTaked = true;
                }
                break;
            case REQUEST_CROP_2:
                if (resultCode == RESULT_OK) {
                }
                break;
            default:
                break;
        }

    }

    /**
     * 显示拍摄的图片
     */
    private void setImageToView() {
        Bitmap bitmap = Utils.decodeFileBitmap(mFilePath, 500, 500);
        mImageView.setImageBitmap(bitmap);
    }

    private void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mPhotoUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, REQUEST_CROP_2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
