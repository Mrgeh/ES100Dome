package com.example.es100dome.widget;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.es100.MstManager;
import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.baserx.RxManager;
import com.es100.entity.ConfTerminalList;
import com.example.es100dome.ConfMemberAdapter;
import com.example.es100dome.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


/**
 * @author Administrator
 */
public class ConfMemberDialog extends DialogFragment {
    public static final String TAG = "ConfMemberDialog";
    private RxManager rxManager;
    private List<ConfTerminalList.ConfTerminal> data = new ArrayList<>();
    private ConfMemberAdapter confMemberAdapter;
    private Button button;
    private RecyclerView recyclerView;
    private TextView tv_member_num;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rxManager = new RxManager();
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_conf_memeber, null);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.bg_dialog)));
        getDialog().getWindow().setDimAmount(0.1f);
        button = (Button) contentView.findViewById(R.id.bt_apply_chairman);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.rcy_conf_member);
        tv_member_num = (TextView) contentView.findViewById(R.id.tv_member_num);
        tv_member_num.setText("与会人员"+ MyState.getInstance().terminalNum);
        data.clear();
        data.addAll(MyState.getInstance().confTerminalArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        confMemberAdapter = new ConfMemberAdapter(data, getActivity());
        recyclerView.setAdapter(confMemberAdapter);
        rxManager.on(AppConstant.RxAction.UPDATE_TERMINAL, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                data.clear();
                data.addAll(MyState.getInstance().confTerminalArrayList);
                confMemberAdapter.notifyDataSetChanged();
            }
        });
        rxManager.on(AppConstant.RxAction.TERMINAL_NUM, new Action1<String>() {
            @Override
            public void call(String s) {
                tv_member_num.setText("与会人员"+ MyState.getInstance().terminalNum);
            }
        });
        button.setOnClickListener(v -> MstManager.getInstance().sendCmd().applyChairman(MyState.getInstance().callId));
        return contentView;
    }
    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.dimAmount = 0.0f;
        window.setGravity(Gravity.RIGHT);
//        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.BottomDialog_Animation1);
        window.setAttributes((WindowManager.LayoutParams) wlp);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( getDialog().getWindow().getAttributes().width, dm.heightPixels );
    }
}
