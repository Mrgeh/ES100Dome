package com.example.es100dome;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.es100.AppConfig;
import com.es100.MyState;
import com.es100.bace.BaseActivity;
import com.es100.baserx.RxManager;
import com.es100.jni.MstApp;
import com.es100.login.LoginPlatformParam;
import com.es100.login.LoginPlatformResponse;
import com.es100.login.TerminalInfo;
import com.es100.login.TerminalPlatformType;
import com.es100.util.Const;
import com.es100.util.DeviceUtil;
import com.es100.util.LoadingDialog;
import com.es100.util.ScreenParam;
import com.es100.util.StringUtil;
import com.es100.util.UIHelper;
import com.example.es100dome.contract.LoginContract;
import com.example.es100dome.model.GetLoginResponseModel;
import com.example.es100dome.presenter.GetLoginResponsePresenter;
import com.ifreecomm.debug.MLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import permissions.dispatcher.NeedsPermission;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity<GetLoginResponsePresenter, GetLoginResponseModel> implements
        LoginContract.View, View.OnClickListener{
    public static final String LOGIN_GK_SUCCESS_KEY = "GK_REGISTER_SUCCESS";
    private static final String TAG = "LoginActivity";
    EditText et_username,et_password,et_ip,et_port;
    //    @Bind(R.id.tv_login)
//    TextView tv_login;
    private String username;
    private String password;
    private String gk_ip;
    private String gk_port;
    private MyState sta = MyState.getInstance();
    private String intranet = "";
    private Button btn_login;
    private RxManager rxManager;
    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {
        ScreenParam.init(LoginActivity.this);
        rxManager = new com.es100.baserx.RxManager();
        rxManager.on(LOGIN_GK_SUCCESS_KEY, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.e(TAG,"注册GK成功");
                startActivity(MeetingActivity.class);
                finish();
            }
        });
        et_username = (EditText) this.findViewById(R.id.et_username);
        et_password = (EditText) this.findViewById(R.id.et_password);
        et_ip = (EditText) this.findViewById(R.id.et_ip);
        et_port = (EditText) this.findViewById(R.id.et_port);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }
        @NeedsPermission({Manifest.permission.READ_PHONE_STATE})
        void loginAction(){
            LoadingDialog.showDialogForLoading( (Activity) mContext,"请稍后",true);
            LoginPlatformParam param = new LoginPlatformParam();
            param.setAccount(username);
            param.setPassword(password);
            param.setMachineId(DeviceUtil.getDeviceId(mContext));
            param.setPlatform(TerminalPlatformType.Android);
            param.setType(LoginPlatformParam.TYPE_SOFTTERMINAL);
            param.setVersion("V"+ MyApp.getName()+"_"+ MyApp.getCode());
            param.setMachineVersion(android.os.Build.MANUFACTURER+":"+ android.os.Build.MODEL);
            param.setMachineSystem("Android:"+android.os.Build.VERSION.RELEASE);
            MyState.getInstance().machineId = Const.PHONETYPE;
            MyState.getInstance().version = MyApp.getName();
            mPresenter.loginPlatformResponseRequest(param);
        }
    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }
    public static boolean mIsLogining = false;
    @Override
    public void showErrorTip(String msg) {
        LoadingDialog.cancelDialogForLoading();
        mIsLogining = false;
        UIHelper.toastMessageTop(msg);
    }

    @Override
    public void returnLoginPlatformResponse(final LoginPlatformResponse loginPlatformResponse) {
//        Log.i("KIIII","返回的登录数据" + loginPlatformResponse.toString());
        if(loginPlatformResponse.code.equals("200")) {
            // 登录成功,
            MyState.getInstance().setTerminalInfo(loginPlatformResponse.getData());
            // 注册GK服务器
            String source = splitIp(AppConfig.getInstance().getString(Const.GK_IP, ""));
            if(!StringUtil.isEmpty(loginPlatformResponse.getData().getGkIntranetIpAddress())) {
                intranet = splitIp(loginPlatformResponse.getData().getGkIntranetIpAddress());
            }

            sta.isIntranet = source.equals(intranet);
            // 全局保存token, 很多地方需要验证
            sta.setTerminalInfo(loginPlatformResponse.getData());
            sta.token = loginPlatformResponse.getData().getUserInfoDto().getToken();
            sta.isLogin = true;
            MLog.e(TAG, "token  " + loginPlatformResponse.getData().getUserInfoDto().getToken());
            AppConfig.getInstance().setString(Const.TOKEN, loginPlatformResponse.getData().getUserInfoDto().getToken());
            AppConfig.getInstance().setString(Const.GK_NAME, username);
            AppConfig.getInstance().setString(Const.GK_PWD, password);
            AppConfig.getInstance().setString(Const.TE_NAME, loginPlatformResponse.getData().getUserName());
            AppConfig.getInstance().setString(Const.TE_PWD, loginPlatformResponse.getData().getPassword());
            AppConfig.getInstance().setString(Const.TOKEN, loginPlatformResponse.getData().getUserInfoDto().getToken());
            AppConfig.getInstance().setString(Const.USER_NAME, loginPlatformResponse.getData().getUserInfoDto()
                    .getNickName());
            AppConfig.getInstance().setString(Const.USER_ID, loginPlatformResponse.getData().getUserInfoDto()
                    .getId()+"");
            AppConfig.getInstance().setString(Const.ACCOUNT, loginPlatformResponse.getData().getUserInfoDto().getAccount());
            AppConfig.getInstance().setString(Const.E164, loginPlatformResponse.getData().getE164());
            AppConfig.getInstance().setString(Const.MQTT_IP, loginPlatformResponse.getData().getMqttIpAddress());
            Single.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            registerGk(loginPlatformResponse.getData());
                        }
                    });
