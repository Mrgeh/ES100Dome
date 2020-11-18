package com.example.es100dome.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.example.es100dome.R;


/**
 * @author Administrator
 */
public class Video2AudioDialog {
    private static Dialog mLoadingDialog;
    private static MyState sta = MyState.getInstance();
    //    private static AudioCallWaitDialog audioCallWaitDialog;
    private static String meetingCode;
    private static String ip;

    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading( final Activity context, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_video_to_audio, null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.getWindow().setDimAmount(0.78f);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialogForLoading();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getInstance().post(AppConstant.RxAction.SWITCH_AUDIO,true);
                cancelDialogForLoading();
            }
        });
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
