package com.hjx.v2ex.util;

import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.V2EXapi;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by shaxiboy on 2017/3/10 0010.
 */

public class HTMLUtilTest {

    int sessionId;

    public void login() {
        sessionId = V2EXapi.login("shaxiboy", "ysl5129v2ex");
    }

    public void loginout() {
        V2EXapi.loginout(sessionId);
    }

    @Test
    public void test() {
        login();

        try {
            RetrofitService service = RetrofitSingleton.getInstance();
            Method serviceMethod = RetrofitService.class.getDeclaredMethod("signin");
            String html = ((ResponseBody) ((Call) serviceMethod.invoke(service)).execute().body()).string();
            Method htmlParsedMethod = HTMLUtil.class.getMethod("parseSigninParams", String.class);
            Object result = htmlParsedMethod.invoke(null, html);
            System.out.println(result);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginout();
    }

}
