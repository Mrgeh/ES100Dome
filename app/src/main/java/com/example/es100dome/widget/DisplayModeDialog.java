package com.example.es100dome.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.es100.e.DisplayMode;
import com.example.es100dome.DisplayModeAdapter;
import com.example.es100dome.R;

/**
 * @Description : 显示模式选择对话框
 * @Author : BruceChen
 * @Date : 2018/1/20 13:59
 */

public final class DisplayModeDialog extends AppCompatDialog {
    private ModeSelectedListener listener;
    private GridView gridView;

    public DisplayModeDialog(Context context, ModeSelectedListener listener) {
        this(context, com.es100.R.style.dialog2);
        this.listener = listener;
    }

    private DisplayModeDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_display_mode);
        Window window = getWindow();
        if(window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
        gridView = (GridView) findViewById(R.id.gridView);
        DisplayModeAdapter adapter = new DisplayModeAdapter(getContext());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            if(listener != null) {
                if(listener.modeSelected(adapter.getItem(position))) {
                    dismiss();
                }
            }
        });
    }

    public interface ModeSelectedListener {
        boolean modeSelected(DisplayMode mode);
    }
}
