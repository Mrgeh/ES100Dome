package com.example.es100dome.presenter;

import com.es100.login.LoginPlatformParam;
import com.es100.login.LoginPlatformResponse;
import com.es100.util.LoginSubscriber;
import com.example.es100dome.contract.LoginContract;


/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class GetLoginResponsePresenter extends LoginContract.Presenter {
    @Override
    public void loginPlatformResponseRequest(LoginPlatformParam loginPlatformParam) {
        mRxManage.add(mModel.getLoginPlatformResponse(loginPlatformParam).subscribe(new LoginSubscriber<LoginPlatformResponse>(mContext, false) {
            @Override
            protected void _onNext(LoginPlatformResponse loginPlatformResponseBaseResponse) {
                mView.returnLoginPlatformResponse(loginPlatformResponseBaseResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }


}

