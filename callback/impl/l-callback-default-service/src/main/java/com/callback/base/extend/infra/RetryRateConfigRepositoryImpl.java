package com.callback.base.extend.infra;

import com.callback.base.service.callback.infra.RetryRateConfigRepository;
import com.callback.base.service.exception.RetryOverException;
import com.callback.base.utils.CallBackApolloConfigUtils;
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
