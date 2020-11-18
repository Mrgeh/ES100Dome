package com.example.es100dome.widget;

import android.app.Activity;
import android.app.Dialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.es100.MstManager;
import com.es100.MyState;
import com.es100.util.StringUtil;
import com.es100.util.ToastUitl;
import com.example.es100dome.R;


/**
 * @author Administrator
 */
public class ConfPwdDialog {
    /** 加载数据对话框 */
    private static Dialog mLoadingDialog;
    private static boolean see_status;

    /**
     * 显示加载对话框
     * @param context 上下文
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(final Activity context, boolean cancelable,int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_conf_pwd,null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog1);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setContentView(view);
        mLoadingDialog.show();
        RelativeLayout rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        RelativeLayout rl_confirm = (RelativeLayout) view.findViewById(R.id.rl_confirm);
        EditText et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        TextView tv_see_status = (TextView) view.findViewById(R.id.tv_see_status);
        TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
        switch (type){
            case 0:
                tv_type.setText("请输入主席密码");
                break;
            case 1:
                tv_type.setText("请输入会议密码");
                break;
            default:
                break;
        }
        tv_see_status.setOnClickListener(v -> {
            if(see_status) {
                see_status = false;
                tv_see_status.setText(R.string.icon_see_off);
                TransformationMethod method =  PasswordTransformationMethod.getInstance();
                et_pwd.setTransformationMethod(method);
            } else {
                see_status = true;
                tv_see_status.setText(R.string.icon_see_on);
                HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
                et_pwd.setTransformationMethod(method);
            }
            et_pwd.setSelection(et_pwd.getText().toString().length());
        });
        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialogForLoading();
            }
        });
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type){
                    case 0:
                        if(StringUtil.isEmpty(et_pwd.getText().toString().trim())){
                            ToastUitl.showShort("请输入主席密码");
                            return;
                        }
                        MstManager.getInstance().sendCmd().confirmConfPwd(MyState.getInstance().callId,et_pwd.getText().toString());
                        break;
                    case 1:
                        if(StringUtil.isEmpty(et_pwd.getText().toString().trim())){
                            ToastUitl.showShort("请输入会议密码");
                            return;
                        }
                        MstManager.getInstance().sendCmd().confirmConfPwd(MyState.getInstance().callId,et_pwd.getText().toString());
                        break;
                    default:
                        break;
                }

                cancelDialogForLoading();
            }
        });
        return  mLoadingDialog;
    }
    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if(mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
