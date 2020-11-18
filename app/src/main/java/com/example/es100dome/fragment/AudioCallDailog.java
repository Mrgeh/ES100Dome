package com.example.es100dome.fragment;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.es100.MstManager;
import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.baserx.RxManager;
import com.es100.db.ContactDbManager;
import com.es100.jni.MstApp;
import com.es100.jni.MstApp.MedRealTimeData;
import com.es100.listener.FragmentRegister;
import com.es100.listener.IAudioCall;
import com.es100.util.ScreenParam;
import com.es100.util.StringUtil;
import com.es100.util.UIHelper;
import com.example.es100dome.R;
import com.ifreecomm.debug.MLog;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import rx.functions.Action1;

public class AudioCallDailog extends Dialog {
    private static MyState sta = MyState.getInstance();
    public final static String TAG = "AudioCallDailog";

    private static boolean mIsSoundOff = false;//是否闭音标志，默认不闭音
    Context mContext;
    View view;
    static ImageView mImageViewVolume;
    ImageView mImageViewDown;
    static TextView mTextViewDuration;
    TextView mTextViewName;
    ImageView mImageViewPeopleIcon;
    ImageView ivIcon;
    TextView tvName;
    String iconPath;
    ContactDbManager mContactDbManager;
    private String temp164;
    private HashMap<String, String> e164map;
    public boolean speak = false;
    public static long currentTime;
    private RelativeLayout rl_voice;
    private Button bt_call_down;
    private RelativeLayout rl_sound_status;
    private static TextView tv_duration;
    private TextView tv_name;
    private TextView tv_sound_status;
    private Disposable timeOutDisposable;
    private RxManager rxManager = new RxManager();
    private TextView tv_sound;
    public AudioCallDailog(Context context) {
        super(context, /*R.style.Transparent*/R.style.dialogWindowAnim);
        mContext = context;
        //LogUtil.i("MyDialog", "Context context");
//        fullScreenChange();

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = ScreenParam.width;
        lp.height = ScreenParam.height;//- (int) (25 * ScreenParam.density)
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        speak = false;
        currentTime = System.currentTimeMillis();
    }

