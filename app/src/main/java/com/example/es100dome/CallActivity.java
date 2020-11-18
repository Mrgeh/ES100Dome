package com.example.es100dome;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.es100.AppConfig;
import com.es100.MstManager;
import com.es100.MyState;
import com.es100.app.ApiConstant;
import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.es100.baserx.RxManager;
import com.es100.e.DisplayMode;
import com.es100.entity.EpId;
import com.es100.entity.MeetingControlParam;
import com.es100.jni.MstApp;
import com.es100.service.MQService;
import com.es100.util.Const;
import com.es100.util.DateTimeUtil;
import com.es100.util.FileUtil;
import com.es100.util.RxTimerUtil;
import com.es100.util.ScreenParam;
import com.es100.util.SendPictureUtils;
import com.es100.util.StringUtil;
import com.es100.util.ToastUitl;
import com.es100.util.UIHelper;
import com.example.es100dome.widget.AnimMessage;
import com.example.es100dome.widget.CallEndDialog;
import com.example.es100dome.widget.CameraModeDialog;
import com.example.es100dome.widget.ConfControllDialog;
import com.example.es100dome.widget.ConfMemberDialog;
import com.example.es100dome.widget.ConfPwdDialog;
import com.example.es100dome.widget.DisplayModeDialog;
import com.example.es100dome.widget.DragFrameLayout;
import com.example.es100dome.widget.DragView;
import com.example.es100dome.widget.LPAnimationManager;
import com.example.es100dome.widget.StretchDurationDialog;
import com.example.es100dome.widget.SwitchLayoutDialog;
import com.example.es100dome.widget.Video2AudioDialog;
import com.ifreecomm.debug.MLog;
import com.ifreecomm.enums.CameraType;
import com.ifreecomm.media.JfCameraCapture;
import com.ifreecomm.media.Media;
import com.ifreecomm.media.ScreenRecorder;
import com.ifreecomm.widget.AutoFitTextureView;
import com.jakewharton.rxbinding2.view.RxView;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.es100.e.DisplayMode.REMOTE_MAIN_LOCAL;

/**
 * @Description : 通话界面,视频,直播统一使用该界面
 * @Author : BruceChen
 * @Date : 2018/1/15 11:04
 */
@RuntimePermissions
public class CallActivity extends AppCompatActivity implements DisplayModeDialog.ModeSelectedListener {
    private SurfaceTexture mSurfaceTexture;
    private SurfaceTexture mSubSurfaceTexture;
    private final String TAG = "CallActivity";
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private RxManager mRxManager;
    private MyState sta = MyState.getInstance();
    private boolean bDropMyself;
    /**
     * 0 主流 1辅流 2 共享
     */
    private int mode = 0;
    /**
     * 所有视频显示控件的根布局
     */
    @Bind(R.id.allVideoViewLayout)
    DragFrameLayout allVideoViewLayout;

    /**
     * 辅流布局
     */
    @Bind(R.id.remoteSubLayout)
    FrameLayout remoteSubLayout;
    @Bind(R.id.remoteSubTextureView)
    AutoFitTextureView remoteSubTextureView;
    private Surface remoteSubSurface;

    /**
     * 主流
     */
    @Bind(R.id.remoteMainLayout)
    DragView remoteMainLayout;
    @Bind(R.id.remoteMainTextureView)
    AutoFitTextureView remoteMainTextureView;
    private Surface remoteMainSurface;

    /**
     * 本地摄像头,
     */
    @Bind(R.id.localLayout)
    FrameLayout localLayout;
    @Bind(R.id.localTextureView)
    AutoFitTextureView localTextureView;

    /**
     * 动画小图像,默认 {@link #localLayout}
     */
    private FrameLayout smallLayout;
    /**
     * 当前主画面显示主流或者辅流（0 主流 1 辅流）
     */
    private int main_mode = 0;
    @Bind(R.id.videoCallTopLayout)
    RelativeLayout topLayout;

    @Bind(R.id.videoCallBottomLayout)
    RelativeLayout bottomLayout;

    /**
     * 名称
     */
    @Bind(R.id.callName)
    TextView callName;

    /**
     * 时长
     */
    @Bind(R.id.callTime)
    TextView callTime;

    /**
     * 闭音
     */
    @Bind(R.id.muteImage)
    TextView muteImage;
    @Bind(R.id.muteTxt)
    TextView muteText;
    /**
     * 音量
     */
    @Bind(R.id.shieldIcon)
    TextView shieldIcon;
    /**
     * 更多
     */
    @Bind(R.id.tv_more)
    TextView tv_more;
    /**
     * 网络质量
     */
    @Bind(R.id.iv_net_state)
    ImageView iv_net_state;
    /**
     * 动画组
     */
    private AnimatorSet mAnimatorSetShow, mAnimatorSetHide;
    /**
     * 动画控件是否显示, 是否正在进行动画
     */
    private boolean isShow = true, isRunAnimation = false;
    /**
     * 系统音量开关,默认打开
     */
    private boolean isVolumeOff = false;
    /**
     * 是否闭音标志，默认不闭音
     */
    private boolean mIsSoundOff = false;

//    private boolean canApplySpeak = false;
    /**
     * 屏幕方向
     */
    private int activityRotation = 1;
    /**
     * 摄像头ID
     */
    private int mCameraId = 1;

    /**
     * 呼叫时长计时器
     */
    private Disposable callTimeDisposable;

    /**
     * 订阅者管理
     */
    private CompositeDisposable mCompositeDisposable;

    /**
     * 延时隐藏计时器
     */
    private Disposable hideLayoutDisposable;
    private Disposable cameraDisposable;
    private MediaProjectionManager mMediaProjectionManager ;
    private MediaProjection mediaProjection;
    public static final int REQUEST_CODE = 0;
//    private BroadcastReceiver mVolumeReceiver;

