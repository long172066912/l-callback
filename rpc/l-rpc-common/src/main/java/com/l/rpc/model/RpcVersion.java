package com.l.rpc.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RpcVersion
 * @Description: RPC版本与协议控制
 * @date 2021/10/22 5:13 PM
 */
public enum RpcVersion {
    /**
     * RPC返回版本1.0
     */
    RPC_VERSION_1_0("1.0");

    public static final String RPC_VERSION_KEY = "RPC_VERSION";
    /**
     * RPC调用服务异常返回header中的cd字段名
     */
    public static final String RPC_HEADER_ERROR_CD_KEY = "RPC_ERROR_CD";
    /**
     * RPC调用服务异常返回header中的desc字段名
     */
    public static final String RPC_HEADER_ERROR_DESC_KEY = "RPC_ERROR_DESC";

    private String version;

    private static Map<String, RpcVersion> versionMap;

    static {
        versionMap = Arrays.stream(RpcVersion.values()).collect(Collectors.toMap(RpcVersion::getVersion, e -> e));
    }

    RpcVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static RpcVersion getVersion(String version) {
        return versionMap.get(version);
    }
}
