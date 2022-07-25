package com.wb.rpc.factory;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Proxy;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RpcFactory
 * @Description: 玩吧rpc代理工厂
 * @date 2022/7/4 11:46 AM
 */
public class RpcFactory {

    private static RpcFactory factory;

    public static RpcFactory getInstance() {
        if (null == factory) {
            synchronized (RpcFactory.class) {
                if (null == factory) {
                    factory = new RpcFactory();
                }
            }
        }
        return factory;
    }

    public <T> T getService(Class<T> clazz) throws Throwable {
        //对rpc代理进行代理
        return (T) Proxy.newProxyInstance(RpcFactory.class.getClassLoader(), new Class[]{clazz}, new RpcProxy());
    }
}
