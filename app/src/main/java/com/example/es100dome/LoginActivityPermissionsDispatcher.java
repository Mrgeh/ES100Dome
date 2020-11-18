package com.example.es100dome;

import android.support.v4.app.ActivityCompat;

import permissions.dispatcher.PermissionUtils;

/**
 * Created by Administrator on 2018-11-2.
 */

public class LoginActivityPermissionsDispatcher {

    private static final int REQUEST_LOGINACTION = 1;

    private static final String[] PERMISSION_LOGINACTION = new String[] {"android.permission.READ_PHONE_STATE"};

    private LoginActivityPermissionsDispatcher() {
    }

    static void loginActionWithCheck(LoginActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_LOGINACTION)) {
            target.loginAction();
        } else {
            ActivityCompat.requestPermissions(target, PERMISSION_LOGINACTION, REQUEST_LOGINACTION);
        }
    }

    static void onRequestPermissionsResult(LoginActivity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOGINACTION:
                if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, PERMISSION_LOGINACTION)) {
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.loginAction();
                }
                break;
            default:
                break;
        }
    }
}
