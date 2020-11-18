package com.example.es100dome.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.bace.BaseFragment;
import com.es100.entity.ConfTerminalList;
import com.example.es100dome.ConfMemberAdapter1;
import com.example.es100dome.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * Created by Administrator on 2018/2/28 0028.
 */

public class ConfMemberFragment extends BaseFragment {
    @Bind(R.id.bt_invite)
    Button bt_invite;
    @Bind(R.id.rcy_conf_member)
    RecyclerView rcy_conf_member;
    private List<ConfTerminalList.ConfTerminal> data = new ArrayList<>();
    public static final String TAG = "ConfMemberFragment";
    private ConfMemberAdapter1 confMemberAdapter1;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_conf_member;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {

        data.clear();
        data.addAll(MyState.getInstance().confTerminalArrayList);
        rcy_conf_member.setLayoutManager(new LinearLayoutManager(getActivity()));
        confMemberAdapter1 = new ConfMemberAdapter1(data, getActivity());
        rcy_conf_member.setAdapter(confMemberAdapter1);
        mRxManager.on(AppConstant.RxAction.UPDATE_TERMINAL, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                data.clear();
                data.addAll(MyState.getInstance().confTerminalArrayList);
                confMemberAdapter1.notifyDataSetChanged();
            }
        });
    }
}
