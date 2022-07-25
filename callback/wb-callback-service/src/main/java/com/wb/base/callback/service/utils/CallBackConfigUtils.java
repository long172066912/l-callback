package com.wb.base.callback.service.utils;

import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;

import java.security.MessageDigest;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConfigUtils
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 2:44 PM
 */
public class CallBackConfigUtils {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackConfigUtils.class);

    /**
     * 根据配置获取背压唯一key
     *
     * @param callBackConfig
     * @return
     */
    public static String getCallBackConfigKey(CallBackConfig callBackConfig) {
        return string2Md5(callBackConfig.getRoute() + callBackConfig.getPath() + callBackConfig.getCallBackType().name());
    }


    /**
     * MD5
     *
     * @param inStr
     * @return
     */
    public static String string2Md5(String inStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(inStr.getBytes("utf-8"));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            LOGGER.error(String.format("string2Md5 error |%s", e));
            return null;
        }
    }
}
