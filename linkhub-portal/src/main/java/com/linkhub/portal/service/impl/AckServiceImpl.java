package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linkhub.common.model.dto.ack.UpdateAckDto;
import com.linkhub.common.model.pojo.Ack;
import com.linkhub.common.mapper.AckMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IAckService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-27
 */
@Service
public class AckServiceImpl extends ServiceImpl<AckMapper, Ack> implements IAckService {

    @Override
    public boolean updateAck(UpdateAckDto updateAckDto) {
        String userId = SecurityUtils.getLoginUserId();
        Long lastMessageId = updateAckDto.getLastMessageId();
        String converseId = updateAckDto.getConverseId();

        Ack ack = new Ack();
        ack.setUserId(userId);
        ack.setConverseId(converseId);
        ack.setLastMessageId(lastMessageId);

        LambdaUpdateWrapper<Ack> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Ack::getUserId, userId);
        wrapper.eq(Ack::getConverseId, converseId);
        return this.saveOrUpdate(ack, wrapper);
    }

    @Override
    public List<Ack> listAllAck() {
        String userId = SecurityUtils.getLoginUserId();

        LambdaQueryWrapper<Ack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ack::getUserId, userId);
        return baseMapper.selectList(wrapper);
    }
}
