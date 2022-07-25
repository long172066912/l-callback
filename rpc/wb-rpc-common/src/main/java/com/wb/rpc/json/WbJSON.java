package com.wb.rpc.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Json 工具
 * <p>
 * 使用方法请查看方法注释。
 * <p>
 * 其他使用说明
 * <p>
 * 序列化:
 * -排序 {@link com.fasterxml.jackson.annotation.JsonPropertyOrder @JsonPropertyOrder}，默认是按照Java类成员变量的声明顺序进行序列化。
 * -忽略字段 {@link @JsonIgnore、@JsonIgnoreProperties }
 * -自定义属性名 {@link @JsonProperty }
 * <p>
 * 反序列化:
 * 枚举  Converter @JsonCreator JsonValue
 * <p>
 * 使用注意事项：
 * 1. 不符合驼峰规范的变量 {@link #toJson(Object)} 转json后全部变成小写。
 * 例如ABc 序列化后为 abc
 * 解决方案： 属性加 @JsonProperty注解 并且 get方法加 @JsonIgnore注解（不加此注解序列化后会存在两个属性 ABc、abc并存）
 * <p>
 * 2.符合驼峰，单个小写字母+大写，Lombok自动生成的set方法（@Data），例如：aBc,反序列化会丢失字段.
 * 解决方法：idea生成set方法、或不使用此类命名方式（单个小写字母 + 大写)。
 * <p>
 * 3.对象中的方法不要已get开头，如果必要加 @JsonIgnore 注解。
 * 4.日期转化,时区问题， @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")，该注解需设置时区。
 * 5.布尔变量还是最好不要以is开头。
 *
 * @author
 */
public class WbJSON {

    private static final Logger logger = LoggerFactory.getLogger(WbJSON.class);

    private final static ObjectMapper defaultMapper = new ObjectMapper();

    /**
     * 如果有null字段的话,就不输出,用在节省流量的情况
     */
    private final static ObjectMapper pureMapper = new ObjectMapper();

    /**
     * null字段也输出
     */
    private final static ObjectMapper rawMapper = new ObjectMapper();

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

    // ------------- defaultMapper start -------------

    /**
     * json字符串到对象,默认配置
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return defaultMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("WbJSON fromJson " + json, e);
            return null;
        }
    }

    /**
     * json字符串到对象,默认配置 {@link #defaultMapper}
     *
     * @param json
     * @param valueTypeRef 例如:new TypeReference<Map<String, Att>>(){}
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeReference valueTypeRef) {
        try {
            return defaultMapper.readValue(json, valueTypeRef);
        } catch (Exception e) {
            logger.error("WbJSON fromJson", e);
            return null;
        }
    }

    /**
     * 对象到json字符串,默认配置
     * - 属性为NULL不被序列化
     * - java.sql.Date format yyyy-MM-dd
     *
     * @param value
     * @return
     */
    public static String toJson(Object value) {
        try {
            return defaultMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error("WbJSON toJson", e);
            return null;
        }
    }

    // ------------- defaultMapper end -------------


    // ------------- pureMapper start -------------

    /**
     * json字符串到对象,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T fromJsonPure(String json, Class<T> valueType) {
        try {
            return pureMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("WbJSON fromJsonPure", e);
            return null;
        }
    }

    /**
     * json字符串到对象,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param json
     * @param valueTypeRef 例如:new TypeReference<Map<String, Att>>(){}
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T fromJsonPure(String json, TypeReference valueTypeRef) {
        try {
            return pureMapper.readValue(json, valueTypeRef);
        } catch (Exception e) {
            logger.error("WbJSON fromJsonPure", e);
            return null;
        }
    }

    /**
     * 对象到json字符串,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param value
     * @return
     */
    public static String toJsonPure(Object value) {
        try {
            return pureMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error("WbJSON toJsonPure", e);
            return null;
        }
    }
    // ------------- pureMapper end -------------

    /**
     * 对象到json字符串,有null字段的话也输出
     *
     * @param value
     * @return
     */
    public static String toJsonRaw(Object value) {
        try {
            return rawMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error("WbJSON toJsonRaw", e);
            return null;
        }
    }

    /**
     * 将 json 转成 JsonNode
     *
     * @param json
     * @return
     */
    public static JsonNode readTree(String json) {
        try {
            return defaultMapper.readTree(json);
        } catch (IOException e) {
            logger.error("WbJSON readTree", e);
            return null;
        }
    }
}
