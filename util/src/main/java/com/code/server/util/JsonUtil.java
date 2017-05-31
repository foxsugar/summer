package com.code.server.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by sun on 2015/8/21.
 */
public final class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    static{

        //去掉默认的时间戳格式
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //设置为中国
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        //空值不序列化
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //反序列化时，属性不存在的兼容处理
        mapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //序列化时，日期的统一格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //单引号处理
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);


        //显示类型用的
        mapper.enableDefaultTyping();


    }


    public static String toJson(Object object){
        String result = null;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference){
        T result = null;
        try {
            result = mapper.readValue(json, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T readValue(String json , Class<T> valueType){
        T result = null;
        try {
            result = mapper.readValue(json, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonNode readTree(String content) {
        try {
            return mapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }







}
