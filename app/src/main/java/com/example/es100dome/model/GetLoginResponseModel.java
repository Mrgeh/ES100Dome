package com.example.es100dome.model;

import com.es100.App;
import com.es100.MyState;
import com.es100.baserx.RxSchedulers;
import com.es100.login.LoginPlatformParam;
import com.es100.login.LoginPlatformResponse;
import com.example.es100dome.MyApp;
import com.example.es100dome.api.Api;
import com.example.es100dome.contract.LoginContract;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class GetLoginResponseModel implements LoginContract.Model {
    @Override
    public Observable<LoginPlatformResponse> getLoginPlatformResponse(LoginPlatformParam loginPlatformParam) {
        return Api.getDefault( MyApp.getAppContext(), MyState.getInstance().token,MyApp.getHostUrl())
                .getLoginPlatformResponse(Api.getCacheControl(),loginPlatformParam)
                .map(new Func1<LoginPlatformResponse, LoginPlatformResponse>() {
                    @Override
                    public LoginPlatformResponse call(LoginPlatformResponse loginPlatformResponse) {
                        return loginPlatformResponse;
                    }
                }).compose(RxSchedulers.<LoginPlatformResponse>io_main());
    }

}
