package com.mtsahakis.photograbber.data;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.lang.ref.WeakReference;

public class PermissionManager {

    private static final String LOG_TAG = "PermissionManager";
    private static final int REQUEST_CAMERA_PERMISSION = 102;

    private WeakReference<Context> mContextReference;

    public PermissionManager(@NonNull Context context) {
        mContextReference = new WeakReference<>(context);
    }

    /*************************************** Camera ***********************************************/
    public static final String PERMS_CAMERA = android.Manifest.permission.CAMERA;

    public boolean hasPermissionForCamera() {
        return hasPermission(PERMS_CAMERA);
    }

    public void requestPermissionsForCamera() {
        if (shouldAskForCameraPermission()) {
            markedPermissionAsAskedForCamera();
            requestPermissions(new String[]{PERMS_CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void markedPermissionAsAskedForCamera() {
        markedPermissionAsAsked(PERMS_CAMERA);
    }

    private boolean shouldAskForCameraPermission() {
        return shouldAskForPermission(PERMS_CAMERA);
    }

    /*************************************** Utility methods **************************************/
    private boolean useRunTimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean hasPermission(String permission) {
        if (!useRunTimePermissions()) {
            return true;
        }

        Context context = mContextReference.get();
        if (context != null) {
            try {
                PackageInfo packageInfo = context.
                        getPackageManager().
                        getPackageInfo(context.getPackageName(), 0);
                int targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion;
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    return context.checkSelfPermission(permission)
                            == PackageManager.PERMISSION_GRANTED;
                } else {
                    return PermissionChecker.checkSelfPermission(context, permission)
                            == PermissionChecker.PERMISSION_GRANTED;
                }

            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return false;
            }
        }

        return true;
    }

    private void requestPermissions(String[] permission, int requestCode) {
        if (useRunTimePermissions()) {
            Context context = mContextReference.get();
            if (context != null) {
                ((Activity) context).requestPermissions(permission, requestCode);
            }
        }
    }

    private boolean shouldShowRational(String permission) {
        if (useRunTimePermissions()) {
            Context context = mContextReference.get();
            if (context != null) {
                return ((Activity) context).shouldShowRequestPermissionRationale(permission);
            }
        }
        return false;
    }

    private boolean shouldAskForPermission(String permission) {
        if (useRunTimePermissions()) {
            return !hasPermission(permission) &&
                    (!hasAskedForPermission(permission) || shouldShowRational(permission));
        }
        return false;
    }

    private boolean hasAskedForPermission(String permission) {
        Context context = mContextReference.get();
        if (context != null) {
            return PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getBoolean(permission, false);
        }
        return false;
    }

    private void markedPermissionAsAsked(String permission) {
        Context context = mContextReference.get();
        if (context != null) {
            PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(permission, true)
                    .apply();
        }
    }
}
