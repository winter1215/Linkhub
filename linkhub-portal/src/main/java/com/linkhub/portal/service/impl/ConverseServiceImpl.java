package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.model.pojo.Converse;
import com.linkhub.common.mapper.ConverseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IConverseService;
import com.linkhub.security.util.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 会话表 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
@Service
public class ConverseServiceImpl extends ServiceImpl<ConverseMapper, Converse> implements IConverseService {
    @Resource
    private GroupMemberMapper groupMemberMapper;

    @Override
    public Set<String> getUserAllConverseIds(String userId) {
        List<Converse> converses = baseMapper.selectList(
                new LambdaQueryWrapper<Converse>()
                        .eq(Converse::getMember, userId)
                        .select(Converse::getConverseId));
        List<String> dmConverseIds = converses.stream().map(Converse::getConverseId).collect(Collectors.toList());

        List<GroupMember> groups = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getUserId, userId)
                        .select(GroupMember::getGroupId));
        List<String> groupIds = groups.stream().map(GroupMember::getGroupId).collect(Collectors.toList());

        // todo: panel id, group 的表还没创建完
        Set<String> res = new HashSet<>(dmConverseIds);
        res.addAll(groupIds);
        return res;
    }
}
