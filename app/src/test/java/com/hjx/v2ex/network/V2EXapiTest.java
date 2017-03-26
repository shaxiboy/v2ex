package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Topic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        getAllTopic();
    }

    public void getAllTopic() throws Exception {
        if(sessionId > 0) {
            PageData<Topic> pageData = new PageData<>();
            pageData.setCurrentPage(300);
            V2EXapi.getAllTopic(pageData);
            System.out.println();
        }
    }

}