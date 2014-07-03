package com.vivavu.dream.common;

/**
 * Created by yuja on 14. 1. 8.
 */
public class Constants {
    static public final String settings = "settings";
    static public final String email = "email";
    static public final String token = "token";
    static public final String tokenType = "tokenType";

    /* Dev Server */
    static public String url = "http://masunghoon.iptime.org";
    static public String port = "5001";

    /* Real Server */
//    static public String url = "http://54.178.201.197";
//    static public String port = "80";

    /* Real Server Test */
//    static public String url = "http://54.178.201.197";
//    static public String port = "5000";

    static public String baseUrl = url + ":"+port+"/";


    // Authentication
    static public String apiUsers = baseUrl+"api/users";
    static public String apiUserInfo = baseUrl+"api/user/{userId}";
    static public String apiResetPassword = baseUrl+"api/reset_password/{email}";
    static public String apiValidEmail = baseUrl+"api/valid_email/{email}";


    static public String apiPostBucket = baseUrl+"api/buckets/";
    static public String apiBuckets = baseUrl+"api/buckets/user/{userId}";
    static public String apiBucketGroup = baseUrl+"api/test/{userId}";

    //버킷, 등록, 읽기, 수정, 삭제
    static public String apiBucketInfo = baseUrl+"api/bucket/{bkt_id}";
    
    static public String apiPlanList = baseUrl + "api/user/{userid}/today";
    static public String apiTodayListWithPage = baseUrl + "api/user/{userid}/today?page={page}";
    static public String apiPlanInfo = baseUrl + "api/plan/{plan_id}";

    static public String apiToken = baseUrl+"api/token";
    static public String apiBaseInfo = baseUrl+"api/resource";

    static public String apiTimeline = baseUrl+"api/bucket/{bucketId}/timeline";
    static public String apiTimelineWithPage = baseUrl+"api/bucket/{bucketId}/timeline?page={page}";
    static public String apiTimelineForDate = baseUrl+"api/bucket/{bucketId}/timeline?date={date}";
    static public String apiTimelineInfo = baseUrl+"api/content/{postId}";
    static public String apiTimelineMetaInfo = baseUrl+"api/bucket/{bucketId}/timeline/exists";
}