    /**
     * 默认模式
     */
    private DisplayMode defMode = REMOTE_MAIN_LOCAL;
    private ConfControllDialog confControllDialog;
    private ConfMemberDialog confMemberDialog;
    @Bind(R.id.ll_container)
    LinearLayout ll_container;
    @Bind(R.id.fl_container)
    FrameLayout docMainLayout;
    @Bind(R.id.frame_root)
    FrameLayout frame_root;
    @Bind(R.id.ll_doc)
    LinearLayout ll_doc;
    @Bind(R.id.tv_apply_speak)
    TextView tv_apply_speak;
    @Bind(R.id.tv_speak_icon)
    TextView tv_speak_icon;
    @Bind(R.id.speaker_container)
    LinearLayout speaker_container;
    private AgentWeb mAgentWeb;
    private JfCameraCapture.Builder builder;
    private Disposable timeOutDisposable;
    private ScreenRecorder screenRecorder;
    //    @Bind(R.id.bt_back)
//    Button bt_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 锁屏显示,开启屏幕,屏幕常亮
        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |*/ WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call);
        LPAnimationManager.init(this);
        ButterKnife.bind(this);
        mRxManager = new RxManager();
        initView();
        sta.IsInConference = true;
        sta.isOpenLocal = true;
        startHideLayoutTimer();
        AppConfig.getInstance().setInt(Const.CAMERA_ID, mCameraId);
        MstManager.getInstance().sendCmd().SetShieldCfg(1);
        MstManager.getInstance().sendCmd().SetShieldCfg(0);
        mRxManager.on(AppConstant.RxAction.SWITCH_CAMERA, (Action1<Boolean>) aBoolean -> switchCamera());
        mRxManager.on(AppConstant.RxAction.FINISH_VIDEO_CALL_ACTIVITY, (Action1<Boolean>) aBoolean -> {
            MLog.e(TAG, "视通界面结束");
            if (!bDropMyself) {
                if(null!=screenRecorder){
                    screenRecorder.quit();
                }
                SendPictureUtils.releasePictureEncoder();
                Media.getInstance().stopCapture();
                Media.getInstance().stopPreview(true);
                Media.getInstance().closeVideoRemChannel();
                CallEndDialog.cancelDialogForLoading();
                finish();
            }
        });
        mRxManager.on(AppConstant.RxAction.SET_MUTE_RESULT, setMuteResult);
        mRxManager.on(AppConstant.RxAction.APPLY_SPEAK_RESULT, (Action1<Boolean>) aBoolean -> {
            if (aBoolean) {
                UIHelper.toastMessageTop(R.string.speaksuccess);
            } else {
                UIHelper.toastMessageTop(R.string.speakfail);
            }
        });
        mRxManager.on(AppConstant.RxAction.STOP_RCV_SEC_VID, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                back2Sub();
                if (isShow) {
                    stopHideLayoutTimer();
                    hideLayout();
                } else {
                    showLayout1();
                }
                remoteSubLayout.setVisibility(View.INVISIBLE);
                ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                remoteMainLayout.setLayoutParams(remoteSubLayoutParams);
                remoteMainLayout.bringToFront();
                localLayout.setVisibility(View.INVISIBLE);
                smallLayout = localLayout;
                mode = 0;
                sta.layoutMode = mode;
            }
        });
        mRxManager.on(AppConstant.RxAction.CLOSE_LOCAL, new Action1<Boolean>() {
            @Override
            public void call(Boolean isOpen) {
                if (isOpen) {
                    sta.isOpenLocal = false;
                    Media.getInstance().stopCapturePre();
                    SendPictureUtils.sendPicture();
//                    Media.getInstance(CallActivity.this).sendApicture();
                } else {
                    SendPictureUtils.releasePictureEncoder();
                    MstApp.getInstance().OpenVidEnc(1);
//                    Media.getInstance(CallActivity.this).closePictureEncoder();
                    sta.isOpenLocal = true;
                }

            }
        });
        mRxManager.on(AppConstant.RxAction.REMOTE_SUB, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                back2Sub();
            }
        });
        mRxManager.on(AppConstant.RxAction.REMOTE_MAIN, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                MLog.e(TAG, "回到主流");

                if (mode == 2) {
                    MLog.e(TAG, "doc back ");

                    if (isShow) {
                        stopHideLayoutTimer();
                        hideLayout();
                    } else {
                        showLayout1();
                    }
                    ViewGroup.LayoutParams remoteSubLayoutParams1 = docMainLayout.getLayoutParams();
                    remoteMainLayout.setLayoutParams(remoteSubLayoutParams1);
                }
                if (mode == 1) {
                    MLog.e(TAG, "sub back");
                    back2Sub();
                    if (isShow) {
                        stopHideLayoutTimer();
                        hideLayout();
                    } else {
                        showLayout1();
                    }
                    ViewGroup.LayoutParams remoteSubLayoutParams1 = remoteSubLayout.getLayoutParams();
                    remoteMainLayout.setLayoutParams(remoteSubLayoutParams1);
                }
                remoteMainLayout.bringToFront();
                smallLayout = localLayout;
                mode = 0;
                sta.layoutMode = mode;
            }
        });
        if (sta.repPwdType != -1) {
            ConfPwdDialog.showDialogForLoading(CallActivity.this, false, sta.repPwdType);
        }
        if (sta.isHost) {
            tv_apply_speak.setText("我来发言");
            tv_speak_icon.setText(R.string.icon_talk);
        }
        if ((!sta.isHost) && sta.isChairman) {
            tv_apply_speak.setText("我来主讲");
            tv_speak_icon.setText(R.string.icon_speaker);
        }
        if ((!sta.isHost) && (!sta.isChairman)) {
            tv_apply_speak.setText("举手发言");
            tv_speak_icon.setText(R.string.icon_hand);
        }
        mRxManager.on(AppConstant.RxAction.CONF_PWD, new Action1<Integer>() {
            @Override
            public void call(Integer type) {
                ConfPwdDialog.showDialogForLoading(CallActivity.this, false, type);
            }
        });
        mRxManager.on(AppConstant.RxAction.RECEIVE_AUX, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                MLog.e(TAG, "收到辅流");
                if (mode == 2) {
                    return;
                }
                mode = 1;
                remoteSubLayout.setVisibility(View.VISIBLE);
                remoteSubLayout.bringToFront();
            }
        });
        mRxManager.on(AppConstant.RxAction.LOCAL_SMALL, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
//                if (sta.hasAux) {
//                    ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
//                    remoteMainLayout.setLayoutParams(localLayoutParams1);
//                    remoteMainLayout.bringToFront();
//                } else {
//                    localLayout.setVisibility(View.VISIBLE);
//                    localLayout.bringToFront();
//                }
                switch (mode) {
                    case 0:
                        localLayout.setVisibility(View.VISIBLE);
                        localLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case 1:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case 2:
                        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;

                    default:
                        break;
                }
            }
        });
        mRxManager.on(AppConstant.RxAction.SWITCH_AUDIO, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                MLog.e(TAG, "音视频切换");
                //关闭编码器
                Media.getInstance().stopCapture();
                Media.getInstance().stopPreview(true);
                Media.getInstance().closeVideoRemChannel();
                MstManager.getInstance().sendCmd().VideoToAudio(sta.callId);
//                Media.getInstance(CallActivity.this).CloseVideoEncoder();
//                if (sta.isChairman || sta.isHost) {
//                    Intent intent = new Intent(getApplicationContext(), AudioActivity.class);
//                    intent.putExtra("mIsSoundOff", mIsSoundOff);
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), AudioCallActivity.class);
//                    intent.putExtra("mIsSoundOff", mIsSoundOff);
//                    startActivity(intent);
//                }
                finish();
            }
        });
        if (sta.isOpenCamera) {
            localLayout.setVisibility(View.VISIBLE);
        } else {
            localLayout.setVisibility(View.INVISIBLE);
            localLayout.bringToFront();
        }
        if (sta.isTalkToMem) {

        }