//            if(null!=loginPlatformResponse.getData().getSipConfigDto()){
//                registerSip(loginPlatformResponse.getData());
//            }

        } else {
            showErrorTip(loginPlatformResponse.msg);
            LoadingDialog.cancelDialogForLoading();
        }
    }
    private String splitIp(String gk_ip) {
        String s = gk_ip.split(":")[0];
        int i = s.lastIndexOf(".");
        return s.substring(0, i);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
    private void registerGk(TerminalInfo info) {
        if(!sta.isIntranet) {
            MLog.e(TAG,"外网ip");
            if(!TextUtils.isEmpty(info.getGkIpAddress()) && info.getGkIpAddress().contains(":")) {
                String gkIp = info.getGkIpAddress().substring(0, info.getGkIpAddress().lastIndexOf(":"));
                MstApp.getInstance().uISendMsg.SetGKCfg1(gkIp, info.getE164(), info.getUserName(), info.getPassword(),
                        1,info.getRasPort());
                MLog.i(TAG, "gkIp:" + gkIp + "," + info.getE164() + "," + info.getUserName() + "," + info.getPassword
                        ()+","+info.getRasPort());
                MyState.getInstance().E164 = info.getE164();
            }
        } else {
            MLog.e(TAG,"内网ip");
            if(!TextUtils.isEmpty(info.getGkIntranetIpAddress()) && info.getGkIntranetIpAddress().contains(":")) {
                String gkIp = info.getGkIntranetIpAddress().substring(0, info.getGkIntranetIpAddress().lastIndexOf
                        (":"));
                MstApp.getInstance().uISendMsg.SetGKCfg1(gkIp, info.getE164(), info.getUserName(), info.getPassword(),
                        1,info.getRasPort());
                MLog.i(TAG, "gkIp:" + gkIp + "," + info.getE164() + "," + info.getUserName() + "," + info.getPassword
                        ()+","+info.getRasPort());
                MyState.getInstance().E164 = info.getE164();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                gk_ip = et_ip.getText().toString().trim();
                gk_port = et_port.getText().toString().trim();
                if(StringUtil.isEmpty(username)){
//                    ToastUitl.showShort("请输入用户名");
                    com.es100.util.ToastUitl.showShort("请输入用户名");
                    return;
                }
                if(StringUtil.isEmpty(password)){
                    com.es100.util.ToastUitl.showShort("请输入用密码");
                    return;
                }
                if(StringUtil.isEmpty(gk_ip)){
                    com.es100.util.ToastUitl.showShort("请输入ip地址");
                    return;
                }
                if(StringUtil.isEmpty(gk_port)){
                    com.es100.util.ToastUitl.showShort("请输入端口号");
                    return;
                }
                AppConfig.getInstance().setString(Const.GK_IP,gk_ip);
                AppConfig.getInstance().setString(Const.GK_164,gk_port);
                LoginActivityPermissionsDispatcher.loginActionWithCheck(LoginActivity.this);
                break;
            default:
                break;
        }
    }
}
