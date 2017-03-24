package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.NodeOld;
import com.hjx.v2ex.entity.ReplyOld;
import com.hjx.v2ex.entity.TopicOld;
import com.hjx.v2ex.network.RetrofitSingleton;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/3/5 0005.
 */

public class V2EXServiceTest {

    @Test
    public void test() {
        V2EXService service = RetrofitSingleton.getInstance();
        Method serviceMethod = null;
        try {
            serviceMethod =V2EXService.class.getDeclaredMethod("vistTopicPage", int.class, int.class);
            String html = ((ResponseBody) ((Call) serviceMethod.invoke(service, 349594, 1)).execute().body()).string();
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
