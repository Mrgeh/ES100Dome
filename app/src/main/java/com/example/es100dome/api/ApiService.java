package com.example.es100dome.api;


import com.es100.entity.Condition;
import com.es100.entity.ConfParamEntity;
import com.es100.entity.ConfSiteWithStick;
import com.es100.entity.ConfTerminalList;
import com.es100.entity.ConfsiteEntity;
import com.es100.entity.ContactResponse;
import com.es100.entity.ContactTreeNameEntity;
import com.es100.entity.CreateConfParam;
import com.es100.entity.E164Name;
import com.es100.entity.EpId;
import com.es100.entity.McuP2PConf;
import com.es100.entity.MeetingControlResponse;
import com.es100.entity.MeetingScheduleListEntity;
import com.es100.entity.MqttEntity;
import com.es100.entity.ParticipantDetail;
import com.es100.entity.PermissionEntity;
import com.es100.entity.QueryMeetingListRequest;
import com.es100.entity.SingleTerminalDetail;
import com.es100.entity.UpdateResponse;
import com.es100.entity.VirtualConfDetail;
import com.es100.entity.VirtualParticipants;
import com.es100.login.LoginPlatformParam;
import com.es100.login.LoginPlatformResponse;
import com.es100.login.UserPwd;
import com.example.es100dome.entity.BaseResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 *
 * @author zhoujin
 * @date 2017/7/3
 */

public interface ApiService {
    /**
     * 登录会管平台
     * @param cacheControl
     * @param loginPlatformParam
     * @return
     */
    @POST("api/terminal/login")
    Observable<LoginPlatformResponse> getLoginPlatformResponse(@Header("Cache-Control") String cacheControl,
                                                               @Body LoginPlatformParam loginPlatformParam);
    /**
     * 获取会议日程
     * @param cacheControl
     * @param size
     * @param queryMeetingListRequest
     * @return
     */
    @POST("api/conference/query")
    Observable<MeetingScheduleListEntity> getMeetingEntity(@Header("Cache-Control") String cacheControl,
                                                           @Header("Authorization") String authorization,
                                                           @Query("size") String size,
                                                           @Body QueryMeetingListRequest queryMeetingListRequest);

    /**
     * 获取组织架构
     * @param cacheControl
     * @param showOption
     * @param hideNullNode
     * @param type
     * @param includeTypeData
     * @return
     */
    @GET("api/user/getMyResourceTree")
    Observable<ContactResponse> getResourceTree(@Header("Cache-Control") String cacheControl,
                                                @Header("Authorization") String authorization,
                                                @Query("showOption") boolean showOption,
                                                @Query("hideNullNode") boolean hideNullNode,
                                                @Query("type") String type,
                                                @Query("includeTypeData") boolean includeTypeData);

    /**
     * 根据组织id获取组织架构
     * @param cacheControl
     * @param organizationId
     * @param showOption
     * @param type
     * @param includeTypeData
     * @return
     */
    @GET("api/user/getMyResourceTree/{organizationId}")
    Observable<ContactResponse> getResourceTreeById(@Header("Cache-Control") String cacheControl,
                                                    @Header("Authorization") String authorization,
                                                    @Path("organizationId") String organizationId,
                                                    @Query("showOption") boolean showOption,
                                                    @Query("type") String type,
                                                    @Query("includeTypeData") boolean includeTypeData);

    /**
     * 修改密码
     * @param cacheControl
     * @param authorization
     * @param userPwd
     * @return
     */
    @POST("api/user/changePassword")
    Observable<BaseResponse> changePwd(@Header("Cache-Control") String cacheControl,
                                       @Header("Authorization") String authorization,
                                       @Body UserPwd userPwd);

    /**
     * 根据e164号获取用户名
     * @param cacheControl
     * @param authorization
     * @param e164
     * @return
     */
    @GET("api/user/getUserByE164/{e164}")
    Observable<E164Name> getUserByE164(@Header("Cache-Control") String cacheControl,
                                       @Header("Authorization") String authorization,
                                       @Path("e164") String e164);

