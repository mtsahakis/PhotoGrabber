package com.mtsahakis.photograbber.ui;


public interface PhotoPresenter {

    void init();

    void takePhoto();

    void savePhoto();

    void onCameraPermissionsResult();

}