    private void setWindow() {
        Window dialogWindow = getWindow();
        if(dialogWindow != null) {
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
    @Override
    public void dismiss() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        Intent intent = new Intent("fullscreen");
//        MLog.d("ddd", "eeeeeebbb2");
//        mContext.sendBroadcast(intent);
        sta.callIn = -1;
        sta.GHStype = -1;
        stopTimeOut();
        super.dismiss();
    }


    long lasttime = 0;
    View.OnClickListener mClinkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_calldown:
                    if(sta.h323callstate != 4) {
                        return;
                    }
                    long curtime = System.currentTimeMillis();
                    if(curtime - lasttime < 500) {  //两次按键间隔小于0.5s,直接返回
                        return;
                    }
                    sta.StartDrop = true;
                    startTimeOut();
                    MstApp.getInstance().uISendMsg.Drop(sta.callId, false);
//                    dismiss();
//                    MainActivity.mHandler.post(MainActivity.mCheckDropTimeOut);
//                    lasttime = curtime;
                    break;
                case R.id.rl_sound_status:
                    if(mIsSoundOff) {
                        MstApp.getInstance().uISendMsg.SetShieldCfg(0);
                    } else {
                        MstApp.getInstance().uISendMsg.SetShieldCfg(1);
                    }
                    break;
                case R.id.rl_voice:
                    AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME,
                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                    break;
//                case R.id.btn_down:
//                    break;
//                case R.id.duration:
//                    break;
//                case R.id.name_textview:
//                    break;
                case R.id.people_imageview:
                    break;
//                case R.id.btn_spkeaking:
//                    if(speak)
//                        MstApp.getInstance().uISendMsg.LocalReqFloor(sta.callId);
//                    else {
//                        UIHelper.toastMessageTop(R.string.notsupportspeak);
//                    }
//                    break;
                default:
                    break;
            }
        }
    };
    private void startTimeOut() {
        stopTimeOut();
        timeOutDisposable = Single.timer(35, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(sta.h323callstate==4){
                        return;
                    }
                    MstApp.getInstance().uISendMsg.Drop(sta.callId,true);
                    sta.StartCall = false;
                    sta.StartDrop = true;
                    dismiss();
                }, Throwable:: printStackTrace);
    }
    private void stopTimeOut() {
        if(timeOutDisposable != null) {
            if(!timeOutDisposable.isDisposed()) {
                timeOutDisposable.dispose();
            }
            timeOutDisposable = null;
        }
    }

    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1) {
                long duration = System.currentTimeMillis() - sta.h323CallStartTime;
                int hour = (int) ((duration / (1000 * 60)) % 60/60);
                int diffMins = (int) ((duration / (1000 * 60)) % 60);
                String sMins = "" + diffMins;
                if(diffMins < 10) {
                    sMins = "0" + sMins;
                }
                int diffSecs = (int) ((duration / 1000) % 60);
                String sSecs = "" + diffSecs;
                if(diffSecs < 10) {
                    sSecs = "0" + sSecs;
                }
                tv_duration.setText(hour+":"+sMins + ":" + sSecs);
            } else if(msg.what == 4) {
                if(mImageViewVolume != null) {
                    MLog.e("qqqqq", "vlumn set");
                    if(msg.arg1 == 0) {
                        mImageViewVolume.setBackgroundResource(R.drawable.call_volume_0);
                        MLog.e("qqqqq", "vlumn set 1");
                    } else {
                        mImageViewVolume.setBackgroundResource(R.drawable.call_volume);
                        MLog.e("qqqqq", "vlumn set 2");
                    }
                }
                //UIHelper.getInstance().setImage(mImageViewVolume, R.drawable.not_silen_soud);
                //UIHelper.getInstance().setImage(mImageViewVolume, R.drawable.silen_sound);
            }
            return true;
        }
    });

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
       setWindow();
        mContactDbManager = new ContactDbManager(mContext);
        view = LayoutInflater.from(mContext).inflate(R.layout.dailog_audio_call, null);
        rl_voice = (RelativeLayout) view.findViewById(R.id.rl_voice);
        bt_call_down = (Button) view.findViewById(R.id.btn_calldown);
        rl_sound_status = (RelativeLayout) view.findViewById(R.id.rl_sound_status);
        tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_sound_status = (TextView) view.findViewById(R.id.tv_sound_status);
        mImageViewPeopleIcon = (ImageView) view.findViewById(R.id.people_imageview);
        tv_sound = (TextView) view.findViewById(R.id.tv_sound);
        rl_voice.setOnClickListener(mClinkListener);
        bt_call_down.setOnClickListener(mClinkListener);
        rl_sound_status.setOnClickListener(mClinkListener);
        setContentView(view);
        if(mIsSoundOff) {
            tv_sound_status.setText(mContext.getResources().getString(R.string.icon_close_mic));
            tv_sound.setText("取消闭音");
        } else {
            tv_sound_status.setText(mContext.getResources().getString(R.string.icon_open_mic));
            tv_sound.setText("闭音");
        }
        MstManager.getInstance().sendCmd().SetShieldCfg(1);
        MstManager.getInstance().sendCmd().SetShieldCfg(0);
        rxManager.on("finish_audio_call", new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                MLog.e(TAG,"关闭语音");
                dismiss();
            }
        });
        tv_name.setText(sta.showName);
        rxManager.on("AUDIO_CALL", new Action1<String>() {
            @Override
            public void call(String s) {
                if(!StringUtil.isEmpty(s)){
                    tv_name.setText(s);
                }
            }
        });
        rxManager.on(AppConstant.RxAction.SET_MUTE_RESULT, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(!aBoolean){
                    return;
                }
                if(!mIsSoundOff) {
                    mIsSoundOff = true;
                    tv_sound_status.setText(mContext.getResources().getString(R.string.icon_close_mic));
                    tv_sound.setText("取消闭音");

                } else {
                    mIsSoundOff = false;
                    tv_sound_status.setText(mContext.getResources().getString(R.string.icon_open_mic));
                    tv_sound.setText("闭音");
                }
            }
        });
        FragmentRegister.regIVideoCallListener(new IAudioCall() {

            @Override
            public void StopRcdAck(int ackId) {
            }

            @Override
            public void StartRcdAck(int ackId) {
            }

            @Override
            public void StartPhotoGraphAck(int ackId, String picName) {
            }

            @Override
            public void SetShieldCfgAck(int ackId) {
            }

            @Override
            public void LocalReqFloorAck(int ackId) {
                if(ackId == 0) {
                    UIHelper.toastMessageTop(R.string.speaksuccess);
                } else {
                    UIHelper.toastMessageTop(R.string.speakfail);
                }

            }

            @Override
            public void SetMuteCfgAck(int ackId) {
//                if(ackId == 0) {
//                    if(mImageViewVolume != null) {
//                        if(!sta.mIsMute) {
//                            UIHelper.getInstance().setImage(mImageViewVolume, R.drawable.not_silen_soud);
//                        } else {
//                            UIHelper.getInstance().setImage(mImageViewVolume, R.drawable.silen_sound);
//                        }
//                    }
//                }
            }

            @Override
            /**
             * 支持多点会议消息
             *
             */
            public void SupportConfCtrl() {
                speak = true;
//                mImageViewSpeak.setImageResource(R.drawable.tospeak);
            }

            @Override
            public void SetBlueToothAck(int ackId) {
            }

            @Override
            public void RepCallState() {
            }

            @Override
            public void OpenCapWithconsultVidFmt(int vidfmt) {
            }

            @Override
            public void GetMedRealTimeDataAck(int ackId, MedRealTimeData medRealTimeData) {
            }

            @Override
            public void DropCallAck(int ackId) {
            }

            @Override
            public void CleanScreenAck(int ackId) {
            }

            @Override
            public void CallTermAck(int ackId) {
            }

            @Override
            public void FlowCtrlAck(int ackId) {
                // TODO Auto-generated method stub

            }
        });

        timer.schedule(task, 0, 1000);
        initDialog();
    }

    void initDialog() {

    }

    public void setIp(String ip, String e164) {
    }

    public void setBg(int res) {
        getWindow().setBackgroundDrawableResource(res);
    }

    public void updateWH() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = ScreenParam.width;
        lp.height = ScreenParam.height - (int) (25 * ScreenParam.density);
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
    }

}