    /**
     * 获取最新版本信息
     * @param cacheControl
     * @param authorization
     * @param machineId
     * @param deviceModel
     * @return
     */
    @GET("api/deviceUpgrade/getLatestVersion")
    Observable<UpdateResponse> getLatestVersion(@Header("Cache-Control") String cacheControl,
                                                @Header("Authorization") String authorization,
                                                @Query("machineId") String machineId,
                                                @Query("deviceModel") String deviceModel);

    /**
     *
     * @param downloadUrl
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(
            @Url String downloadUrl
    );

    /**
     * 邀请用户入会
     * @param cacheControl
     * @param authorization
     * @param confId
     * @param list
     * @return
     */
    @POST("api/conference/inviteUserToConf")
    Observable<BaseResponse> inviteUserToConf(@Header("Cache-Control") String cacheControl,
                                                                             @Header("Authorization") String authorization,
                                                                             @Query("confId") Long confId,
                                                                             @Body ArrayList<Long> list);

    /**
     * 邀请会议室
     * @param cacheControl
     * @param authorization
     * @param confSiteWithSticks
     * @return
     */

    @POST("api/conference/appendConfSite/{conferenceId}")
    Observable<BaseResponse> inviteConfsiteToConf(@Header("Cache-Control") String cacheControl,
                                                                                 @Header("Authorization") String authorization,
                                                                                 @Path("conferenceId") Long conferenceId,
                                                                                 @Body ArrayList<ConfSiteWithStick> confSiteWithSticks);
    /**
     * 根据会议Id获取与会终端列表
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param showOption
     * @return
     */
    @GET("api/conference/getConfSitesTreeByConfId")
    Observable<ConfTerminalList> getConfSitesTreeByConfId(@Header("Cache-Control") String cacheControl,
                                                          @Header("Authorization") String authorization,
                                                          @Query("cmConfId") Long cmConfId,
                                                          @Query("showOption") boolean showOption);

    /**
     * 根据会议号获取与会终端列表
     * @param cacheControl
     * @param authorization
     * @param confCode
     * @return
     */
    @GET("api/conference/getTerminalList")
    Observable<ConfTerminalList> getTerminalList(@Header("Cache-Control") String cacheControl,
                                                 @Header("Authorization") String authorization,
                                                 @Query("confCode") String confCode
    );

    /**
     * 会议控制(MCU代理)
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param control
     * @param epId
     * @return
     */
    @POST("api/mcuproxy/meetingControl")
    Observable<MeetingControlResponse> meetingControl(@Header("Cache-Control") String cacheControl,
                                                      @Header("Authorization") String authorization,
                                                      @Query("cmConfId") long cmConfId,
                                                      @Query("control") String control,
                                                      @Body EpId epId
    );

    /**
     * 结束会议
     * @param cacheControl
     * @param authorization
     * @param id
     * @return
     */
    @POST("api/conference/close/{id}")
    Observable<BaseResponse> closeMeeting(@Header("Cache-Control") String cacheControl,
                                                                         @Header("Authorization") String authorization,
                                                                         @Path("id") long id
    );

    /**
     * 获取会议详情
     * @param cacheControl
     * @param authorization
     * @param id
     * @return
     */
    @GET("api/confsite/getById")
    Observable<ConfsiteEntity> getConfsite(@Header("Cache-Control") String cacheControl,
                                           @Header("Authorization") String authorization,
                                           @Query("id") long id
    );

    /**
     * 获取会议参数模板
     * @param cacheControl
     * @param authorization
     * @param condition
     * @return
     */
    @POST("api/confParamTmpl/find?size=20&sort=seq,ASC")
    Observable<ConfParamEntity> getConParam(@Header("Cache-Control") String cacheControl,
                                            @Header("Authorization") String authorization,
                                            @Body Condition condition
    );

    /**
     * 添加会议
     * @param cacheControl
     * @param authorization
     * @param createConfParam
     * @return
     */
        @POST("api/conference/add")
    Observable<BaseResponse> addConf(@Header("Cache-Control") String cacheControl,
                                     @Header("Authorization") String authorization,
                                     @Body CreateConfParam createConfParam
        );

