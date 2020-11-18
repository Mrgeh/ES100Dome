package com.example.es100dome.fragment;

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
import com.example.es100dome.R;
import com.ifreecomm.debug.MLog;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 远端呼叫界面
 */
public class RemoteCallInDialog extends Dialog {
    public final String TAG = "RemoteCallInDialog";
    Context mContext;
    private int callId;
    private MediaPlayer mMediaPlayer;
    private MyState sta = MyState.getInstance();
    private String showName;
    private Disposable timeOutDisposable;
    View view;

    public RemoteCallInDialog(Context context, int id, String remReqCalle164, String showName) {
        super(context, R.style.dialogWindowAnim);
        mContext = context;
        callId = id;
        this.showName = showName;
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
        MLog.e(TAG, " showName" + showName);
        ipOrNameTextView.setText(showName);
        ivIcon.setImageResource(R.drawable.people_default);
        callTip.setText("来电信息");
        Button btStopCall = (Button) findViewById(R.id.bt_stop);
        Button btAccept = (Button) findViewById(R.id.bt_accept);
        btStopCall.setOnClickListener(arg0 -> {
            MLog.e(TAG, "点击挂断");
//            MQServiceByInConf.actionStop(App.getAppContext());
            try {
                MyMqttService.unSubscribeTOPIC(sta.topic_ConfsitetreeInfo);
                MyMqttService.unSubscribeTOPIC(sta.topic_start);
                MyMqttService.unSubscribeTOPIC(sta.topic_end);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            MstApp.getInstance().uISendMsg.Drop(callId, false);
            sta.IsInConference = false;
            dismiss();
        });
        btAccept.setOnClickListener(arg0 -> {
            stopSound();
            stopTimeOut();
            callTip.setText("正在连接");
            sta.isGHS = false;
            sta.StartCall = true;
            sta.callIn = 3;
            if (sta.is245) {
                MLog.e(TAG, "是245");
                MstApp.getInstance().uISendMsg.AcceptCall245(sta.callId);
                RxBus.getInstance().post(AppConstant.RxAction.HAND_ANSWER, true);
            } else {
                MLog.e(TAG, "不是245");
                RxBus.getInstance().post(AppConstant.RxAction.HAND_ANSWER, true);
            }

        });
        playSound();
        startTimeOut();
    }

    private void setWindow() {
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
                .subscribe(aLong -> {
                    sta.StartCall = false;
                    sta.StartDrop = false;
                    MLog.e(TAG, "呼叫超时挂断");
                    MstApp.getInstance().uISendMsg.Drop(sta.callId, true);
                    dismiss();
                }, Throwable::printStackTrace);
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
        super.dismiss();
        RxBus.getInstance()
                .post(AppConstant.RxAction.IS_SHOW_REMOTE_CALL_IN_DIALOG, false);
    }

}
