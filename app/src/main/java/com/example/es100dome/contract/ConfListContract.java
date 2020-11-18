package com.example.es100dome.contract;


import com.es100.bace.BasePresenter;
import com.es100.bace.BaseView;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.entity.QueryMeetingListRequest;

import rx.Observable;


/**
 * Created by panda on 2017/7/7.
 */

public interface ConfListContract {
    interface Model {
        Observable<MeetingScheduleListEntity> getMeetingEntity(String size, QueryMeetingListRequest queryMeetingListRequest);
    }
    interface View extends BaseView {
        void returnMeetingEntityResponse(MeetingScheduleListEntity meetingScheduleListEntity);
    }
    abstract class Presenter extends BasePresenter<View,Model> {
        public abstract void meetingEntityResponseRequest(String size, QueryMeetingListRequest queryMeetingListRequest);
    }
}
