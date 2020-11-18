package com.example.es100dome.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.baseapp.AppManager;
import com.es100.baserx.RxBus;
import com.es100.baserx.RxManager;
import com.example.es100dome.CallActivity;
import com.example.es100dome.fragment.AudioCallDailog;
import com.example.es100dome.fragment.AudioCallWaitDialog;

import com.example.es100dome.fragment.CallInDialog;
import com.example.es100dome.fragment.RemoteCallInDialog;
import com.ifreecomm.debug.MLog;

import rx.functions.Action1;

/**
 * @Description : 主界面消息接收控制辅助类
 * @Author : BruceChen
 * @Date : 2018/1/18 9:08
 */

public final class MainActivityMessageControl {
    public static final String TAG = "MainActivityMessageControl";
    private Context mContext;
    private RxManager mRxManager;
    private Activity activity ;
    /**
     * 来电接听对话框
     */
    private CallInDialog callInDialog;

    /**
     * 远端来电接听对话框
     */
    private RemoteCallInDialog remoteCallInDialog;

    /**
     *主动呼叫等待对话框
     */
    private AudioCallWaitDialog audioCallWaitDialog;

    /**
     * 语音通话界面
     */
    private AudioCallDailog audioCallDailog;
    public MainActivityMessageControl(Context mContext, RxManager mRxManager) {
        this.mContext = mContext;
        this.mRxManager = mRxManager;
    }

    public void addMessage() {

        mRxManager.on(AppConstant.RxAction.IS_SHOW_AUDIO_CALL_DIALOG, (Action1<Boolean>) isShow -> {
            activity = AppManager.getAppManager().currentActivity();
            MLog.e(TAG,"收到提示消息语音通话界面"+isShow);
            if(isShow){
                dismissAudioCallDialog();
                audioCallDailog = new AudioCallDailog(activity);
                audioCallDailog.show();
                audioCallDailog.setIp(MyState.getInstance().callIp, MyState.getInstance().showName);
            }else{
                dismissAudioCallDialog();
            }
        });
        mRxManager.on(AppConstant.RxAction.IS_SHOW_CALL_WAIT_DIALOG, (Action1<Boolean>) isShow -> {
            activity = AppManager.getAppManager().currentActivity();
            MLog.e(TAG,"呼叫等待界面"+isShow);
            if(isShow){
                dismissCallWaitDialog();
                audioCallWaitDialog = new AudioCallWaitDialog(activity, MyState.getInstance().callText, MyState.getInstance().showName);
                audioCallWaitDialog.show();
//                audioCallWaitDialog.setIp(MyState.getInstance().callText,MyState.getInstance().showName);
            }else{
                dismissCallWaitDialog();
            }
        });
        mRxManager.on(AppConstant.RxAction.IS_SHOW_CALL_IN_DIALOG, (Action1<Boolean>) isShow -> {
            MLog.e(TAG,"收到呼入界面"+isShow);
            activity = AppManager.getAppManager().currentActivity();
            if(isShow) {
                dismissCallInDialog();
                callInDialog = new CallInDialog(activity, MyState.getInstance().showName, MyState.getInstance()
                        .callId);

                callInDialog.show();
            } else {
                dismissCallInDialog();
            }
        });

        mRxManager.on(AppConstant.RxAction.IS_SHOW_REMOTE_CALL_IN_DIALOG, (Action1<Boolean>) isShow -> {

            activity = AppManager.getAppManager().currentActivity();
            MLog.e(TAG,"收到远端呼入界面"+isShow);
            if(isShow) {
                dismissRemoteCallInDialog();
                remoteCallInDialog = new RemoteCallInDialog(activity, MyState
                        .getInstance().callId, MyState.getInstance().callText, MyState.getInstance().showName);
                remoteCallInDialog.show();
            } else {
                dismissRemoteCallInDialog();
            }
        });

        mRxManager.on(AppConstant.RxAction.JUMP_TO_VIDEO_CALL_ACTIVITY, (Action1<Boolean>) isShow -> {
            MLog.e(TAG,"收到视通界面"+isShow);
            AppManager.getAppManager().currentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CallActivity.class);
                    mContext.startActivity(intent);
                    dismissDialog();
                }
            });

        });

    }


    private void dismissCallInDialog() {
        if(callInDialog != null) {
            if(callInDialog.isShowing()) {
                callInDialog.dismiss();
            }
            callInDialog = null;
        }
    }

    private void dismissRemoteCallInDialog() {
        if(remoteCallInDialog != null) {
            if(remoteCallInDialog.isShowing()) {
                remoteCallInDialog.dismiss();
            }
            remoteCallInDialog = null;
        }
    }
    private void dismissCallWaitDialog() {
        if(audioCallWaitDialog != null) {
            if(audioCallWaitDialog.isShowing()) {
                audioCallWaitDialog.dismiss();
            }
            audioCallWaitDialog = null;
        }
    }
    private void dismissAudioCallDialog() {
        if(audioCallDailog != null) {
            if(audioCallDailog.isShowing()) {
                audioCallDailog.dismiss();
            }
            audioCallDailog = null;
        }
    }
    public void onDestroy() {
        mContext = null;
        dismissCallInDialog();
        dismissRemoteCallInDialog();
    }
    private void dismissDialog() {
        RxBus.getInstance()
                .post(AppConstant.RxAction.IS_SHOW_CALL_WAIT_DIALOG, false);
        RxBus.getInstance()
                .post(AppConstant.RxAction.IS_SHOW_CALL_IN_DIALOG, false);
        RxBus.getInstance()
                .post(AppConstant.RxAction.IS_SHOW_REMOTE_CALL_IN_DIALOG, false);
    }
}