    /**
     * 删除会议
     * @param cacheControl
     * @param authorization
     * @param ids
     * @return
     */
    @POST("api/conference/drop")
    Observable<BaseResponse> dropConf(@Header("Cache-Control") String cacheControl,
                                      @Header("Authorization") String authorization,
                                      @Body List<Long> ids
    );

    /**
     * 根据会议id获取还未开启的会议
     * @param cacheControl
     * @param authorization
     * @param id
     * @return
     */
    @GET("api/conference/getParticipantDetail/{id}")
    Observable<ParticipantDetail> getParticipantDetail(@Header("Cache-Control") String cacheControl,
                                                       @Header("Authorization") String authorization,
                                                       @Path("id") long id
    );

    /**
     * 会议中移除与会人
     * @param cacheControl
     * @param authorization
     * @param confId
     * @param userIds
     * @return
     */
    @POST("api/conference/removeParticipants")
    Observable<BaseResponse> removeParticipants(@Header("Cache-Control") String cacheControl,
                                                @Header("Authorization") String authorization,
                                                @Query("confId") long confId,
                                                @Body List<Long> userIds
    );


    /**
     * 获取我的功能权限
     * @param cacheControl
     * @param authorization
     * @return
     */
    @GET("api/user/getMyPermissions")
    Observable<PermissionEntity> getMyPermissions(@Header("Cache-Control") String cacheControl,
                                                  @Header("Authorization") String authorization
    );
    /**
     * 获取虚拟会议室与会列表
     * @param cacheControl
     * @param authorization
     * @return
     */
    @GET("api/conference/get/{id}")
    Observable<VirtualParticipants> getVirtualList(@Header("Cache-Control") String cacheControl,
                                                   @Header("Authorization") String authorization,
                                                   @Path("id") long confId
    );

    /**
     * 获取虚拟会议室详情
     * @param cacheControl
     * @param authorization
     * @param confId
     * @return
     */
    @GET("api/conference/update/{id}")
    Observable<VirtualConfDetail> getVirtualDetail(@Header("Cache-Control") String cacheControl,
                                                   @Header("Authorization") String authorization,
                                                   @Path("id") long confId
    );

    /**
     * 更新会议室
     * @param cacheControl
     * @param authorization
     * @param createConfParam
     * @return
     */
    @POST("api/conference/update")
    Observable<BaseResponse> updateVirtual(@Header("Cache-Control") String cacheControl,
                                           @Header("Authorization") String authorization,
                                           @Body CreateConfParam createConfParam
    );
//
//    /**
//     * 语音激励
//     * @param cacheControl
//     * @param authorization
//     * @param cmConfId
//     * @return
//     */
//    @POST("api/mcuproxy/meetingControl")
//    Observable<BaseResponse> freeDiscussion(@Header("Cache-Control") String cacheControl,
//                                            @Header("Authorization") String authorization,
//                                            @Query("cmConfId") long cmConfId,
//                                            @Query("control") String control
//
//    );

    /**
     * 自由讨论
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param isExcitation
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/freeDiscussion")
    Observable<BaseResponse> freeDiscussion(@Header("Cache-Control") String cacheControl,
                                            @Header("Authorization") String authorization,
                                            @Path("cmConfId") long cmConfId,
                                            @Query("isExcitation") boolean isExcitation

    );

    /**
     * 会议中移除与会人
     * @param cacheControl
     * @param authorization
     * @param conferenceId
     * @param epId
     * @return
     */
    @GET("api/conference/removeConfSiteInConf/{conferenceId}/{epId}")
    Observable<BaseResponse> removeConfSiteInConf(@Header("Cache-Control") String cacheControl,
                                                  @Header("Authorization") String authorization,
                                                  @Path("conferenceId") long conferenceId,
                                                  @Path("epId") String epId);

    /**
     * 获取mqtt配置信息
     * @param cacheControl
     * @param authorization
     * @param type
     * @return
     */
    @GET("api/systemServiceConfig/querySystemConfigMap")
    Observable<MqttEntity> getMqttConfigure(@Header("Cache-Control") String cacheControl,
                                            @Header("Authorization") String authorization,
                                            @Query("type") String type
    );

