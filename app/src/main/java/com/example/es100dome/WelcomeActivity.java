package com.example.es100dome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.es100.util.ScreenParam;
import com.ifreecomm.debug.MLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 程序启动欢迎界面 <br>
 * @author Administrator
 */
@RuntimePermissions
public class WelcomeActivity extends AppCompatActivity {
    public static final String TAG = "WelcomeActivity";
    private String userName="";
    private String passWord="";
    private String url="";
    private String port="";

    @Override
    protected void onStart() {
        super.onStart();
        userName = getIntent().getStringExtra("userName");
        passWord = getIntent().getStringExtra("passWord");
        url = getIntent().getStringExtra("url");
        port = getIntent().getStringExtra("port");
        MLog.e(TAG,"onStart---userName"+userName+"---passWord"+passWord+"---url"+url+"---port"+port);
    }
    public final static int REQUEST_READ_PHONE_STATE = 1;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("WWEEE", ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)+"");
        setContentView(R.layout.welcome);
//        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
//            finish();
//            return;
//        }

        ScreenParam.init(WelcomeActivity.this);
        Single.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> WelcomeActivityPermissionsDispatcher.redirectToWithCheck(this));
    }


    @NeedsPermission({Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
            })
     void redirectTo() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.activity_anim_out);
        finish();
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
        for (int grantResult : grantResults) {
            if (grantResult != 0) {
                finish();
            }
        }
    }
}