//        MLog.e(TAG,"会议共享状态"+sta.isConfOnSharing);
//        if(sta.isConfOnSharing){
//            go2Doc();
//        }
        mRxManager.on(AppConstant.RxAction.CLOSE_CAMERA, new Action1<Boolean>() {
            @Override
            public void call(Boolean isOpen) {
                if (isOpen) {
                    localLayout.setVisibility(View.VISIBLE);
                } else {
                    localLayout.setVisibility(View.INVISIBLE);
                    localLayout.bringToFront();
                }
            }
        });
        mRxManager.on(AppConstant.RxAction.SWITCH_CAMERA_MODE, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                switchCamera();
            }
        });
        mRxManager.on(AppConstant.RxAction.APPLY_CHAIRMAN_SUCCEED, new Action1<Boolean>() {
            @Override
            public void call(Boolean isChairman) {
                if (isChairman) {
                    MLog.e(TAG, "申请主席成功");
                    ll_doc.setVisibility(View.VISIBLE);
                    if (!sta.isHost) {
                        tv_apply_speak.setText("我来主讲");
                        tv_speak_icon.setText(R.string.icon_speaker);
                    }

                    if (null != confMemberDialog) {
                        if (confMemberDialog.getDialog() != null) {
                            if (confMemberDialog.getDialog().isShowing()) {
                                confMemberDialog.getDialog().dismiss();
                            } else {
                                confControllDialog.getDialog().dismiss();
                            }
                            confControllDialog = new ConfControllDialog();
                            confControllDialog.show(getSupportFragmentManager(), "confControllDialog");
                        }
                        confMemberDialog = null;
//                        confMemberDialog.dismiss();
//                        confControllDialog = new ConfControllDialog();
//                        confControllDialog.show(getSupportFragmentManager(), "confControllDialog");

                    }
                } else {
                    MLog.e(TAG, "释放主席成功");
                    ll_doc.setVisibility(View.GONE);
//                    if(mode==2){
//                        back2Main();
//                    }
                    if (sta.isHost) {
                        tv_apply_speak.setText("我来发言");
                        tv_speak_icon.setText(R.string.icon_talk);
                    } else {
                        tv_apply_speak.setText("举手发言");
                        tv_speak_icon.setText(R.string.icon_hand);
                    }
                    if (null != confControllDialog) {
                        if (confControllDialog.getDialog() != null) {
                            if (confControllDialog.getDialog().isShowing()) {
                                confControllDialog.getDialog().dismiss();
                                confMemberDialog = new ConfMemberDialog();
                                confMemberDialog.show(getSupportFragmentManager(), "confMemberDialog");
                            }
                        } else {
                            confControllDialog.getDialog().dismiss();
                            confMemberDialog = new ConfMemberDialog();
                            confMemberDialog.show(getSupportFragmentManager(), "confMemberDialog");
                        }
                        confControllDialog = null;
                    }
                }

            }
        });
//        registerBroadcast();
        mRxManager.on(AppConstant.RxAction.REMOTE_DOC, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                MLog.e(TAG, "收到消息" + aBoolean);
                if (aBoolean) {
                    MLog.e(TAG, "进入文档共享界面");
                    go2Doc();
                } else {
                    MLog.e(TAG, "结束文档共享界面");
                    if (mAgentWeb != null) {
                        MLog.e(TAG, "mAgentWeb销毁");
//                        mAgentWeb.getUrlLoader().reload();
//                        mAgentWeb = AgentWeb.with(CallActivity.this)
//                                .setAgentWebParent(ll_container, new LinearLayout.LayoutParams(-1, -1))
//                                .useDefaultIndicator()// 使用默认进度条
//                                .createAgentWeb()//
//                                .ready()
//                                .go(MyState.getInstance().getTerminalInfo().getSharedFileCatalog()+"?confId="+sta.confId+"&userId="+sta.getTerminalInfo().getUserInfoDto().getId()
//                                        +"&userName="+sta.getTerminalInfo().getUserInfoDto().getAccount()+"&token="+sta.token);
                    }
                    back2Main();
                }
            }
        });
        mRxManager.on(AppConstant.RxAction.SPEAKER_NAME, new Action1<String>() {
            @Override
            public void call(String s) {
                MLog.e(TAG, "申请发言的人" + s);
                if (!StringUtil.isEmpty(s)) {
                    LPAnimationManager.addAnimalMessage(new AnimMessage(s, "", 0, ""));
                }
            }
        });
        mRxManager.on(AppConstant.RxAction.STRETCH_DURATION, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                StretchDurationDialog.showDialogForLoading(CallActivity.this, true);
            }
        });
        mRxManager.on(AppConstant.RxAction.REFRESH_DECODE_FORMAT, new Action1<Size>() {
            @Override
            public void call(Size size) {
                if (remoteMainTextureView != null && size.getWidth() != 0) {
                    com.ifreecomm.debug.MLog.e(TAG, "resize surface");
                    remoteMainTextureView.setAspectRatio(size.getWidth(), size.getHeight());
                }
            }
        });
        mRxManager.on(AppConstant.RxAction.CALL_END, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                callEnd();
            }
        });
    }
    private void startTimeOut() {
        stopTimeOut();
        timeOutDisposable = Single.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    SendPictureUtils.releasePictureEncoder();
                    Media.getInstance().stopCapture();
                    Media.getInstance().stopPreview(true);
                    Media.getInstance().closeVideoRemChannel();
                    CallEndDialog.cancelDialogForLoading();
                    finish();
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
//    private void registerBroadcast() {
//        IntentFilter filterVolume = new IntentFilter(VOLUME_CHANGED_ACTION);
//        mVolumeReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent == null) {
//                    return;
//                }
//                if (VOLUME_CHANGED_ACTION.equals(intent.getAction())) {
//                    setShield();
//                }
//            }
//        };
//        registerReceiver(mVolumeReceiver, filterVolume);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    @Override
    protected void onDestroy() {
        RxTimerUtil.cancel();
        MyState.getInstance().confTerminalArrayList.clear();
        MyState.getInstance().isOpenCamera = true;

        if(null!=mAgentWeb){
            mAgentWeb.getWebCreator().getWebView().destroy();
        }
//        unregisterReceiver(mVolumeReceiver);
        ToastUitl.reset();
        sta.isGetTerminal = false;
        sta.isFirstChair = true;
        sta.isHost = false;
        sta.isChairman = false;
        sta.isRemoteCall = false;
        sta.repPwdType = -1;
        sta.IsInConference = false;
        sta.hasAux = false;
        sta.is245 = false;
        sta.isOpenCamera = true;
        sta.isOpenLocal = true;
        sta.isConf = true;
        sta.isMqttStart = false;
        sta.isOpenChannel = false;
        sta.isOpenChannelSub = false;
        sta.mainChannelType = 0;
        sta.subChannelType = 0;
        sta.callIn = -1;
        sta.GHStype = -1;
        sta.isTalkToMem = false;
        super.onDestroy();
        LPAnimationManager.release();
//        Media.getInstance(CallActivity.this).closePictureEncoder();
        stopTimeOut();
        stopShowTime();
        stopHideLayoutTimer();
        ButterKnife.unbind(this);
        if (mRxManager != null) {
            mRxManager.clear();
            mRxManager = null;
        }
        destroyAnimation();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
        MQService.actionStop(CallActivity.this);
    }

    /**
     * 设置控件相关参数/属性/监听
     */

    public void initView() {
//        initConnectQuality();
        sta.isChairman = false;
        sta.isGetTerminal = true;
        sta.AudioOrVideoCtr = 0;
        // 设置名称
        callName.setText(sta.showName + "(" + sta.callText + ")");
        // 开始计时
        startShowTime();
        smallLayout = localLayout;
        allVideoViewLayout.addDragChildView(remoteMainLayout);
        allVideoViewLayout.addDragChildView(localLayout);
        mRxManager.on("click", new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (mode == 2) {
                    back2Main();
                } else {
                    if (isRunAnimation) {
                        return;
                    }
                    if (isShow) {
                        stopHideLayoutTimer();
                        hideLayout();
                    } else {
                        showLayout();
                    }
                }
            }
        });
        remoteSubLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunAnimation) {
                    return;
                }
                if (isShow) {
                    stopHideLayoutTimer();
                    hideLayout();
                } else {
                    showLayout();
                }
            }
        });
        allVideoViewLayout.setOnClickListener(v -> {
            if (isRunAnimation) {
                return;
            }
            if (isShow) {
                stopHideLayoutTimer();
                hideLayout();
            } else {
                showLayout();
            }
        });
        if (sta.hasAux) {
            MLog.e(TAG, "收到辅流");
            if (mode == 2) {
                return;
            }
            mode = 1;
            remoteSubLayout.setVisibility(View.VISIBLE);
            remoteSubLayout.bringToFront();

        }
        mCameraId = (notHasFrontCam() ? 0 : 1);

        builder = new JfCameraCapture
                .Builder(CallActivity.this)
                .setCameraType(CameraType.CAMERA_2)
                .setOpenGl(false)
                .setId(mCameraId)
                .setOrientation(getWindowManager().getDefaultDisplay().getRotation())
                .setDefaultWH(1920, 1080);

        localTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                com.ifreecomm.debug.MLog.i(TAG, "localTextureView*****onSurfaceTextureAvailable***************");
                builder.setOrientation(getWindowManager().getDefaultDisplay().getRotation());
                Media.getInstance().startPreview(localTextureView, builder);
                MstApp.getInstance().setLocalSurface(true);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//                JfCameraCapture jfCameraCapture = Media.getInstance().getJfCameraCapture();
