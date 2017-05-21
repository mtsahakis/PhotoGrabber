package com.mtsahakis.photograbber.ui;


import android.support.annotation.NonNull;
import android.util.Log;

import com.mtsahakis.photograbber.data.PermissionManager;
import com.mtsahakis.photograbber.data.Repository;

import java.io.File;
import java.io.IOException;

public class PhotoPresenterImpl implements PhotoPresenter {

    private static final String LOG_TAG = "PhotoPresenter";

    private PhotoView mView;
    private Repository mRepository;
    private PermissionManager mPermissionsManager;
    private File mTakenPhoto;

    PhotoPresenterImpl(@NonNull PhotoView view,
                       @NonNull Repository repository,
                       @NonNull PermissionManager permissionsManager) {
        mView = view;
        mRepository = repository;
        mPermissionsManager = permissionsManager;
    }

    @Override
    public void init() {
        try {
            File photoFile = mRepository.getPhotoFile();
            if (photoFile != null && photoFile.exists()) {
                mView.hideImageNotAvailable();
                mView.setPhoto(photoFile);
            } else {
                mView.showImageNotAvailable();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void takePhoto() {
        if (mPermissionsManager.hasPermissionForCamera()) {
            startCamera();
        } else {
            mPermissionsManager.requestPermissionsForCamera();
        }
    }

    @Override
    public void savePhoto() {
        try {
            mRepository.savePhotoFile(mTakenPhoto);
            File photoFile = mRepository.getPhotoFile();
            if (photoFile != null && photoFile.exists()) {
                mView.hideImageNotAvailable();
                mView.setPhoto(photoFile);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onCameraPermissionsResult() {
        if (mPermissionsManager.hasPermissionForCamera()) {
            startCamera();
        }
    }

    private void startCamera() {
        try {
            mTakenPhoto = mRepository.createPhotoFile();
            mView.startCamera(mTakenPhoto);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
