package com.hjx.v2ex.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by shaxiboy on 2017/3/26 0026.
 */
public class V2EXapiTest {

    int sessionId;

    @Before
    public void setUp() throws Exception {
        sessionId = V2EXapi.login("shaxiboy", "ysl5129v2ex");
    }

    @After
    public void tearDown() throws Exception {
        if(sessionId > 0) {
            V2EXapi.loginout(sessionId);
        }
    }

    @Test
    public void test() throws Exception{
        Object result = V2EXapi.getTopicReplies(350989, 1);
        System.out.println(result);
    }

}