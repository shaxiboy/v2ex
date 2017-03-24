package com.hjx.v2ex.util;

import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.network.V2EXService;

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

    @Test
    public void test() {
        try {
            V2EXService service = RetrofitSingleton.getInstance();
            Method serviceMethod = V2EXService.class.getDeclaredMethod("vistHomePage", String.class);
            String html = ((ResponseBody) ((Call) serviceMethod.invoke(service, "tech")).execute().body()).string();
            Method htmlParsedMethod = HTMLUtil.class.getMethod("parseNodeGuide", String.class);
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
    }

}
