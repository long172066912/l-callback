package com.wb.base.callback.service.callback.infra;

import com.wb.base.callback.service.exception.RetryOverException;

/**
* @Title: RetryRateConfigRepostory
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/5/27 4:32 PM
* @version V1.0
*/
public interface RetryRateConfigRepository {

    /**
     * 获取下一个重试时间
     * @param now
     * @return
     * @throws RetryOverException
     */
    int getNextRetryTime(int now) throws RetryOverException;
}