//                if (jfCameraCapture != null) {
//                    JfCamera jfCamera = jfCameraCapture.getJfCamera();
//                    if (null != jfCamera) {
//                        jfCamera.onSurfaceTextureSizeChanged(width, height);
//                    }
//                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                com.ifreecomm.debug.MLog.i(TAG, "localTextureView*****onSurfaceTextureDestroyed***************");
                MstApp.getInstance().setLocalSurface(false);
                Media.getInstance().stopPreview(false);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        remoteMainTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                remoteMainSurface = new Surface(surface);
                MLog.e(TAG, "remoteMainSurface:" + remoteMainSurface);
                if (MstManager.getInstance().sendCmd() != null) {
                    MstManager.getInstance().sendCmd().SetRemoteSurface(remoteMainSurface);
//                    MstManager.getInstance().sendCmd().ReOpenVidDec();
                    sta.isOpenLocal = true;
                    if (Media.getInstance().isVideoChannelOpen()) {
                        MLog.e(TAG, "***********isVideoChannelOpen**********");
                        Media.getInstance().startVideoDecode(remoteMainSurface);
//                        if(mSurfaceTexture!=null){
//                            remoteMainTextureView.setSurfaceTexture(mSurfaceTexture);
//                        }
                    }
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                MLog.i(TAG, "width:" + width + ", height:" + height);
            }



            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                MLog.e(TAG, "remoteMainSurface*****onSurfaceTextureDestroyed***************");

//                mSurfaceTexture = surface;

                if (remoteMainSurface != null) {
                    remoteMainSurface.release();
                    remoteMainSurface = null;
                }

                if (MstManager.getInstance().sendCmd() != null) {
                    MstManager.getInstance().sendCmd().SetRemoteSurface(null);
                    //MstManager.getInstance().sendCmd().closeVidDec();
                }
                Media.getInstance().stopVideoDecode();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        remoteSubTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                remoteSubSurface = new Surface(surface);
                if (MstManager.getInstance().sendCmd() != null) {
                    MstManager.getInstance().sendCmd().SetSecRemoteSurface(remoteSubSurface);
//                    MstManager.getInstance().sendCmd().ReOpenSecVidDec();
                    sta.isOpenLocal = true;
                    if (Media.getInstance().isVideoChannelOpen()) {
                        MLog.i(TAG, "***********isVideoChannelOpen**********");
                        Media.getInstance().startVideoDecode(remoteSubSurface);
//                        if(mSubSurfaceTexture!=null){
//                            remoteSubTextureView.setSurfaceTexture(mSurfaceTexture);
//                        }
                    }
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (remoteSubSurface != null) {
                    remoteSubSurface.release();
                    remoteSubSurface = null;
                }
                if (MstManager.getInstance().sendCmd() != null) {
                    MstManager.getInstance().sendCmd().SetSecRemoteSurface(null);
                    //MstManager.getInstance().sendCmd().ReOpenSecVidDec();
                }
//                mSubSurfaceTexture = surface;
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
//        remoteSubTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                remoteSubSurface = new Surface(surface);
//                if (MstManager.getInstance().sendCmd() != null) {
//                    MstManager.getInstance().sendCmd().SetSecRemoteSurface(remoteSubSurface);
//                    MstManager.getInstance().sendCmd().ReOpenSecVidDec();
////                    Media.getInstance(CallActivity.this).closePictureEncoder();
////                    sta.isOpenLocal = true;
////                    if (null != mLocRenderer) {
////                        mLocRenderer.setFlag(true);
////                    }
//                }
//            }
//
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            }
//
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                if (remoteSubSurface != null) {
//                    remoteSubSurface.release();
//                    remoteSubSurface = null;
//                }
//                if (MstManager.getInstance().sendCmd() != null) {
//                    MstManager.getInstance().sendCmd().SetSecRemoteSurface(null);
//                    MstManager.getInstance().sendCmd().ReOpenSecVidDec();
//                }
//                return true;
//            }
//
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            }
//        });
//        remoteMainTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                remoteMainSurface = new Surface(surface);
//                if (MstManager.getInstance().sendCmd() != null) {
//                    MstManager.getInstance().sendCmd().SetRemoteSurface(remoteMainSurface);
//                    MstManager.getInstance().sendCmd().ReOpenVidDec();
////                    Media.getInstance(CallActivity.this).closePictureEncoder();
////                    sta.isOpenLocal = true;
////                    if (null != mLocRenderer) {
////                        mLocRenderer.setFlag(true);
////                    }
//                }
//            }
//
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            }
//
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                if (remoteMainSurface != null) {
//                    remoteMainSurface.release();
//                    remoteMainSurface = null;
//                }
//                if (MstManager.getInstance().sendCmd() != null) {
//                    MstManager.getInstance().sendCmd().SetRemoteSurface(null);
//                    MstManager.getInstance().sendCmd().closeVidDec();
//                }
//                return true;
//            }
//
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            }
//        });
        setShield();
        ll_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAgentWeb != null) {
                    mAgentWeb.getUrlLoader().reload();
                }
                if (!sta.isConfOnSharing) {
                    ToastUitl.showLong("当前会议暂无文档共享");
                }
                go2Doc();
            }
        });
