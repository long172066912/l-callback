package com.callback.base.extend.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.callback.base.extend.infra.model.CallBackMessageDO;
import org.apache.ibatis.annotations.*;

/**
 * @author liuxiaofeng
 * description QQ登录绑定信息
 * @date 14:29 2022/4/18
 **/
@Mapper
public interface CallBackMessageMapper extends BaseMapper<CallBackMessageDO> {

    String UID_TABLE_NAME = " callback_message ";


    /**
     * 保存用户uid对应的unionid
     * @param message
     * @return
     */
    @Insert("insert into " + UID_TABLE_NAME + " (platform_type, business_type, back_pressure, message_id, config, data) values (#{message.platformType}, #{message.businessType}, #{message.backPressure}, #{message.messageId}, #{message.config}, #{message.data}) ")
    int save(@Param("message") CallBackMessageDO message);

    /**
     * 根据主键id删除
     * @param id
     * @return
     */
    @Delete("delete from " + UID_TABLE_NAME + " where id = #{id} limit 1 ")
    int deleteById(@Param("id") long id);

    /**
     * 获取一条
     * @return
     */
    @Select("select id, platform_type, business_type, back_pressure, message_id, config, data  from " + UID_TABLE_NAME + " where status = 1 order by id asc limit 1 ")
    CallBackMessageDO selectOne();
}
