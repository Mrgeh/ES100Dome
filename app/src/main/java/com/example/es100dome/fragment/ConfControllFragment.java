package com.example.es100dome.fragment;


import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es100.MstManager;
import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.bace.BaseFragment;
import com.es100.baserx.RxBus;
import com.example.es100dome.R;
import com.example.es100dome.widget.ChooseDurationDialog;

import butterknife.Bind;
import ch.ielse.view.SwitchView;

/**
 * Created by Administrator on 2018/2/28 0028.
 */

public class ConfControllFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.bt_release_chairman)
    Button bt_release_chairman;
    @Bind(R.id.switch_free_discuss)
    SwitchView switch_free_discuss;
    @Bind(R.id.tv_mute_all)
    TextView tv_mute_all;
    @Bind(R.id.tv_demute_all)
    TextView tv_demute_all;
    @Bind(R.id.ll_stretch_duration)
    LinearLayout ll_stretch_duration;
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_conf_controll;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        bt_release_chairman.setOnClickListener(this);
        tv_mute_all.setOnClickListener(this);
        tv_demute_all.setOnClickListener(this);
        ll_stretch_duration.setOnClickListener(this);
//        bt_end_meeting.setOnClickListener(this);
        if(MyState.getInstance().isBeginFreeDisscss){
            switch_free_discuss.setOpened(true);
        }else{
            switch_free_discuss.setOpened(false);
        }
        if(MyState.getInstance().isHost){
            bt_release_chairman.setText("结束会议");
        }else if((!MyState.getInstance().isHost)&& MyState.getInstance().isChairman){
            bt_release_chairman.setText("释放主讲");
        }
        switch_free_discuss.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                switch_free_discuss.setOpened(true);
                RxBus.getInstance().post(AppConstant.RxAction.BEGIN_FREE_DISCUSS, true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                switch_free_discuss.setOpened(false);
                RxBus.getInstance().post(AppConstant.RxAction.END_FREE_DISCUSS,true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_release_chairman:
                if(MyState.getInstance().isHost){
                    MyState.getInstance().isNormalEnd = true;
                    RxBus.getInstance().post(AppConstant.RxAction.END_MEETING, MyState.getInstance().confId);
                }else if((!MyState.getInstance().isHost)&& MyState.getInstance().isChairman){
                    MstManager.getInstance().sendCmd().releaseChairman(MyState.getInstance().callId);
                    MyState.getInstance().isChairman  = false;
                }

                break;
//            case R.id.bt_end_meeting:
//                RxBus.getInstance().post(AppConstant.RxAction.END_MEETING,MyState.getInstance().confId);
//                break;
            case R.id.tv_mute_all:
//                tv_mute_all.setBackgroundResource(R.drawable.shape_mute_all);
//                tv_demute_all.setBackgroundResource(R.drawable.shape_demute_all);
                RxBus.getInstance().post(AppConstant.RxAction.MUTE_ALL,true);
                break;
            case R.id.tv_demute_all:
//                tv_mute_all.setBackgroundResource(R.drawable.shape_demute_all);
//                tv_demute_all.setBackgroundResource(R.drawable.shape_mute_all);
                RxBus.getInstance().post(AppConstant.RxAction.DEMUTE_ALL,true);
                break;
            case R.id.ll_stretch_duration:
                ChooseDurationDialog.showDialogForLoading(getActivity(),true);
                break;
            default:
                break;
        }
    }
}
