package com.wb.rpc.server.util;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: PlatformApolloConfigUtil
 * @Description: 可设计成抽象，通过实现让每个服务有不同前缀
 * @date 2021/10/12 2:53 PM
 */
public class ServerConfigUtil {
    /**
     * 获取服务异常唯一标识，用作拼接
     *
     * @return
     */
    public static String getServerErrorIdent() {
        return "AA";
    }
}