    /**
     * 审批申请发言
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param epID
     * @param isAgree
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/approvalspeakreq/{epID}")
    Observable<BaseResponse> approvalspeakreq(@Header("Cache-Control") String cacheControl,
                                              @Header("Authorization") String authorization,
                                              @Path("cmConfId") long cmConfId,
                                              @Path("epID") String epID,
                                              @Body Integer isAgree
    );

    /**
     * 一键主讲
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/oneKeynote")
    Observable<BaseResponse> oneKeynote(@Header("Cache-Control") String cacheControl,
                                        @Header("Authorization") String authorization,
                                        @Path("cmConfId") long cmConfId
    );

    /**
     * 取消发言
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param epID
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/cancelSpeakSite/{epID}")
    Observable<BaseResponse> cancelSpeakSite(@Header("Cache-Control") String cacheControl,
                                             @Header("Authorization") String authorization,
                                             @Path("cmConfId") long cmConfId,
                                             @Path("epID") String epID
    );

    /**
     * 设置主讲
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param epID
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/setChairman/{epID}")
    Observable<BaseResponse> setChairman(@Header("Cache-Control") String cacheControl,
                                         @Header("Authorization") String authorization,
                                         @Path("cmConfId") long cmConfId,
                                         @Path("epID") String epID
    );

    /**
     * 取消主讲
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param epID
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/cancelChairman/{epID}")
    Observable<BaseResponse> cancelChairman(@Header("Cache-Control") String cacheControl,
                                            @Header("Authorization") String authorization,
                                            @Path("cmConfId") long cmConfId,
                                            @Path("epID") String epID
    );

    /**
     *全部闭音
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/muteAll")
    Observable<BaseResponse> muteAll(@Header("Cache-Control") String cacheControl,
                                     @Header("Authorization") String authorization,
                                     @Path("cmConfId") long cmConfId
    );

    /**
     * 取消全部闭音
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @return
     */
    @POST("api/confcontrol/{cmConfId}/demuteAll")
    Observable<BaseResponse> demuteAll(@Header("Cache-Control") String cacheControl,
                                       @Header("Authorization") String authorization,
                                       @Path("cmConfId") long cmConfId
    );

    /**
     * 会议延时
     * @param cacheControl
     * @param authorization
     * @param conferenceId
     * @param seconds
     * @return
     */
    @POST("api/conference/stretchDuration/{conferenceId}/{seconds}")
    Observable<BaseResponse> stretchDuration(@Header("Cache-Control") String cacheControl,
                                             @Header("Authorization") String authorization,
                                             @Path("conferenceId") long conferenceId,
                                             @Path("seconds") int seconds
    );

    /**
     * 点对点会议根据会议号获取会议信息
     * @param cacheControl
     * @param authorization
     * @param confCode
     * @return
     */
    @POST("/api/McuP2pConference/info/{confCode}")
    Observable<McuP2PConf> McuP2pConference(@Header("Cache-Control") String cacheControl,
                                            @Header("Authorization") String authorization,
                                            @Path("confCode") String confCode
    );

    /**
     * 获取某一会场详情
     * @param cacheControl
     * @param authorization
     * @param cmConfId
     * @param epID
     * @return
     */
    @POST("/api/confcontrol/{cmConfId}/getConventionerInfo/{epID}")
    Observable<SingleTerminalDetail> getConventionerInfo(@Header("Cache-Control") String cacheControl,
                                                         @Header("Authorization") String authorization,
                                                         @Path("cmConfId") long cmConfId,
                                                         @Path("epID") String epID
    );
    /**
     * 获取组织架构根节点
     * @param cacheControl
     * @param authorization
     * @param id
     * @return
     */
    @GET("api/organization/get?")
    Observable<ContactTreeNameEntity> getTreeName(@Header("Cache-Control") String cacheControl,
                                                  @Header("Authorization") String authorization,
                                                  @Query("id") String id);

}
