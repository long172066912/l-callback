package com.wb.base.callback.extend.infra;

import com.wb.base.callback.service.callback.infra.RetryRateConfigRepository;
import com.wb.base.callback.service.exception.RetryOverException;
import com.wb.base.callback.utils.CallBackApolloConfigUtils;
import org.springframework.stereotype.Repository;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RetryRateConfigRepositoryImpl
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 4:37 PM
 */
@Repository
public class RetryRateConfigRepositoryImpl implements RetryRateConfigRepository {

    @Override
    public int getNextRetryTime(int now) throws RetryOverException {
        CallBackApolloConfigUtils.RetryRateData retryRateConfig = CallBackApolloConfigUtils.getRetryRateConfig();
        if (now <= 0){
            return retryRateConfig.getArr()[0];
        }
        Integer index = retryRateConfig.getMap().get(now) + 1;
        if (index == retryRateConfig.getMap().size()) {
            throw new RetryOverException();
        }
        return retryRateConfig.getArr()[index];
    }
}
