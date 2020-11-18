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
import com.es100.util.ToastUitl;
import com.example.es100dome.R;
import com.ifreecomm.debug.MLog;


/**
 * @author Administrator
 */
public class CameraModeDialog {
    /** 加载数据对话框 */
    private static Dialog mLoadingDialog;
    public static final String TAG = "CameraModeDialog";
    /**
     * 显示加载对话框
     * @param context 上下文
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(final Activity context, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_camera_mode,null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog1);
        Window window = mLoadingDialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        window.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
        attributes.y = 180;
        attributes.x = 18;
        window.setAttributes(attributes);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelable);
        mLoadingDialog.setContentView(view);
        mLoadingDialog.show();
        TextView tv_switch = (TextView) view.findViewById(R.id.tv_switch_camera);
        TextView tv_close_camera = (TextView) view.findViewById(R.id.tv_close_camera);
        MLog.e(TAG,"摄像头状态"+ MyState.getInstance().isOpenCamera);
//        if(MyState.getInstance().isOpenCamera){
//            tv_close_camera.setText("关闭画中画");
//        }else{
//            tv_close_camera.setText("打开画中画");
//        }
        if(MyState.getInstance().isOpenLocal){
            tv_close_camera.setText("关闭摄像头");
        }else{
            tv_close_camera.setText("打开摄像头");
        }
        tv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyState.getInstance().isOpenLocal){
                    RxBus.getInstance().post(AppConstant.RxAction.SWITCH_CAMERA_MODE, true);
                    cancelDialogForLoading();
                }else{
                    ToastUitl.showShort("本地画面已关闭，不可切换摄像头");
                    cancelDialogForLoading();
                }
            }
        });
        tv_close_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(MyState.getInstance().isOpenCamera){
//                    tv_close_camera.setText("打开画中画");
//                    MyState.getInstance().isOpenCamera = false;
//                }else{
//                    tv_close_camera.setText("关闭画中画");
//                    MyState.getInstance().isOpenCamera = true;
//                }
//                RxBus.getInstance().post(AppConstant.RxAction.CLOSE_CAMERA, MyState.getInstance().isOpenCamera);
//                cancelDialogForLoading();
                RxBus.getInstance().post(AppConstant.RxAction.CLOSE_LOCAL, MyState.getInstance().isOpenLocal);
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
