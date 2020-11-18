package com.example.es100dome.contract;


import com.es100.bace.BaseModel;
import com.es100.bace.BasePresenter;
import com.es100.bace.BaseView;
import com.es100.login.LoginPlatformParam;
import com.es100.login.LoginPlatformResponse;

import rx.Observable;

/**
 * Created by panda on 2017/7/7.
 */

public interface LoginContract {
    interface Model  extends BaseModel {
        Observable<LoginPlatformResponse> getLoginPlatformResponse(LoginPlatformParam loginPlatformParam);
    }
    interface View extends BaseView {
        void returnLoginPlatformResponse(LoginPlatformResponse loginPlatformResponse);
    }
    abstract class Presenter extends BasePresenter<View,Model> {
        public abstract void loginPlatformResponseRequest(LoginPlatformParam loginPlatformParam);
    }
}
