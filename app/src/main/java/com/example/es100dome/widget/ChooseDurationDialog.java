package com.example.es100dome.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.es100.util.ToastUitl;
import com.example.es100dome.R;


/**
 * @author Administrator
 */
public class ChooseDurationDialog {
    private static Dialog mLoadingDialog;
    private static int seconds = 0;
    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading( final Activity context, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_duration, null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.getWindow().setDimAmount(0.78f);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);

        TextView tv_10 = (TextView) view.findViewById(R.id.tv_10);
        TextView tv_20 = (TextView) view.findViewById(R.id.tv_20);
        TextView tv_30 = (TextView) view.findViewById(R.id.tv_30);
        TextView tv_60 = (TextView) view.findViewById(R.id.tv_60);
        TextView tv_120 = (TextView) view.findViewById(R.id.tv_120);
        TextView tv_180 = (TextView) view.findViewById(R.id.tv_180);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialogForLoading();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seconds==0){
                    ToastUitl.showShort("请选择会议延长的时间");
                    return;
                }
                RxBus.getInstance().post(AppConstant.RxAction.STRETCH_SECONDS,seconds);
                cancelDialogForLoading();
            }
        });
        tv_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_mute_all);
                tv_20.setBackgroundResource(R.drawable.shape_demute_all);
                tv_30.setBackgroundResource(R.drawable.shape_demute_all);
                tv_60.setBackgroundResource(R.drawable.shape_demute_all);
                tv_120.setBackgroundResource(R.drawable.shape_demute_all);
                tv_180.setBackgroundResource(R.drawable.shape_demute_all);
                seconds = (10*60);
            }
        });
        tv_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_demute_all);
                tv_20.setBackgroundResource(R.drawable.shape_mute_all);
                tv_30.setBackgroundResource(R.drawable.shape_demute_all);
                tv_60.setBackgroundResource(R.drawable.shape_demute_all);
                tv_120.setBackgroundResource(R.drawable.shape_demute_all);
                tv_180.setBackgroundResource(R.drawable.shape_demute_all);
                seconds = (20*60);
            }
        });
        tv_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_demute_all);
                tv_20.setBackgroundResource(R.drawable.shape_demute_all);
                tv_30.setBackgroundResource(R.drawable.shape_mute_all);
                tv_60.setBackgroundResource(R.drawable.shape_demute_all);
                tv_120.setBackgroundResource(R.drawable.shape_demute_all);
                tv_180.setBackgroundResource(R.drawable.shape_demute_all);
                seconds = (30*60);
            }
        });
        tv_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_demute_all);
                tv_20.setBackgroundResource(R.drawable.shape_demute_all);
                tv_30.setBackgroundResource(R.drawable.shape_demute_all);
                tv_60.setBackgroundResource(R.drawable.shape_mute_all);
                tv_120.setBackgroundResource(R.drawable.shape_demute_all);
                tv_180.setBackgroundResource(R.drawable.shape_demute_all);
                seconds = (60*60);
            }
        });
        tv_120.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_demute_all);
                tv_20.setBackgroundResource(R.drawable.shape_demute_all);
                tv_30.setBackgroundResource(R.drawable.shape_demute_all);
                tv_60.setBackgroundResource(R.drawable.shape_demute_all);
                tv_120.setBackgroundResource(R.drawable.shape_mute_all);
                tv_180.setBackgroundResource(R.drawable.shape_demute_all);
                seconds = (120*60);
            }
        });
        tv_180.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_10.setBackgroundResource(R.drawable.shape_demute_all);
                tv_20.setBackgroundResource(R.drawable.shape_demute_all);
                tv_30.setBackgroundResource(R.drawable.shape_demute_all);
                tv_60.setBackgroundResource(R.drawable.shape_demute_all);
                tv_120.setBackgroundResource(R.drawable.shape_demute_all);
                tv_180.setBackgroundResource(R.drawable.shape_mute_all);
                seconds = (180*60);
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
