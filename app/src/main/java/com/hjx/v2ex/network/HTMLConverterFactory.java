package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.Topic;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

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
            if(type == Node.class) {
                throw new IOException();
            }
            Topic topic = new Topic();
            topic.setContent("test");
            return (T) Arrays.asList(new Topic[] {topic});
        }

    }
}
