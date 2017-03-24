package com.hjx.v2ex.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shaxiboy on 2017/3/3 0003.
 */

public class TopicOld implements Serializable {

    /**
     * id : 344419
     * title : 给 qingcloud 反馈一个免费创建主机的 bug，得到了 20 元感谢费
     * url : http://www.v2ex.com/t/344419
     * content : ![]( http://wx1.sinaimg.cn/mw690/6bbfe5b6gy1fd8pts6xrxj21k40xqdl9.jpg)
     * <p>
     * ![]( http://wx3.sinaimg.cn/mw1024/6bbfe5b6gy1fd8q3abr0vj20je0jjabq.jpg)
     * <p>
     * 真的不明白青云的人怎么想的，生气。
     * content_rendered : <p><img alt="" src="http://wx1.sinaimg.cn/mw690/6bbfe5b6gy1fd8pts6xrxj21k40xqdl9.jpg"></p>
     * <p><img alt="" src="http://wx3.sinaimg.cn/mw1024/6bbfe5b6gy1fd8q3abr0vj20je0jjabq.jpg"></p>
     * <p>真的不明白青云的人怎么想的，生气。</p>
     * <p>
     * replies : 181
     * member : {"id":118929,"username":"Yc1992","tagline":"","avatar_mini":"//v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_mini.png?m=1442311108","avatar_normal":"//v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_normal.png?m=1442311108","avatar_large":"//v2ex.assets.uxengine.net/avatar/b4a7/dc2e/118929_large.png?m=1442311108"}
     * node : {"id":104,"name":"cloud","title":"云计算","title_alternative":"Cloud Computing","url":"http://www.v2ex.com/go/cloud","topics":1989,"avatar_mini":"//v2ex.assets.uxengine.net/navatar/c9e1/074f/104_mini.png?m=1488428111","avatar_normal":"//v2ex.assets.uxengine.net/navatar/c9e1/074f/104_normal.png?m=1488428111","avatar_large":"//v2ex.assets.uxengine.net/navatar/c9e1/074f/104_large.png?m=1488428111"}
     * created : 1488454465
     * last_modified : 1488502830
     * last_touched : 1488520221
     */

    private int id;
    private String title;
    private String url;
    private String content;
    @SerializedName("content_rendered")
    private String contentRendered;
    private int replies;
    private MemberOld member;
    private NodeOld node;
    private long created;
    //从网页获取。当显示为“置顶”时获取不到
    private String lastModifiedS;

    public String getLastModifiedS() {
        return lastModifiedS;
    }

    public void setLastModifiedS(String lastModifiedS) {
        this.lastModifiedS = lastModifiedS;
    }

    @SerializedName("last_modified")
    private long lastModified;
    @SerializedName("last_touched")
    private long lastTouched;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentRendered() {
        return contentRendered;
    }

    public void setContentRendered(String contentRendered) {
        this.contentRendered = contentRendered;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public MemberOld getMember() {
        return member;
    }

    public void setMember(MemberOld member) {
        this.member = member;
    }

    public NodeOld getNode() {
        return node;
    }

    public void setNode(NodeOld node) {
        this.node = node;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLastTouched() {
        return lastTouched;
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched = lastTouched;
    }

    @Override
    public String toString() {
        return "TopicOld{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", contentRendered='" + contentRendered + '\'' +
                ", replies=" + replies +
                ", member=" + member +
                ", node=" + node +
                ", created=" + created +
                ", lastModified=" + lastModified +
                ", lastTouched=" + lastTouched +
                '}';
    }
}
