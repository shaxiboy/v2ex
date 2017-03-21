package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public interface V2EXService {

    public static final String BASE_URL = "https://www.v2ex.com/";

    //取用户信息
    @GET("api/members/show.json")
    Call<Member> getMember(@Query("username") String username);

    //取单个主题信息
    @GET("api/topics/show.json")
    Call<List<Topic>> getTopic(@Query("id") int id);

    //取最新主题
    @GET("api/topics/latest.json")
    Call<List<Topic>> getLatestTopics();

    //取热议主题
    @GET("api/topics/hot.json")
    Call<List<Topic>> getHotTopics();

    //根据提供信息取主题，参数选其一
    @GET("api/topics/show.json")
    Call<List<Topic>> getTopics(@Query("username") String username, @Query("node_id") Integer nodeId, @Query("node_name") String nodeName);

    //取主题回复，topic_id必选
    @GET("api/replies/show.json")
    Call<List<Reply>> getReplies(@Query("topic_id") int topicId, @Query("page") Integer page, @Query("page_size") Integer pageSize);

    //取主题回复，从网页中抓取内容
    @GET("t/{topicId}")
    Call<ResponseBody> getRepliesFromHTML(@Path("topicId") int topicId, @Query("p") int page);

    //取单个节点信息，参数选其一
    @GET("api/nodes/show.json")
    Call<Node> getNode(@Query("id") Integer id, @Query("name") String name);

    //取所有节点
    @GET("api/nodes/all.json")
    Call<List<Node>> getAllNodes();

    //取tab标签对应的主题，从网页中抓取内容
    @GET(".")
    Call<ResponseBody> getTopicsFromHTML(@Query("tab") String tab);
}
