package com.example.es100dome.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.baserx.RxBus;
import com.es100.util.DisplayUtil;
import com.example.es100dome.R;


/**
 * @author Administrator
 */
public class SwitchLayoutDialog {
    public static final String TAG = "ConfModeDialog";
    /** 加载数据对话框 */
    private static Dialog mLoadingDialog;
    /**
     * 显示加载对话框
     * @param context 上下文
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(final Activity context, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_switch_layout,null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog1);
        Window window = mLoadingDialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        window.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        attributes.y = 190;
        if(!MyState.getInstance().isChairman){
            attributes.x = DisplayUtil.getScreenWidth(context)/5;
        }else{
            attributes.x = DisplayUtil.getScreenWidth(context)/12;
        }
        window.setAttributes(attributes);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setContentView(view);
        TextView tv_remote_sub = (TextView) view.findViewById(R.id.tv_remote_sub);
        TextView tv_remote_main = (TextView) view.findViewById(R.id.tv_remote_main);
        TextView tv_small = (TextView) view.findViewById(R.id.tv_small);
        TextView tv_remote_doc = (TextView) view.findViewById(R.id.tv_remote_doc);
        if(MyState.getInstance().hasAux){
            tv_remote_sub.setVisibility(View.VISIBLE);
        }else{
            tv_remote_sub.setVisibility(View.GONE);
        }
        if(MyState.getInstance().isConfOnSharing){
            tv_remote_doc.setVisibility(View.VISIBLE);
        }else{
            tv_remote_doc.setVisibility(View.GONE);
        }
//        if(MyState.getInstance().isOpenLocal){
//            tv_close_local.setText("关闭本地画面");
//        }else{
//            tv_close_local.setText("打开本地画面");
//        }
//        tv_audio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MLog.e(TAG,"音视频切换");
////                RxBus.getInstance().post(AppConstant.RxAction.SWITCH_AUDIO,true);
//                Video2AudioDialog.showDialogForLoading(context,true);
//                cancelDialogForLoading();
//            }
//        });
//        tv_close_local.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RxBus.getInstance().post(AppConstant.RxAction.CLOSE_LOCAL, MyState.getInstance().isOpenLocal);
//                cancelDialogForLoading();
//            }
//        });
        tv_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getInstance().post(AppConstant.RxAction.LOCAL_SMALL, true);
                cancelDialogForLoading();
            }
        });
        tv_remote_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getInstance().post(AppConstant.RxAction.REMOTE_DOC,true);
                cancelDialogForLoading();
            }
        });
        tv_remote_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyState.getInstance().isConf = false;
                RxBus.getInstance().post(AppConstant.RxAction.REMOTE_SUB, true);
                cancelDialogForLoading();
            }
        });
        tv_remote_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyState.getInstance().isConf = false;
                RxBus.getInstance().post(AppConstant.RxAction.REMOTE_MAIN, true);
                cancelDialogForLoading();
            }
        });
        mLoadingDialog.show();
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
