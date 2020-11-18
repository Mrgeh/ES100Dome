package com.example.es100dome;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.es100.AppConfig;
import com.es100.MyState;
import com.es100.app.AppConstant;
import com.es100.bace.BaseActivity;
import com.es100.baserx.RxBus;
import com.es100.baserx.RxManager;
import com.es100.e.QueryMeetingType;
import com.es100.entity.Confrence;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.entity.QueryMeetingListRequest;
import com.es100.jni.MstApp;
import com.es100.util.Const;
import com.example.es100dome.contract.ConfListContract;
import com.example.es100dome.contract.MainActivityMessageControl;
import com.example.es100dome.model.ConfListModel;
import com.example.es100dome.presenter.ConfListPresenter;
import com.ifreecomm.debug.MLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * Created by Administrator on 2018-11-5.
 */

public class MeetingActivity extends BaseActivity<ConfListPresenter, ConfListModel> implements ConfListContract.View {
    public static final String TAG = "MeetActivity";
    @Bind(R.id.meeting_swipeRefreshLayout)
    SwipeRefreshLayout meeting_swipeRefreshLayout;
    @Bind(R.id.rcy_meeting_schedule)
    RecyclerView rcyMeetingSchedule;
    @Bind(R.id.rl_empty)
    RelativeLayout rl_emty;
    private MeetingScheduleAdapter meetingScheduleAdapter;
    private List<MeetingScheduleListEntity.MeetingSchedule> data = new ArrayList<>();
    private MyState sta;
    private MainActivityMessageControl messageControl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_meeting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageControl = new MainActivityMessageControl(this, mRxManager);
        messageControl.addMessage();
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {

        mRxManager.on("JoinMeeting", (Action1<Confrence>) confrence -> {
            /**
             * @// TODO: 2018/1/19 0019
             */
            sta.callType = 0;
            sta.StartCall = true;
            sta.callIp = null;
            sta.E164orName = confrence.getConfCode();
            sta.h323callName = confrence.getConfName();
            sta.callIn = 0;

            sta.showName = confrence.getConfName();
            sta.callText = confrence.getConfCode();
            sta.myCallType = 0;
            MLog.e(TAG,"会议号"+ confrence.getConfCode()+"呼叫类型"+AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0));
            MstApp.getInstance().uISendMsg.Call(confrence.getConfCode(),null,0,null, AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0),sta.callType);
            RxBus.getInstance().post(AppConstant.RxAction.IS_SHOW_CALL_WAIT_DIALOG,true);
        });
        sta = MyState.getInstance();
        rcyMeetingSchedule.setLayoutManager(new LinearLayoutManager(mContext));
        data.clear();
        meetingScheduleAdapter = new MeetingScheduleAdapter(data,mContext);
        rcyMeetingSchedule.setAdapter(meetingScheduleAdapter);
        mPresenter.meetingEntityResponseRequest("500", new QueryMeetingListRequest(QueryMeetingType.AllConfOfCurrentUser));

        meeting_swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                normalList.clear();
                mPresenter.meetingEntityResponseRequest("500",new QueryMeetingListRequest(QueryMeetingType.AllConfOfCurrentUser));
            }
        });
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void returnMeetingEntityResponse(MeetingScheduleListEntity meetingScheduleListEntity) {
        Log.e(TAG,"返回的会议数据"+meetingScheduleListEntity.toString());
        data.clear();
        if(meetingScheduleListEntity.data.content.size()!=0){
            for (MeetingScheduleListEntity.MeetingSchedule meetingSchedule:meetingScheduleListEntity.data.content){
                data.add(meetingSchedule);
//                if(meetingSchedule.fireType.equals("Virtual")){
//                    virtuallList.add(meetingSchedule);
//                }else {
//                    normalList.add(meetingSchedule);
//                }
            }
        }
        if(meeting_swipeRefreshLayout.isRefreshing()){
            meeting_swipeRefreshLayout.setRefreshing(false);
        }
        meetingScheduleAdapter.upDateList(data);
        if(data.size()==0){
            rl_emty.setVisibility(View.VISIBLE);
        }else{
            rl_emty.setVisibility(View.GONE);
        }
    }
//    private MyState sta;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_meeting);
//        sta = MyState.getInstance();
//        this.findViewById(R.id.bt_go).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //一键入会
//                sta.callType = 0;
//                sta.StartCall = true;
//                sta.callIp = null;
//                sta.E164orName = "123";
//                sta.h323callName = "sdk调试勿删勿动 ";
//                sta.callIn = 0;
//
//                sta.showName = "sdk调试勿删勿动 ";
//                sta.callText = "123";
//                sta.myCallType = 0;
//                MstApp.getInstance().uISendMsg.Call("123",null,0,null, AppConfig.getInstance().getInt(Const.CALL_PROTOCAL, 0),sta.callType);
//            }
//        });
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageControl.onDestroy();
        messageControl = null;
    }
}