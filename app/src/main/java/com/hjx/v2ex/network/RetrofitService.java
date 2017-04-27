package com.hjx.v2ex.network;

import com.hjx.v2ex.bean.FavoriteMembers;
import com.hjx.v2ex.bean.FavoriteNodes;
import com.hjx.v2ex.bean.MemberFavoriteResult;
import com.hjx.v2ex.bean.NodeFavoriteResult;
import com.hjx.v2ex.bean.TopicFavoriteResult;
import com.hjx.v2ex.bean.HomePage;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.MemberTopicReplies;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.bean.NodesPlane;
import com.hjx.v2ex.bean.SigninParams;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.bean.SignoutResult;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.bean.V2EXMoreInfo;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by shaxiboy on 2017/3/2 0002.
 */

public interface RetrofitService {

    String BASE_URL = "https://www.v2ex.com/";
    int PAGE_TOPICS_ITEM_NUM = 20;
    int PAGE_TOPIC_REPLIES_ITEM_NUM = 100;


    //get请求
    @GET
    Call<ResponseBody> doGet(@Url String url);

    //关于页面
    @GET("about")
    Call<V2EXMoreInfo> aboutPage();

    //tab标签对应的主页
    @GET(".")
    Call<ResponseBody> homePageOld(@Query("tab") String tab);

    //tab标签对应的主页
    @GET(".")
    Call<HomePage> homePage(@Query("tab") String tab);

    //获取tab标签下的主题
    @GET(".")
    Call<TopicsPageData> getTabTopics(@Query("tab") String tab);

    //全部主题列表页
    @GET("recent")
    Call<ResponseBody> allTopicsPage(@Query("p") Integer page);

    //主题详情页
    @GET("t/{id}")
    Call<ResponseBody> topicDetailsPageOld(@Path("id") int topicID, @Query("p") Integer page);

    //主题详情页
    @GET("t/{id}")
    Call<TopicPage> topicPage(@Path("id") int topicID, @Query("p") Integer page);

    //全部节点页面
    @GET("planes")
    Call<NodesPlane> allNodesPage();

    //节点详情页
    @GET("go/{node}")
    Call<NodePage> nodeDetailsPage(@Path("node") String nodeName);

    //获取节点下的主题
    @GET("go/{node}")
    Call<TopicsPageData> getNodeTopics(@Path("node") String nodeName, @Query("p") int page);

    //会员详情页
    @GET("member/{membername}")
    Call<Member> memberDetailsPage(@Path("membername") String memberName);

    //会员发表的主题列表页
    @GET("member/{membername}/topics")
    Call<TopicsPageData> getMemberTopics(@Path("membername") String memberName, @Query("p") int page);

    //会员发表的主题回复列表页
    @GET("member/{membername}/replies")
    Call<MemberTopicReplies> getMemberTopicReplies(@Path("membername") String memberName, @Query("p") int page);

    //登陆页面
    @GET("signin")
    Call<SigninParams> signin();

    //登陆
    @Headers({
            "Referer: https://www.v2ex.com/signin"
    })
    @POST("signin")
    @FormUrlEncoded
    Call<SigninResult> signin(@FieldMap Map<String, String> map);

    //登出
    @Headers({
            "referer: https://www.v2ex.com"
    })
    @GET("signout")
    Call<SignoutResult> signout(@Query("once") int sessionId);

    //收藏主题、取消收藏主题
    @GET()
    Call<TopicFavoriteResult> favoriteTopic(@Url String url, @Header("referer") String referer);

    //我收藏的主题列表页面
    @GET("my/topics")
    Call<TopicsPageData> getFavoriteTopics(@Query("p") int page);

    //关注会员、取消关注会员
    @GET()
    Call<MemberFavoriteResult> favoriteMember(@Url String url, @Header("referer") String referer);

    //我关注的人
    @GET("my/following")
    Call<FavoriteMembers> getFavoriteMembersTopics();

    //我关注的人发表的主题
    @GET("my/following")
    Call<TopicsPageData> getFavoriteMembersTopics(@Query("p") int page);

    //收藏节点、取消收藏节点
    @GET()
    Call<NodeFavoriteResult> favoriteNode(@Url String url, @Header("referer") String referer);

    //我收藏的节点页面
    @GET("my/nodes")
    Call<FavoriteNodes> getFavoriteNodes();

    //感谢主题
    @POST("thank/topic/{id}")
    @FormUrlEncoded
    Call<ResponseBody> thankTopic(@Path("id") int topicID, @Field("t") String token);

}
