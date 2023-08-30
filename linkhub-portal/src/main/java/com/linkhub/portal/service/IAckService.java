package com.linkhub.portal.service;

import com.linkhub.common.model.dto.ack.UpdateAckDto;
import com.linkhub.common.model.pojo.Ack;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-27
 */
public interface IAckService extends IService<Ack> {

    /**
    * 更新用户最后一条消息id
    */
    boolean updateAck(UpdateAckDto updateAckDto);

    List<Ack> listAllAck();
}
