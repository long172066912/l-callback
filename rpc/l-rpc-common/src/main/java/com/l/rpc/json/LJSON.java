package com.l.rpc.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;

/**
* @Title: LJSON
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/7/27 6:18 PM
* @version V1.0
*/
public class LJSON {

    private final static ObjectMapper defaultMapper = new ObjectMapper();

    /**
     * 如果有null字段的话,就不输出,用在节省流量的情况
     */
    private final static ObjectMapper pureMapper = new ObjectMapper();

    static {
        // 初始化,这是Jackson所谓的key缓存：对JSON的字段名是否调用String#intern方法，放进字符串常量池里，以提高效率,设置为false。
        defaultMapper.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        //反序列化忽略未知属性，不报错
        defaultMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //默认情况下（false）parser解析器是不能解析包含控制字符的json字符串，设置为true不报错。
        defaultMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //属性为NULL不被序列化，只对bean起作用，Map List不起作用
        defaultMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 指定类型的序列化, 不同jackson版本对不同的类型的默认规则可能不一样，这里做强制指定
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(java.sql.Date.class, new DateSerializer(null, new SimpleDateFormat("yyyy-MM-dd")));
        defaultMapper.registerModule(simpleModule);
        //org.json.JSONArray、org.json.JSONObject 序列化反序列化
        defaultMapper.registerModule(new JsonOrgModule());


        // 初始化,这是Jackson所谓的key缓存：对JSON的字段名是否调用String#intern方法，放进字符串常量池里，以提高效率，默认是true。
        pureMapper.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        //属性为NULL不被序列化，只对bean起作用，Map List不起作用
        pureMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //org.json.JSONArray、org.json.JSONObject 序列化反序列化
        pureMapper.registerModule(new JsonOrgModule());
    }

    /**
     * json字符串到对象,默认配置
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> valueType) {
        return defaultMapper.readValue(json, valueType);
    }

    /**
     * json字符串到对象,默认配置 {@link #defaultMapper}
     *
     * @param json
     * @param valueTypeRef 例如:new TypeReference<Map<String, Att>>(){}
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T fromJson(String json, TypeReference valueTypeRef) {
        return defaultMapper.readValue(json, valueTypeRef);
    }

    /**
     * 对象到json字符串,默认配置
     * - 属性为NULL不被序列化
     * - java.sql.Date format yyyy-MM-dd
     *
     * @param value
     * @return
     */
    @SneakyThrows
    public static String toJson(Object value) {
        return defaultMapper.writeValueAsString(value);
    }
}
