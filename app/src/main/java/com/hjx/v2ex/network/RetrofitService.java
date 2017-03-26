package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.MemberOld;
import com.hjx.v2ex.entity.NodeOld;
import com.hjx.v2ex.entity.ReplyOld;
import com.hjx.v2ex.entity.TopicOld;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public interface RetrofitService {

    String BASE_URL = "https://www.v2ex.com/";
    int PAGE_TOPICS_ITEM_NUM = 20;
    int PAGE_TOPIC_REPLIES_ITEM_NUM = 100;

    //tab标签对应的主页
    @GET(".")
    Call<ResponseBody> homePage(@Query("tab") String tab);

    //全部主题列表页
    @GET("recent")
    Call<ResponseBody> allTopicsPage(@Query("p") int page);

    //某个节点的主题列表页
    @GET("go/{node}")
    Call<ResponseBody> nodeTopicsPage(@Path("node") String nodeName, @Query("p") int page);

    //某个会员的主题列表页
    @GET("member/{member}/topics")
    Call<ResponseBody> memberTopicsPage(@Path("member") String memberName, @Query("p") int page);

    //主题详情页
    @GET("t/{id}")
    Call<ResponseBody> topicPage(@Path("id") int topicID, @Query("p") int page);

    //全部节点页面
    @GET("planes")
    Call<ResponseBody> allNodes();

    //登陆页面
    @GET("signin")
    Call<ResponseBody> signin();

    //登陆
    @FormUrlEncoded
    @POST("signin")
    @Headers({
            "Referer: https://www.v2ex.com/signin"
    })
    Call<ResponseBody> signin(@FieldMap Map<String, String> map);

    //登出
    @GET("signout")
    @Headers({
            "Referer: https://www.v2ex.com"
    })
    Call<ResponseBody> signout(@Query("once") int sessionId);
}
