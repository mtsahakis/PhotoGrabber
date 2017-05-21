package com.mtsahakis.photograbber.data;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Repository {

    private WeakReference<Context> mContextReference;

    public Repository(@NonNull Context context) {
        mContextReference = new WeakReference<>(context);
    }

    @Nullable
    public File createPhotoFile() throws IOException {
        Context context = mContextReference.get();
        if (context != null) {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
            String imageFileName = "IMAGE" + "_" + timeStamp;
            File storageDir = context.getExternalFilesDir(null);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return image;
        }

        return null;
    }

    @Nullable
    public File getPhotoFile() throws IOException {
        Context context = mContextReference.get();
        if (context != null) {
            String imageFileName = "IMAGE.jpg";
            File storageDir = context.getExternalFilesDir(null);
            return new File(storageDir, imageFileName);
        }

        return null;
    }

    @Nullable
    public void savePhotoFile(File src) throws IOException {
        Context context = mContextReference.get();
        if (context != null) {
            copy(src, getPhotoFile());
        }
    }

    private void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
