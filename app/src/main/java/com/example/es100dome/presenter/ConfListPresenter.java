package com.example.es100dome.presenter;

import com.es100.baserx.RxSubscriber;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.entity.QueryMeetingListRequest;
import com.example.es100dome.contract.ConfListContract;


/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class ConfListPresenter extends ConfListContract.Presenter {
    @Override
    public void meetingEntityResponseRequest(String size, QueryMeetingListRequest queryMeetingListRequest) {
        mRxManage.add(mModel.getMeetingEntity(size,queryMeetingListRequest).subscribe(new RxSubscriber<MeetingScheduleListEntity>(mContext,false) {
            @Override
            protected void _onNext(MeetingScheduleListEntity meetingScheduleListEntity) {
                mView.returnMeetingEntityResponse(meetingScheduleListEntity);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
