package com.l.rpc.exception;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: SystemCode
 * @Description: 平台系统异常枚举
 * @date 2021/10/8 1:44 PM
 */
public enum SystemCode {
    /**
     * 成功
     */
    SUCCESS("成功"),
    /**
     * 无调用权限
     */
    E90001("无调用权限"),
    /**
     * 系统异常
     */
    E90002("系统异常"),
    /**
     * 参数校验失败
     */
    E90003("参数校验失败"),
    ;
    private String desc;

    private static Set<String> systemCodeSet = Arrays.stream(SystemCode.values()).map(e -> e.name()).collect(Collectors.toSet());

    SystemCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isSystemCode(String code) {
        return systemCodeSet.contains(code);
    }
}
