package com.hjx.v2ex.util;

import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.R.id.list;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class HTMLUtil {

    public static List<Topic> parseTopicList(String html) {
        List<Topic> topics = new ArrayList<>();
        Elements elements = Jsoup.parse(html).body().getElementsByAttributeValue("class", "cell item");
        for(Element element : elements) {
            topics.add(parseTopic(element));
        }
        return topics;
    }

    public static Topic parseTopic(Element el) {
        Topic topic = new Topic();
        Elements tdNodes = el.getElementsByTag("td");
        Member member = new Member();
        Node node = new Node();
        for (Element tdNode : tdNodes) {
            String content = tdNode.toString();
            if (content.contains("class=\"avatar\"") ) {
                Elements userIdNode = tdNode.getElementsByTag("a");
                if (userIdNode != null) {
                    String idUrlString = userIdNode.attr("href");
                    member.setUsername(idUrlString.replace("/member/", ""));
                }

                Elements avatarNode = tdNode.getElementsByTag("img");
                if (avatarNode != null) {
                    String avatarString = avatarNode.attr("src");
                    if (avatarString.startsWith("//")) {
                        avatarString = "http:" + avatarString;
                    }
                    member.setAvatarMini(avatarString);
                }
            } else if (content.contains("class=\"item_title\"") ) {
                Elements aNodes = tdNode.getElementsByTag("a");
                for (Element aNode : aNodes) {
                    if (aNode.attr("class").equals("node")) {
                        String nodeUrlString = aNode.attr("href");
                        node.setName(nodeUrlString.replace("/go/", ""));
                        node.setTitle(aNode.text());
                    } else {
                        if (aNode.toString().contains("reply") ) {
                            topic.setTitle(aNode.text());
                            String topicIdString = aNode.attr("href");
                            String[] subArray = topicIdString.split("#");
                            topic.setId(Integer.parseInt(subArray[0].replace("/t/", "")));
                        }
                    }
                }

                Elements spanNodes = tdNode.getElementsByTag("span");
                for (Element spanNode : spanNodes) {
                    if(spanNode.hasClass("small fade")) {
                        String[] splits = spanNode.text().split("  •  ");
                        if(splits.length > 2) {
                            topic.setLastModifiedS(splits[2]);
                        } else {
                            //某些情况下无法获取时间？
                        }
                    }
                }
            } else if (content.contains("class=\"count_livid\"") ) {
                topic.setReplies(Integer.parseInt(tdNode.getElementsByTag("a").text()));
            }
        }

        topic.setMember(member);
        topic.setNode(node);

        return topic;
    }

    public static List<Reply> parseReplyList(String html) {
        List<Reply> replies = new ArrayList<>();
        Elements elements = Jsoup.parse(html).body().getElementsByAttributeValue("class", "cell item");
        for(Element element : elements) {
            replies.add(parseReply(element));
        }
        return replies;
    }

    private static Reply parseReply(Element element) {
        return null;
    }
}
