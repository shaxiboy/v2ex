package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.MemberOld;
import com.hjx.v2ex.entity.NodeOld;
import com.hjx.v2ex.entity.ReplyOld;
import com.hjx.v2ex.entity.TopicOld;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public interface V2EXService {

    String BASE_URL = "https://www.v2ex.com/";
    int PAGE_TOPICS_ITEM_NUM = 20;
    int PAGE_TOPIC_REPLIES_ITEM_NUM = 100;

    //访问tab标签对应的主页
    @GET(".")
    Call<ResponseBody> vistHomePage(@Query("tab") String tab);

    //访问某个节点的主题列表页
    @GET("go/{node}")
    Call<ResponseBody> vistNodeTopicsPage(@Path("node") String nodeName, @Query("p") int page);

    //访问某个会员的主题列表页
    @GET("member/{member}/topics")
    Call<ResponseBody> vistMemberTopicsPage(@Path("member") String memberName, @Query("p") int page);

    //访问主题详情页
    @GET("t/{id}")
    Call<ResponseBody> vistTopicPage(@Path("id") int topicID, @Query("p") int page);

    //访问全部节点页面
    @GET("planes")
    Call<ResponseBody> visitPlanes();
}
