package com.hjx.v2ex.network;

import com.hjx.v2ex.util.HTMLUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class HTMLConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new HTMLResponseBodyConverter<>(type);
    }

    public static Converter.Factory create() {
        return new HTMLConverterFactory();
    }

    class HTMLResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        private final Type type;

        public HTMLResponseBodyConverter(Type type) {
            this.type = type;
        }

        @Override
        public T convert(ResponseBody responseBody) throws IOException {
            String convertMethodName = "parse" + ((Class) type).getSimpleName();
            try {
                Method convertMethod = HTMLUtil.class.getMethod(convertMethodName, String.class);
                return (T) convertMethod.invoke(null, responseBody.string());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
