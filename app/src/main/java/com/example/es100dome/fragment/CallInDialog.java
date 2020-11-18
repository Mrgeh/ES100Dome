package com.example.es100dome.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.es100.MyState;

import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.es100.jni.MstApp;
import com.es100.service.MyMqttService;
import com.es100.util.StatusBarCompat;
import com.example.es100dome.R;
import com.ifreecomm.debug.MLog;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class CallInDialog extends Dialog {
    public final String TAG = "CallInDialog";
    Context mContext;
    private int callId;
    private MediaPlayer mMediaPlayer;
    private String E164;
    private MyState sta = MyState.getInstance();
    private Disposable timeOutDisposable;
    View view;
    public CallInDialog(Context context, String e164, int id) {
        super(context, R.style.dialogWindowAnim);
        mContext = context;
        callId = id;
        E164 = e164;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setWindow();
        view = LayoutInflater.from(mContext).inflate(R.layout.dailog_call_in, null);
        setContentView(view);
        TextView callTip = (TextView) findViewById(R.id.call_status_textview);
        TextView ipOrNameTextView = (TextView) findViewById(R.id.name_textview);
        ImageView ivIcon = (ImageView) findViewById(R.id.people_imageview);
        MLog.e(TAG, "name" + E164);
        ipOrNameTextView.setText(E164);
        ivIcon.setImageResource(R.drawable.people_default);
        callTip.setText("来电信息");
        Button btStopCall = (Button) findViewById(R.id.bt_stop);
        Button btAccept = (Button) findViewById(R.id.bt_accept);
        btStopCall.setOnClickListener((View arg0) -> {
            sta.StartCall = false;
            sta.StartDrop = false;
            sta.IsInConference = false;
            try {
                MyMqttService.unSubscribeTOPIC(sta.topic_ConfsitetreeInfo);
                MyMqttService.unSubscribeTOPIC(sta.topic_start);
                MyMqttService.unSubscribeTOPIC(sta.topic_end);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            MstApp.getInstance().uISendMsg.Drop(callId, false);
            dismiss();
        });
        btAccept.setOnClickListener((View arg0) -> {
            MLog.e(TAG, "AcceptCall callInDialog 呼叫类型" + sta.callType);
            stopSound();
            stopTimeOut();
            callTip.setText("正在连接");
//            btStopCall.setEnabled(false);
//            btAccept.setEnabled(false);
            sta.callIn = 1;
            sta.StartCall = true;
            MLog.e(TAG, "接受类型 " + sta.callType + "  " + sta.GHStype);
            if (sta.GHStype == 0) {
                MstApp.getInstance().uISendMsg.AcceptCall(sta.callId, 0);
                return;
            } else if (sta.GHStype == 1) {
                MstApp.getInstance().uISendMsg.AcceptCall(sta.callId, 1);
                return;
            }

            MstApp.getInstance().uISendMsg.AcceptCall(sta.callId, 1);
//            com.jaydenxiao.common.baserx.RxBus.getInstance().post(AppConstant.RxAction.HAND_ANSWER,true);
        });
        playSound();
        startTimeOut();
    }

    private void setWindow() {
        StatusBarCompat.setStatusBarColor((Activity) mContext, mContext.getResources().getColor(R.color.statusbar));
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);
        }

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void playSound() {
        try {
            mMediaPlayer = MediaPlayer.create(mContext,
                    RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void startTimeOut() {
        stopTimeOut();
        timeOutDisposable = Single.timer(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long aLong) -> {
                    sta.StartCall = false;
                    sta.StartDrop = false;
                    MstApp.getInstance().uISendMsg.Drop(sta.callId, true);
                    dismiss();
                }, (Throwable throwable) -> {
                    throwable.printStackTrace();
                });
    }

    private void stopTimeOut() {
        if (timeOutDisposable != null) {
            if (!timeOutDisposable.isDisposed()) {
                timeOutDisposable.dispose();
            }
            timeOutDisposable = null;
        }
    }

    @Override
    public void dismiss() {
        stopSound();
        stopTimeOut();
        StatusBarCompat.setStatusBarColor((Activity) mContext, mContext.getResources().getColor(R.color.main_color));
        super.dismiss();
        RxBus.getInstance().post(AppConstant.RxAction.IS_SHOW_CALL_IN_DIALOG, false);
    }

}
