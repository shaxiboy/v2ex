package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.util.HTMLUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/26 0026.
 */

public class V2EXapi {

    public static void getAllTopic(PageData<Topic> pageData) {
        try {
            HTMLUtil.parseAllTopicList(pageData, RetrofitSingleton.getInstance().allTopicsPage(pageData.getCurrentPage()).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int login(String username, String password) {
        try {
            String html = RetrofitSingleton.getInstance().signin().execute().body().string();
            Map<String, String> signinParams = HTMLUtil.parseSigninParams(html);
            for(String key : signinParams.keySet()) {
                if(signinParams.get(key).equals("username")) {
                    signinParams.put(key, username);
                } else if(signinParams.get(key).equals("password")) {
                    signinParams.put(key, password);
                }
            }
            html = RetrofitSingleton.getInstance().signin(signinParams).execute().body().string();
            String errorMsg = HTMLUtil.parseSigninFailedMsg(html);
            if(errorMsg == null) {
//                LogUtil.d("signin success");
                System.out.println("signin success");
                int sessionId = HTMLUtil.parseSessionId(html);
                System.out.println("sessionid: " + sessionId);
                return sessionId;
            } else  {
//                LogUtil.d("signin failed: " + errorMsg);
                System.out.println("signin failed: " + errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void loginout(int sessionId) {
        try {
            String html = RetrofitSingleton.getInstance().signout(sessionId).execute().body().string();
            String resultMsg = HTMLUtil.parseSignoutResultMsg(html);
            System.out.println("resultMsg: " + resultMsg);
            if(resultMsg.equals("重新登录")) {
//                LogUtil.d("signin success");
                System.out.println("signout success");
            } else {
//                LogUtil.d("signin failed: " + errorMsg);
                System.out.println("signout failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
