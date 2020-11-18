package com.example.es100dome;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.es100.AppConfig;
import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.bace.BaseActivity;
import com.es100.baserx.RxBus;
import com.es100.baserx.RxManager;
import com.es100.jni.MstApp;
import com.es100.util.Const;
import com.es100.util.StringUtil;
import com.es100.util.ToastUitl;
import com.example.es100dome.contract.MainActivityMessageControl;

public class DailActivity extends BaseActivity {
    private String s;
    private MainActivityMessageControl messageControl;
    private RxManager rxManager = new RxManager();
    @Override
    public int getLayoutId() {
        return R.layout.activity_dail;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        messageControl = new MainActivityMessageControl(this, rxManager);
        messageControl.addMessage();
        MyState sta = MyState.getInstance();
        EditText editText = (EditText) this.findViewById(R.id.et);
        editText.setText(AppConfig.getInstance().getString(Const.E164,""));
        TextView tv_e164 = (TextView) this.findViewById(R.id.tv_e164);
        tv_e164.setText(AppConfig.getInstance().getString(Const.E164,""));
        Button bt = (Button) this.findViewById(R.id.bt_call);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUitl.show("点击",0);
                s = editText.getText().toString();
                if(StringUtil.isEmpty(s)){
                    ToastUitl.showShort("请输入会议号或者ip地址");
                    return;
                }
//                if (!isSuccess(s)) {
//                    ToastUitl.showShort("请输入正确的会议号或者ip地址");
//                    return;
//                }
//                if(!sta.isNetworkOk){
//                    ToastUitl.showShort("网络不可用，请检查您的网络");
//                    return;
//                }
                sta.callStartTime = System.currentTimeMillis();
                sta.callType = 0;
                sta.callIn = 0;
                MyState.getInstance().StartCall = true;
                if(s.contains("##")){
                    sta.callIp = s;
                    sta.E164orName = s;
                    sta.callText = s;
                    sta.showName = s;
//                    ToastUitl.showLong(s.split("##")[0]+"----"+s.split("##")[1]);
                    MstApp.getInstance().uISendMsg.Call(s.split("##")[1],s.split("##")[0], 0, null, AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0) ,sta.callType);
                    RxBus.getInstance().post(AppConstant.RxAction.IS_SHOW_CALL_WAIT_DIALOG,true);
                    return;
                }
                if(StringUtil.isIpRightfull(s)){
                    sta.callIp = s;
                    sta.E164orName = null;
                    sta.callText = s;
                    sta.showName = s;
                    MstApp.getInstance().uISendMsg.Call(null, s, 0, null, AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0) ,sta.callType);
                }else{
                    sta.callIp = null;
                    sta.E164orName = s;
                    sta.myCallType = 1;
                    sta.callText = s;
                    sta.showName = s;
                    MstApp.getInstance().uISendMsg.Call(s, null, 0, null, AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0) ,sta.callType);
                }
                RxBus.getInstance().post(AppConstant.RxAction.IS_SHOW_CALL_WAIT_DIALOG,true);
            }
        });

    }
}
