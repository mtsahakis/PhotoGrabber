package com.mtsahakis.photograbber.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.mtsahakis.photograbber.R;
import com.mtsahakis.photograbber.data.PermissionManager;
import com.mtsahakis.photograbber.data.Repository;
import com.mtsahakis.photograbber.databinding.ActivityPhotoBinding;

import java.io.File;

public class PhotoActivity extends AppCompatActivity implements PhotoView {

    private static final int REQUEST_TAKE_PHOTO = 101;

    private ActivityPhotoBinding mBinding;
    private PhotoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo);

        PermissionManager permissionManager = new PermissionManager(this);
        Repository repository = new Repository(this);
        mPresenter = new PhotoPresenterImpl(this, repository, permissionManager);
        mBinding.setPresenter(mPresenter);

        mBinding.image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBinding.image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mBinding.image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mPresenter.init();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mPresenter.savePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mPresenter.onCameraPermissionsResult();
    }

    @Override
    public void setPhoto(File photoFile) {

        getWindow().getDecorView().getWidth();
        getWindow().getDecorView().getHeight();

        // Get the dimensions of the View
        int targetW = mBinding.image.getWidth();
        int targetH = mBinding.image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        mBinding.image.setImageBitmap(bitmap);
    }

    @Override
    public void showImageNotAvailable() {
        mBinding.noImages.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideImageNotAvailable() {
        mBinding.noImages.setVisibility(View.GONE);
    }

    @Override
    public void startCamera(File photoFile) {
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.mtsahakis.photograbber.fileprovider",
                photoFile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }
}