//        addListener(findViewById(R.id.ll_doc),o -> new Consumer<Object>() {
//            @Override
//            public void accept(Object o) throws Exception {
//                //主动发起的共享
//                sta.isActiveShare = true;
//                go2Doc();
//            }
//        });
//        addListener(findViewById(R.id.iv_net_state), o -> NetworkStateDialog.showDialogForLoading(CallActivity.this, true));
        addListener(findViewById(R.id.btnAudio), o -> Video2AudioDialog.showDialogForLoading(CallActivity.this, true));
        addListener(findViewById(R.id.tv_call_end), o -> callEnd());
        addListener(findViewById(R.id.btnMute), o -> setMute());
        addListener(findViewById(R.id.btnShield), o -> {
            startHideLayoutTimer();
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager == null) {
                return;
            }
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        });
        addListener(findViewById(R.id.btnQuestTalk), o -> {
            startHideLayoutTimer();
            if ((!sta.isChairman) && (!sta.isHost)) {
                if (sta.canApplySpeak) {
                    MstManager.getInstance().sendCmd().LocalReqFloor(sta.callId);
                } else {
                    UIHelper.toastMessageTop(R.string.notsupportspeak);
                }
            } else if (sta.isHost) {
                //主持使用点名发言的接口
                RxBus.getInstance().post(AppConstant.RxAction.HOST_SPEAK, new MeetingControlParam(sta.confId, ApiConstant.SetSpeakSite, new EpId(sta.hostEpId)));
            } else if ((!sta.isHost) && sta.isChairman) {
                //主讲使用一键主讲的接口
                RxBus.getInstance().post(AppConstant.RxAction.ONE_KEYNOTE, sta.confId);
            }
        });
        addListener(findViewById(R.id.btnSwitchCamera), o -> CameraModeDialog.showDialogForLoading(CallActivity.this, true));
        addListener(findViewById(R.id.btnMode), o -> showSwitchMode());
//        if(sta.isChairman){
//            ll_doc.setVisibility(View.VISIBLE);
//        }else{
//            ll_doc.setVisibility(View.GONE);
//        }
        if(sta.isTourist){
            tv_more.setVisibility(View.INVISIBLE);
        }
        tv_more.setOnClickListener(v -> {
//            MLog.e(TAG, "是否为主席"+sta.isChairman+"---是否为主持"+sta.isHost);
            if (isShow) {
                hideLayout();
            }
            if (sta.isChairman || sta.isHost) {
                confControllDialog = new ConfControllDialog();
                confControllDialog.show(getSupportFragmentManager(), "confControllDialog");
            } else {
                confMemberDialog = new ConfMemberDialog();
                confMemberDialog.show(getSupportFragmentManager(), "confMemberDialog");
            }
        });
        LPAnimationManager.addGiftContainer(speaker_container);
