package com.mtsahakis.photograbber.ui;


import java.io.File;

public interface PhotoView {

    void setPhoto(File photoFile);

    void showImageNotAvailable();

    void hideImageNotAvailable();

    void startCamera(File photoFile);

}
