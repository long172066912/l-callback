package com.callback.base.extend.infra;

import com.callback.base.extend.infra.model.CallBackMessageDO;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackMessageDaoRepository
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/30 5:43 PM
 */
public interface CallBackMessageDaoRepository {

    /**
     * 获取一条回调消息
     *
     * @return
     */
    CallBackMessageDO getCallBackMessageByDb();

    /**
     * 根据id清除数据
     *
     * @param id
     */
    void cleanDbMessageById(long id);
}