//        if(sta.isTalkToMem){
//            ll_doc.setVisibility(View.VISIBLE);
//        }else{
//            if(sta.isChairman){
//                ll_doc.setVisibility(View.VISIBLE);
//            }else{
//                ll_doc.setVisibility(View.GONE);
//            }
//        }
        MLog.e(TAG,"进入通话 是否为主讲 "+sta.isChairman);
        if (sta.isChairman) {
            ll_doc.setVisibility(View.VISIBLE);
        } else {
            ll_doc.setVisibility(View.GONE);
        }
    }


    private void back2Main() {
        sta.isConf = false;
        RxBus.getInstance().post(AppConstant.RxAction.REMOTE_MAIN, true);
    }

    private void back2Sub() {
        if (!sta.hasAux) {
//            ToastUitl.showShort("会议暂时未发送辅流");
            return;
        }
        remoteSubLayout.setVisibility(View.VISIBLE);
        mode = 1;
        sta.layoutMode = mode;
        remoteSubLayout.bringToFront();
        localLayout.setVisibility(View.INVISIBLE);
        smallLayout = localLayout;
    }

    private void go2Doc() {
        //画中画显示远端主流
        mode = 2;
        dismissDialog();
        sta.layoutMode = mode;
        localLayout.setVisibility(View.INVISIBLE);
        docMainLayout.setVisibility(View.VISIBLE);
        docMainLayout.bringToFront();
        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
        remoteMainLayout.setLayoutParams(localLayoutParams2);
        remoteMainLayout.bringToFront();
        smallLayout = remoteMainLayout;
        if (isShow) {
            stopHideLayoutTimer();
            hideLayout();
        }
        AgentWebView.setWebContentsDebuggingEnabled(true);
//        if(mAgentWeb!=null){
//            mAgentWeb.clearWebCache();
//        }
        sta.testUrl = AppConstant.RxAction.TEST_URL+"?confId="+sta.confId+"&userId="+sta.getTerminalInfo().getUserInfoDto().getId()
                +"&userName="+sta.getTerminalInfo().getUserInfoDto().getAccount()+"&token="+sta.token;
        if (sta.isTalkToMem) {
            //点对点会议
            mAgentWeb = AgentWeb.with(CallActivity.this)
                    .setAgentWebParent(ll_container, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()// 使用默认进度条
                    .createAgentWeb()//
                    .ready()
//                    .go(sta.testUrl);
                    .go(MyState.getInstance().getTerminalInfo().getSharedFileCatalog() + "?confId=" + sta.confId + "&userId=" + sta.getTerminalInfo().getUserInfoDto().getId()
                            + "&userName=" + sta.getTerminalInfo().getUserInfoDto().getAccount() + "&token=" + sta.token/*+"confNature=P2pWithPlatform"*/);
        } else {
            //非点对点会议
            mAgentWeb = AgentWeb.with(CallActivity.this)
                    .setAgentWebParent(ll_container, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()// 使用默认进度条
                    .createAgentWeb()//
                    .ready()
//                    .go(sta.testUrl);
                    .go(MyState.getInstance().getTerminalInfo().getSharedFileCatalog() + "?confId=" + sta.confId + "&userId=" + sta.getTerminalInfo().getUserInfoDto().getId()
                            + "&userName=" + sta.getTerminalInfo().getUserInfoDto().getAccount() + "&token=" + sta.token/*+"confNature=PlatformConf"*/);
        }

//        .go(ApiConstant.TEST_URL);
//        sta.testUrl = MyState.getInstance().getTerminalInfo().getSharedFileCatalog()+"?confId="+sta.confId+"&userId="+sta.getTerminalInfo().getUserInfoDto().getId()
//                +"&userName="+sta.getTerminalInfo().getUserInfoDto().getAccount()+"&token="+sta.token;
        MLog.e(TAG, "url" + MyState.getInstance().getTerminalInfo().getSharedFileCatalog() + "?confId=" + sta.confId + "&userId=" + sta.getTerminalInfo().getUserInfoDto().getId()
                + "&userName=" + sta.getTerminalInfo().getUserInfoDto().getAccount() + "&token=" + sta.token);
        mAgentWeb.getWebCreator().getWebView().setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                MLog.e(TAG, "图片路径" + url);
                Bitmap bitmap = stringToBitmap(url);
                FileUtil.saveBitmap(CallActivity.this, bitmap);
            }
        });
        mAgentWeb.getJsAccessEntrace().callJs("closeWhiteBoard()");
        mAgentWeb.getWebCreator().getWebView().addJavascriptInterface(new JsToJava(), "androidShare");
    }

    private class JsToJava {
        @JavascriptInterface
        public void closeWhiteBoardWin(String paramFromJS) {
            MLog.e(TAG, "js返回结果:" + paramFromJS);
            if (paramFromJS.equals("close")) {
                    back2Main();
            }
        }
    }

    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void setShield() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return;
        }
        // 当前的媒体音量
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currVolume == 0) {
            isVolumeOff = true;
            shieldIcon.setText(R.string.icon_close_sound);
        } else {
            isVolumeOff = false;
            shieldIcon.setText(R.string.icon_open_sound);
        }
    }

    private void setMute() {
        startHideLayoutTimer();
        if (mIsSoundOff) {
            MstManager.getInstance().sendCmd().SetShieldCfg(0);
        } else {
            MstManager.getInstance().sendCmd().SetShieldCfg(1);
        }
    }

    /**
     * 闭音开关结果
     */
    private Action1<Boolean> setMuteResult = aBoolean -> {
        if (!aBoolean) {
            return;
        }
        if (!mIsSoundOff) {
//            muteImage.setText(R.string.icon_close_mic);
            muteImage.setText(R.string.icon_close_mic);
            muteText.setText(R.string.open_mic);
            muteImage.setTextColor(getResources().getColor(R.color.red));
            muteText.setTextColor(getResources().getColor(R.color.red));
        } else {
//            muteImage.setText(R.string.icon_open_mic);
            muteImage.setText(R.string.icon_open_mic);
            muteText.setText(R.string.close_mic);
            muteImage.setTextColor(getResources().getColor(R.color.white));
            muteText.setTextColor(getResources().getColor(R.color.white));
        }
        mIsSoundOff = !mIsSoundOff;
    };

    /**
     * 画中画
     */
    private void setCameraMode() {
        stopHideLayoutTimer();
        CameraModeDialog.showDialogForLoading(CallActivity.this, true);
        cameraDisposable = Single.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        CameraModeDialog.cancelDialogForLoading();
                    }
                });
    }

    /**
     * 挂断
     */
    private void callEnd() {
        sta.StartDrop = true;
//        bDropMyself = true;
        int callBuild = 4;
        sta.isRemoteCallIn = false;
        MstManager.getInstance().sendCmd().Drop(sta.callId, false);
        CallEndDialog.showDialogForLoading(this);
//        startTimeOut();
//        if (sta.h323callstate == callBuild) {
//            MLog.e(TAG, "callEnd1");
//            MstManager.getInstance().sendCmd().Drop(sta.callId, false);
//        } else if (sta.IsInConference) {
//            MLog.e(TAG, "callEnd2");
//            sta.startExitMeeting = true;
//            MstManager.getInstance().sendCmd().ExitConferenceCmd();
//        } else {
//            MLog.e(TAG, "callEnd3");
//            sta.startEndTalkTo = true;
//            MstManager.getInstance().sendCmd().EndTalkToCmd();
//        }
//        SendPictureUtils.releasePictureEncoder();
//        Media.getInstance().stopCapture();
//        Media.getInstance().stopPreview(true);
//        Media.getInstance().closeVideoRemChannel();
//        finish();
    }

    private void addListener(View view, Consumer<Object> consumer) {
        addSubscribe(RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(consumer));
    }


    private void startShowTime() {
        callTimeDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(aLong -> DateTimeUtil.getTimeHMS(System.currentTimeMillis() - sta
                        .h323CallStartTime))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((String s) -> {
                    Log.i("startShowTime","startShowTime----"+s);
                    callTime.setText(s);
                });
    }

    private void stopShowTime() {
        if (callTimeDisposable != null) {
            callTimeDisposable.dispose();
            callTimeDisposable = null;
        }
    }

    private void startHideLayoutTimer() {
        stopHideLayoutTimer();
        hideLayoutDisposable = Single.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    stopHideLayoutTimer();
                    if (isRunAnimation) {
                        return;
                    }
                    if (isShow) {
                        hideLayout();
                    }
                });
    }

    private void stopHideLayoutTimer() {
        if (hideLayoutDisposable != null) {
            if (!hideLayoutDisposable.isDisposed()) {
                hideLayoutDisposable.dispose();
            }
            hideLayoutDisposable = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开本地摄像头
//        bindRender();
//        CallActivityPermissionsDispatcher.bindRenderWithCheck(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭本地摄像头
//        unBindRender();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CallActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void setCameraId(int cameraId) {
        AppConfig.getInstance().setInt(Const.CAMERA_ID, cameraId);
    }

    /**
     * bind render to glSurfaceView
     */
    @NeedsPermission({Manifest.permission.CAMERA})
    void bindRender() {
//        if (mLocRenderer == null) {
//            glLocalSurface = new GLSurfaceView(this);
//            FrameLayout.LayoutParams layoutParams =
//                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
//                            .MATCH_PARENT);
//            glLocalSurface.setLayoutParams(layoutParams);
//            localLayout.addView(glLocalSurface);
//            // select GLES 2.0
//            glLocalSurface.setEGLContextClientVersion(2);
//            mLocRenderer = new GLSurfaceRenderer(this, glLocalSurface);
//
//            mLocRenderer.setCameraDisplayOrientation(activityRotation, mCameraId);
//            glLocalSurface.setRenderer(mLocRenderer);
//            glLocalSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        }
    }


    /**
     * 切换摄像头
     */
    private void switchCamera() {

        startHideLayoutTimer();
        if (!sta.isOpeningCamera) {
            sta.isOpeningCamera = true;
            boolean mHasUsb = (sta.IsMT310A || sta.IsMT310B || sta.IsMT300C);
            switch (mCameraId) {
                case 0:
                    mCameraId = 1;
                    break;
                case 1:
                    if (mHasUsb) {
                        if (MstManager.getInstance().sendCmd().checkExternalCameraExist()) {
                            mCameraId = 2;
                        } else {
                            mCameraId = 0;
                        }
                    } else {
                        mCameraId = 0;
                    }
                    break;
                case 2:
                    mCameraId = 0;
                    break;
                default:
                    mCameraId = 0;
                    break;
            }

            if (!sta.isOpenLocal) {
                SendPictureUtils.releasePictureEncoder();
                sta.isOpenLocal = true;
            }

            Media.getInstance().switchCamera(mCameraId);

            sta.isOpeningCamera = false;
        }
    }

    /**
     * 切换布局模式
     */
    private void showSwitchMode() {
        stopHideLayoutTimer();
//        DisplayModeDialog modeDialog = new DisplayModeDialog(this, this);
//        modeDialog.setOnDismissListener(dialog -> startHideLayoutTimer());
//        modeDialog.show();
        SwitchLayoutDialog.showDialogForLoading(CallActivity.this, true);
    }

    /**
     * 动画时长
     */
    private long duration = 600;
    /**
     * 动画监听
     */
    private Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isRunAnimation = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isRunAnimation = false;
            isShow = !isShow;
            if (isShow) {
                startHideLayoutTimer();
            } else {
                stopHideLayoutTimer();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    /**
     * 隐藏布局
     */
    private void hideLayout() {
        destroyAnimation();
        // 隐藏
        mAnimatorSetHide = new AnimatorSet();
        mAnimatorSetHide.setDuration(duration);
        mAnimatorSetHide.addListener(listener);
        // 上标题栏
        ObjectAnimator hideTopLayoutAnimator = ObjectAnimator.ofFloat(topLayout, "translationY",
                0f, -55 * ScreenParam.density);
        hideTopLayoutAnimator.setDuration(duration);
        // 下操作栏
        ObjectAnimator hideBottomLayoutAnimator1 = ObjectAnimator.ofFloat(bottomLayout, "translationY",
                0f, 80 * ScreenParam.density);
        hideBottomLayoutAnimator1.setDuration(duration);
        // 小图像
        ObjectAnimator hideBottomLayoutAnimator2 = ObjectAnimator.ofFloat(smallLayout, "translationY",
                0f, 80 * ScreenParam.density);
        hideBottomLayoutAnimator2.setDuration(duration);
        mAnimatorSetHide.playTogether(hideTopLayoutAnimator, hideBottomLayoutAnimator1, hideBottomLayoutAnimator2);
        mAnimatorSetHide.start();
    }

    /**
     * 显示布局
     */
    private void showLayout() {
        destroyAnimation();
        // 显示
        mAnimatorSetShow = new AnimatorSet();
        mAnimatorSetShow.setDuration(duration);
        mAnimatorSetShow.addListener(listener);
        // 上标题栏
        ObjectAnimator showTopLayoutAnimator = ObjectAnimator.ofFloat(topLayout, "translationY",
                -55 * ScreenParam.density, 0f);
        showTopLayoutAnimator.setDuration(duration);
        // 下操作栏
        ObjectAnimator showBottomLayoutAnimator1 = ObjectAnimator.ofFloat(bottomLayout, "translationY",
                80 * ScreenParam.density, 0f);
        showBottomLayoutAnimator1.setDuration(duration);
        // 小图像
        ObjectAnimator showBottomLayoutAnimator2 = ObjectAnimator.ofFloat(smallLayout, "translationY",
                80 * ScreenParam.density, 0f);
        showBottomLayoutAnimator2.setDuration(duration);
        mAnimatorSetShow.playTogether(showTopLayoutAnimator, showBottomLayoutAnimator1, showBottomLayoutAnimator2);
        mAnimatorSetShow.start();
    }

    private void showLayout1() {
        destroyAnimation();
        // 显示
        mAnimatorSetShow = new AnimatorSet();
        mAnimatorSetShow.setDuration(1);
        mAnimatorSetShow.addListener(listener);
        // 上标题栏
        ObjectAnimator showTopLayoutAnimator = ObjectAnimator.ofFloat(topLayout, "translationY",
                -55 * ScreenParam.density, 0f);
        showTopLayoutAnimator.setDuration(1);
        // 下操作栏
        ObjectAnimator showBottomLayoutAnimator1 = ObjectAnimator.ofFloat(bottomLayout, "translationY",
                80 * ScreenParam.density, 0f);
        showBottomLayoutAnimator1.setDuration(1);
        // 小图像
        ObjectAnimator showBottomLayoutAnimator2 = ObjectAnimator.ofFloat(smallLayout, "translationY",
                80 * ScreenParam.density, 0f);
        showBottomLayoutAnimator2.setDuration(1);
        mAnimatorSetShow.playTogether(showTopLayoutAnimator, showBottomLayoutAnimator1, showBottomLayoutAnimator2);
        mAnimatorSetShow.start();
    }

    /**
     * 销毁动画
     */
    private void destroyAnimation() {
        if (mAnimatorSetShow != null) {
            if (mAnimatorSetShow.isStarted()) {
                mAnimatorSetShow.end();
            }
            mAnimatorSetShow.cancel();
            mAnimatorSetShow = null;
        }
        if (mAnimatorSetHide != null) {
            if (mAnimatorSetHide.isRunning()) {
                mAnimatorSetHide.end();
            }
            mAnimatorSetHide.cancel();
            mAnimatorSetHide = null;
        }
    }


    @Override
    public void onBackPressed() {
        if (isRunAnimation) {
            return;
        }
        stopHideLayoutTimer();
        if (isShow) {
            hideLayout();
        }
    }

    @Override
    public boolean modeSelected(DisplayMode mode) {

        if (defMode == mode) {
            return false;
        }
        switch (mode) {
            case REMOTE_MAIN_LOCAL:
                switch (defMode) {
                    case LOCAL_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams);
                        remoteMainLayout.setLayoutParams(localLayoutParams);
                        localLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case REMOTE_MAIN:
                        localLayout.bringToFront();
                        break;
                    case REMOTE_SUB:
                        remoteMainLayout.bringToFront();
                        localLayout.bringToFront();
                        break;
                    case REMOTE_MAIN_REMOTE_SUB:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteSubLayoutParams);
                        remoteSubLayout.setLayoutParams(localLayoutParams1);
                        remoteMainLayout.bringToFront();
                        localLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case REMOTE_SUB_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams2 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams2);
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        remoteMainLayout.bringToFront();
                        localLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    default:
                        break;
                }
                break;
            case LOCAL_REMOTE_MAIN:
                switch (defMode) {
                    case REMOTE_MAIN_LOCAL:
                        ViewGroup.LayoutParams localLayoutParams = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams);
                        remoteMainLayout.setLayoutParams(localLayoutParams);
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams1 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams1);
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        localLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case REMOTE_SUB:
                        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams2 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams2);
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        localLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case REMOTE_MAIN_REMOTE_SUB:
                        ViewGroup.LayoutParams remoteMainLayoutParams3 = remoteMainLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        remoteSubLayout.setLayoutParams(remoteMainLayoutParams3);
                        remoteMainLayout.setLayoutParams(remoteSubLayoutParams);
                        localLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case REMOTE_SUB_REMOTE_MAIN:
                        localLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        break;
                    default:
                        break;
                }
                break;
            case REMOTE_MAIN:
                switch (defMode) {
                    case REMOTE_MAIN_LOCAL:
                    case REMOTE_SUB:
                        remoteMainLayout.bringToFront();
                        break;
                    case REMOTE_MAIN_REMOTE_SUB:
                        ViewGroup.LayoutParams localLayoutParams = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteSubLayoutParams);
                        remoteSubLayout.setLayoutParams(localLayoutParams);
                        remoteMainLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case REMOTE_SUB_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams1 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams1);
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        remoteMainLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case LOCAL_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams2 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams2);
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        remoteMainLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    default:
                        break;
                }
                break;
            case REMOTE_SUB:
                switch (defMode) {
                    case REMOTE_MAIN_LOCAL:
                    case REMOTE_MAIN:
                        remoteSubLayout.bringToFront();
                        break;
                    case REMOTE_MAIN_REMOTE_SUB:
                        ViewGroup.LayoutParams localLayoutParams = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteSubLayoutParams);
                        remoteSubLayout.setLayoutParams(localLayoutParams);
                        remoteSubLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case REMOTE_SUB_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams1 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams1);
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        remoteSubLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    case LOCAL_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams2 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams2 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams2);
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        remoteSubLayout.bringToFront();
                        smallLayout = localLayout;
                        break;
                    default:
                        break;
                }
                break;
            case REMOTE_MAIN_REMOTE_SUB:
                switch (defMode) {
                    case REMOTE_MAIN_LOCAL:
                    case REMOTE_MAIN:
                    case REMOTE_SUB:
                        ViewGroup.LayoutParams localLayoutParams = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteSubLayoutParams);
                        remoteSubLayout.setLayoutParams(localLayoutParams);
                        remoteMainLayout.bringToFront();
                        remoteSubLayout.bringToFront();
                        smallLayout = remoteSubLayout;
                        break;
                    case REMOTE_SUB_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams1 = remoteSubLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams1 = remoteMainLayout.getLayoutParams();
                        remoteSubLayout.setLayoutParams(remoteMainLayoutParams1);
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        remoteMainLayout.bringToFront();
                        remoteSubLayout.bringToFront();
                        smallLayout = remoteSubLayout;
                        break;


                    case LOCAL_REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams2 = remoteSubLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams2 = remoteMainLayout.getLayoutParams();
                        remoteSubLayout.setLayoutParams(remoteMainLayoutParams2);
                        remoteMainLayout.setLayoutParams(localLayoutParams2);
                        remoteMainLayout.bringToFront();
                        remoteSubLayout.bringToFront();
                        smallLayout = remoteSubLayout;
                        break;
                    default:
                        break;
                }
                break;
            case REMOTE_SUB_REMOTE_MAIN:
                switch (defMode) {
                    case REMOTE_MAIN_REMOTE_SUB:
                        ViewGroup.LayoutParams remoteMainLayoutParams = remoteMainLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteSubLayoutParams = remoteSubLayout.getLayoutParams();
                        remoteMainLayout.setLayoutParams(remoteSubLayoutParams);
                        remoteSubLayout.setLayoutParams(remoteMainLayoutParams);
                        remoteSubLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case REMOTE_SUB:
                    case REMOTE_MAIN_LOCAL:
                    case REMOTE_MAIN:
                        ViewGroup.LayoutParams localLayoutParams1 = localLayout.getLayoutParams();
                        ViewGroup.LayoutParams remoteMainLayoutParams1 = remoteMainLayout.getLayoutParams();
                        localLayout.setLayoutParams(remoteMainLayoutParams1);
                        remoteMainLayout.setLayoutParams(localLayoutParams1);
                        remoteSubLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    case LOCAL_REMOTE_MAIN:
                        remoteSubLayout.bringToFront();
                        remoteMainLayout.bringToFront();
                        smallLayout = remoteMainLayout;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        defMode = mode;
        return true;
    }

    private void dismissDialog() {
        SwitchLayoutDialog.cancelDialogForLoading();
        CameraModeDialog.cancelDialogForLoading();
    }

    private void dismisssConfDialog() {
        if (confMemberDialog != null) {
            confMemberDialog.dismiss();
            confMemberDialog = null;
        }
        if (confControllDialog != null) {
            confControllDialog.dismiss();
            confControllDialog = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MLog.e(TAG,"执行onStop");
//        Button button = new Button(getApplicationContext());
//        WindowManager wm = (WindowManager) getApplicationContext()
//                .getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//
//        /**
//         * 以下都是WindowManager.LayoutParams的相关属性 具体用途请参考SDK文档
//         */
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 这里是关键，你也可以试试2003
//        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
//        /**
//         * 这里的flags也很关键 代码实际是wmParams.flags |=FLAG_NOT_FOCUSABLE;
//         * 40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
//         */
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//        wmParams.width = 1;
//        wmParams.height = 1;
//        wm.addView(button, wmParams); // 创建View
    }

    private boolean notHasFrontCam() {
        if ("T6B".equals(Build.MODEL) || "MT100DBR110".equals(Build.MODEL) || "T6A".equals(Build.MODEL)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_CODE) return;
        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
//        screenRecorder = new ScreenRecorder(new Size(1280,720),outMetrics.densityDpi,mediaProjection);
//        screenRecorder.start();
    }

    public void initScreen() {
        Media.getInstance().stopCapturePre();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }
}
