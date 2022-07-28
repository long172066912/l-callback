package com.callback.base.sdk.utils;

import com.callback.base.sdk.consumer.CallBackConsumer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConsumerClassUtils
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/6/6 4:08 PM
 */
public class ConsumerClassUtils {

    /**
     * 获取泛型类型
     *
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> Type getGenericInterfaces(CallBackConsumer<T> consumer) {

        //  注意这里的 getGenericInterfaces 是获取接口泛型的方法
        Type[] genericInterfaces = consumer.getClass().getGenericInterfaces();

        //  这里强转是因为 ParameterizedType 继承Type接口 并可以获取对应的参数类
        ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[0];

        Type[] typeArguments = genericInterface.getActualTypeArguments();

        return typeArguments[0];
    }
}
