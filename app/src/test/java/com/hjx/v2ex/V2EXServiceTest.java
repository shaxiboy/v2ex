package com.hjx.v2ex;

import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.network.V2EXServiceSingleton;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/3/5 0005.
 */

public class V2EXServiceTest {

    @Test
    public void getNode() {
        try {
            Response<Node> response = V2EXServiceSingleton.getInstance().getNode(null, "creative").execute();
            printResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTopic() {
        try {
            Response<List<Topic>> response = V2EXServiceSingleton.getInstance().getTopic(346656).execute();
            printResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getTopicsFromHTML() {
        Response<ResponseBody> response = null;
        try {
            response = V2EXServiceSingleton.getInstance().getTopicsFromHTML("r2").execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRepliesFromHTML() {
        Response<ResponseBody> response = null;
        try {
            response = V2EXServiceSingleton.getInstance().getRepliesFromHTML(348769, 2).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getReplies() {
        Response<List<Reply>> response = null;
        try {
            response = V2EXServiceSingleton.getInstance().getReplies(348879, 2, 20).execute();
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printResponse(Response response) {
        try {
            if(response.isSuccessful()) {
                System.out.println("success");
                System.out.println(response.body().toString());
            } else {
                System.out.println("error");
                System.out.println(response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
