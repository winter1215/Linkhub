package com.linkhub.portal.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.GroupInviteEnum;
import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.model.dto.group.GroupEditRequest;
import com.linkhub.common.model.dto.group.GroupInviteApplyRequest;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.dto.user.UserInfoDto;
import com.linkhub.common.model.pojo.GroupInvite;
import com.linkhub.common.mapper.GroupInviteMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.User;
import com.linkhub.portal.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>
 * 群组邀请码 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-20
 */
@Service
public class GroupInviteServiceImpl extends ServiceImpl<GroupInviteMapper, GroupInvite> implements IGroupInviteService {

    @Autowired
    IGroupService groupService;

    @Autowired
    IGroupRoleService groupRoleService;

    @Resource
    GroupInviteMapper groupInviteMapper;

    @Resource
    IUserService userService;

    @Resource
    IMessageService messageService;

    private String generateCode(int restTimes) {
        if (restTimes <= 0) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "为当前用户分配随机码失败，请多次重试");
        }
        // generate 4-bit random numbers of string type
        String code = RandomUtil.randomString(8);
        // check if discriminator is unique
        LambdaQueryWrapper<GroupInvite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupInvite::getCode, code);
        GroupInvite groupInvite = baseMapper.selectOne(wrapper);
        if (ObjectUtils.isNotEmpty(groupInvite)) {
            return generateCode(--restTimes);
        }
        return code;
    }


    @Override
    public GroupInvite createGroupInvite(String userId, GroupInviteRequest groupInviteRequest) {
        // 判空处理
        if (ObjectUtils.isEmpty(groupInviteRequest)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        String groupId = groupInviteRequest.getGroupId();
        String inviteType = groupInviteRequest.getInviteType();

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId) || StringUtils.isEmpty(inviteType)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 校验权限
        boolean hasNormalPermission = false;
        boolean hasUnlimitedPermission = false;

        List<String> userAllPermissions = groupRoleService.getUserAllPermissions(groupId, userId);
        for (String permission : userAllPermissions) {
            if (permission.equals(GroupPermissionEnum.INVITE.getMessage())) hasNormalPermission = true;
            else if (permission.equals(GroupPermissionEnum.UN_LIMITED_INVITE.getMessage())) hasUnlimitedPermission = true;
        }


        if ((inviteType.equals(GroupInviteEnum.NORMAL_INVITE.getMessage()) && !hasNormalPermission) ||
                inviteType.equals(GroupInviteEnum.PERMANENT_INVITE.getMessage()) && !hasUnlimitedPermission) {
            throw new GlobalException("没有创建邀请码权限", ErrorCode.NO_AUTH_ERROR.getCode());
        }


        // 创建code
        String code = generateCode(10); // 重试10次
        GroupInvite groupInvite = new GroupInvite();
        groupInvite.setCode(code);
        groupInvite.setCreator(userId);
        groupInvite.setGroupId(groupInviteRequest.getGroupId());
        if (inviteType.equals(GroupInviteEnum.NORMAL_INVITE.getMessage())) {
            LocalDateTime expire = LocalDateTime.now().plus(7, ChronoUnit.DAYS);;
            // 加上7天
            groupInvite.setExpireAt(expire);
        }
        groupInviteMapper.insert(groupInvite);
        return groupInvite;
    }

    @Override
    public int editGroupInvite(String userId, GroupEditRequest groupEditRequest) {
        // 判空
        if (ObjectUtils.isEmpty(groupEditRequest)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (StringUtils.isEmpty(userId) ||
                StringUtils.isEmpty(groupEditRequest.getGroupId()) ||
                StringUtils.isEmpty(groupEditRequest.getCode())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        String groupId = groupEditRequest.getGroupId();
        String code = groupEditRequest.getCode();

        // 校验权限
        boolean hasEditPermission = groupService.checkUserPermission(userId, groupId, GroupPermissionEnum.EDIT_INVITE, null);
        if (!hasEditPermission) {
            throw new GlobalException("没有编辑邀请码权限", ErrorCode.NO_AUTH_ERROR.getCode());
        }

        // 设置expireAt和usageLimit
        Long expireAt = groupEditRequest.getExpireAt();
        // 将时间戳转换为Instant
        Instant instant = Instant.ofEpochSecond(expireAt);
        // 将Instant转换为LocalDateTime，需要指定时区
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        Integer usageLimit = groupEditRequest.getUsageLimit();


        GroupInvite groupInvite = new GroupInvite();
        groupInvite.setExpireAt(localDateTime);
        groupInvite.setUsageLimit(usageLimit);
        LambdaUpdateWrapper<GroupInvite> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(GroupInvite::getCode, code);
        return baseMapper.update(groupInvite, wrapper);
    }

    @Override
    public List<GroupInvite> getAllGroupInviteCode(String userId, String groupId) {
        // 判空
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 鉴权
        boolean hasPermission = groupService.checkUserPermission(userId, groupId,
                GroupPermissionEnum.MANAGE_INVITE, null);
        if (!hasPermission) {
            throw new GlobalException("没有查看权限", ErrorCode.NO_AUTH_ERROR.getCode());
        }
        // 查询返回
        LambdaQueryWrapper<GroupInvite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupInvite::getGroupId, groupId);
        List<GroupInvite> groupInvites = baseMapper.selectList(wrapper);
        return groupInvites;
    }

    @Override
    public GroupInvite findGroupInviteByCode(String code) {
        LambdaQueryWrapper<GroupInvite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupInvite::getCode, code);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void applyInvite(User user, GroupInviteApplyRequest groupInviteApplyRequest) {
        // 判空
        if (ObjectUtils.isEmpty(user)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (ObjectUtils.isEmpty(groupInviteApplyRequest) || StringUtils.isEmpty(groupInviteApplyRequest.getCode())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        String code = groupInviteApplyRequest.getCode();

        // 使用code查询
        GroupInvite invite = findGroupInviteByCode(code);
        if (ObjectUtils.isEmpty(invite)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否使用次数耗尽
        if (ObjectUtils.isNotEmpty(invite.getUsageLimit())) { // 如果为null则是无限次
            int usage = 0;
            if (ObjectUtils.isNotEmpty(invite.getUsage())) usage = invite.getUsage();
            if (usage >= invite.getUsageLimit()) {
                throw new GlobalException("该邀请码使用次数耗尽",ErrorCode.OPERATION_ERROR.getCode());
            }
        }

        // 判断邀请码是否过期
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(invite.getExpireAt())) {
            throw new GlobalException("该邀请码已过期",ErrorCode.OPERATION_ERROR.getCode());
        }

        if (ObjectUtils.isEmpty(invite.getGroupId())) {
            throw new GlobalException("群组邀请失效: 群组id为空",ErrorCode.OPERATION_ERROR.getCode());
        }

        // todo: 将当前用户joinGroup到群组
        // 更新usage
        LambdaUpdateWrapper<GroupInvite> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(GroupInvite::getCode, code);
        GroupInvite groupInvite = new GroupInvite();
        groupInvite.setUsage(invite.getUsage() + 1); // 增加

        // send 加入信息 给群组所有人
        UserInfoDto userInfo = userService.getUserInfo(invite.getCreator());
        messageService.addGroupSystemMessage(invite.getGroupId(), String.format("%s 通过 %s 的邀请码加入群组", user.getNickname(), userInfo.getNickname()));
    }
}
