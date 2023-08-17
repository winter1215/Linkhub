package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.model.dto.converse.ConverseIdRequest;
import com.linkhub.common.model.dto.userdmlist.UserdmlistDto;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.pojo.Userdmlist;
import com.linkhub.common.mapper.UserdmlistMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IUserdmlistService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户的会话列表 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Service
public class UserdmlistServiceImpl extends ServiceImpl<UserdmlistMapper, Userdmlist> implements IUserdmlistService {

    @Autowired
    UserdmlistMapper userdmlistMapper;

    @Override
    public UserdmlistDto addConverse(String userId, ConverseIdRequest converseIdRequest) {
        // 先插入，再查所有userId对应的会话返回
        if (ObjectUtils.isEmpty(converseIdRequest)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(converseIdRequest.getConverseId())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        Userdmlist userdmlist = new Userdmlist();
        userdmlist.setUserId(userId);
        userdmlist.setConverseId(converseIdRequest.getConverseId());
        baseMapper.insert(userdmlist);
        // 查询userId对应的所有记录
        LambdaQueryWrapper<Userdmlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Userdmlist::getUserId, userId);
        List<Userdmlist> userdmlists = baseMapper.selectList(wrapper);

        // 封装
        UserdmlistDto userdmlistDto = new UserdmlistDto();
        userdmlistDto.setUserId(userId);
        List<String> converseIds = new ArrayList<>();
        for (Userdmlist item : userdmlists) {
            converseIds.add(item.getConverseId());
        }
        userdmlistDto.setConverseIds(converseIds);
        return userdmlistDto;
    }
}
