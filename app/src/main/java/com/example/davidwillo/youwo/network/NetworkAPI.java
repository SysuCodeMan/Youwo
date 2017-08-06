package com.example.davidwillo.youwo.network;

import com.example.davidwillo.youwo.network.model.LifeAccount;
import com.example.davidwillo.youwo.network.model.LifeExpress;
import com.example.davidwillo.youwo.network.model.StepRecord;
import com.example.davidwillo.youwo.network.model.Course;
import com.example.davidwillo.youwo.network.model.Homework;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by davidwillo on 6/4/17.
 */

public interface NetworkAPI {


    @FormUrlEncoded
    @POST("/user/login")
    Observable<HttpResult<String>> login(@Field("userName") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("/user/register")
    Observable<HttpResult<String>> register(@Field("userName") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("/user/modifypassword")
    Observable<HttpResult<String>> modifypassword(@Field("userName") String userName, @Field("oldPassword") String oldPssword, @Field("newPassword") String newPssword);

    @FormUrlEncoded
    @POST("/sport/uploadStepRecord")
    Observable<HttpResult<String>> uploadStepRecord(@Field("userName") String userName,@Field("stepRecordList") String stepRecordList);

    @FormUrlEncoded
    @POST("/sport/downloadStepRecord")
    Observable<HttpResult<List<StepRecord>>> downloadStepRecord(@Field("userName") String userName);


    @FormUrlEncoded
    @POST("/life/uploadLifeAccount")
    Observable<HttpResult<String>> uploadLifeAccount(@Field("userName") String userName,@Field("lifeAccountList") String lifeAccountList);

    @FormUrlEncoded
    @POST("/life/downloadLifeAccount")
    Observable<HttpResult<List<LifeAccount>>> downloadLifeAccount(@Field("userName") String userName);

//    @FormUrlEncoded
//    @POST("/life/uploadLifeExpress")
//    Observable<HttpResult<String>> uploadLifeExpress(@Field("userName") String userName,@Field("lifeExpressList") String lifeExpressList);
//
//    @GET("/life/downloadLifeExpress")
//    Observable<HttpResult<List<LifeExpress>>> downloadLifeExpress(@Query("userName") String userName);

    @FormUrlEncoded
    @POST("/study/uploadCourseRecord")
    Observable<HttpResult<String>> uploadCourseRecord(@Field("userName") String userName,@Field("courseRecordList") String courseRecordList);

    @FormUrlEncoded
    @POST("/study/downloadCourseRecord")
    Observable<HttpResult<List<Course>>> downloadCourseRecord(@Field("userName") String userName);

    @FormUrlEncoded
    @POST("/study/uploadHomeworkRecord")
    Observable<HttpResult<String>> uploadHomeworkRecord(@Field("userName") String userName,@Field("homeworkRecordList") String homeworkRecordList);
    @FormUrlEncoded
    @POST("/study/downloadHomeworkRecord")
    Observable<HttpResult<List<Homework>>> downloadHomeworkRecord(@Field("userName") String userName);

}
