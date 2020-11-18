package com.example.es100dome.model;

import com.es100.MyState;
import com.es100.bace.BaseModel;
import com.es100.baserx.RxSchedulers;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.entity.QueryMeetingListRequest;
import com.es100.login.LoginPlatformResponse;
import com.example.es100dome.MyApp;
import com.example.es100dome.api.Api;
import com.example.es100dome.contract.ConfListContract;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class ConfListModel implements ConfListContract.Model, BaseModel {
    @Override
    public Observable<MeetingScheduleListEntity> getMeetingEntity(String size, QueryMeetingListRequest queryMeetingListRequest) {
        return Api.getDefault(MyApp.getAppContext(), MyState.getInstance().token,MyApp.getHostUrl())
                .getMeetingEntity(Api.getCacheControl(), MyState.getInstance().token,size,queryMeetingListRequest)
                .map(new Func1<MeetingScheduleListEntity, MeetingScheduleListEntity>() {
                    @Override
                    public MeetingScheduleListEntity call(MeetingScheduleListEntity meetingScheduleListEntity) {
                        return meetingScheduleListEntity;
                    }
                })
                .compose(RxSchedulers.<MeetingScheduleListEntity>io_main());
    }
}
