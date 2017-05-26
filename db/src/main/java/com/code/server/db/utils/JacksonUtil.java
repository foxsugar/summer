package com.code.server.db.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by sunxianping on 2017/4/1.
 */
public class JacksonUtil {


        public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


        static{

            //去掉默认的时间戳格式
            OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            //设置为中国
            OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            OBJECT_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            //空值不序列化
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            //反序列化时，属性不存在的兼容处理
            OBJECT_MAPPER.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            //序列化时，日期的统一格式
            OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //单引号处理
            OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);


//            OBJECT_MAPPER.enableDefaultTyping();
        }

        public static <T> T fromString(String string, Class<T> clazz) {
            try {
                return OBJECT_MAPPER.readValue(string, clazz);
            } catch (IOException e) {
                throw new IllegalArgumentException("The given string value: "
                        + string + " cannot be transformed to Json object");
            }
        }

        public static String toString(Object value) {
            try {
                return OBJECT_MAPPER.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("The given Json object value: "
                        + value + " cannot be transformed to a String");
            }
        }

        public static JsonNode toJsonNode(String value) {
            try {
                return OBJECT_MAPPER.readTree(value);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static <T> T clone(T value) {
            return fromString(toString(value), (Class<T>) value.getClass());
        }
}
