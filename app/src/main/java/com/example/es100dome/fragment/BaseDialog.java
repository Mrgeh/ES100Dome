package com.example.es100dome.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;


/**
 * Created by Bruce
 * on 2017/6/18.
 */

public abstract class BaseDialog extends Dialog {

    View rootView;

    public BaseDialog(Context context) {
        this(context, com.es100.R.style.dialog_full_screen);
    }

    private BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        rootView = LayoutInflater.from(getContext()).inflate(getLayoutRes(), null, false);
        setContentView(rootView);
        initView(rootView);
        LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
    }

    protected abstract int getLayoutRes();

     public abstract void initView(View rootView);
}
