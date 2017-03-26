package com.hjx.v2ex.network;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by shaxiboy on 2017/3/5 0005.
 */

public class RetrofitServiceTest {

    @Test
    public void test() {
        RetrofitService service = RetrofitSingleton.getInstance();
        Method serviceMethod = null;
        try {
            serviceMethod =RetrofitService.class.getDeclaredMethod("topicPage", int.class, int.class);
            String html = ((ResponseBody) ((Call) serviceMethod.invoke(service, 349594, 1)).execute().body()).string();
            System.out.println(html);

            serviceMethod =RetrofitService.class.getDeclaredMethod("topicPage", int.class, int.class);
            html = ((ResponseBody) ((Call) serviceMethod.invoke(service, 349594, 2)).execute().body()).string();
            System.out.println(html);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
