package com.linkhub.portal.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ClientEventEnum;
import com.linkhub.common.enums.ConverseTypeEnum;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.IMNotifyTypeEnum;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.mapper.UserdmlistMapper;
import com.linkhub.common.model.dto.converse.AppendDMConverseMemberRequest;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.dto.user.UserIdsRequest;
import com.linkhub.common.model.dto.user.UserInfoDto;
import com.linkhub.common.model.pojo.Converse;
import com.linkhub.common.mapper.ConverseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.pojo.Userdmlist;
import com.linkhub.common.model.vo.ConverseVo;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IConverseService;
import com.linkhub.portal.service.IMessageService;
import com.linkhub.portal.service.IUserService;
import com.linkhub.portal.service.IUserdmlistService;
import com.linkhub.security.util.SecurityUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private ConverseMapper converseMapper;


    @Resource
    private IUserdmlistService userdmlistService;

    @Resource
    private IUserService userService;

    @Resource
    private IMessageService messageService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConverseVo creatDMconverse(User user, String[] memberIds) {
        // 判空
        String userId = user.getId();
        if (StringUtils.isEmpty(userId) || ArrayUtils.isEmpty(memberIds)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        // 去重
        Set<String> participantList = new HashSet<>();
        participantList.add(userId);
        participantList.addAll(Arrays.asList(memberIds));

        String type;
        if (participantList.size() > 2) {
            // 多人会话
            type = ConverseTypeEnum.CONVERSE_TYPE_MULTI.getMessage();
        } else {
            type = ConverseTypeEnum.CONVERSE_TYPE_DM.getMessage();
        }

        // 持久化到converse表中
        // 生成converseId
        String converseId = IdUtil.fastSimpleUUID();
        List<Converse> converseList = new ArrayList<>();

        participantList.forEach(participant -> {
            Converse converse = new Converse();
            converse.setConverseId(converseId);
            converse.setType(type);
            converse.setMember(participant);
            converseList.add(converse);
        });

        // 批量插入
        this.saveBatch(converseList);

        ConverseVo converseVo = new ConverseVo();
        converseVo.setId(converseId);
        converseVo.setType(type);
        converseVo.setMembers(new ArrayList<>(participantList));
        converseVo.setCreatedAt(converseList.get(0).getCreateAt());
        converseVo.setUpdatedAt(converseList.get(0).getUpdateAt());
        // joinSocketRoom
        Set<String> roomIds = new HashSet<>();
        roomIds.add(converseId);

        participantList.forEach(participant -> {
            IMUtil.joinRoom(roomIds, participant);
        });

        // 广播更新信息
        String[] target = new String[1];
        target[0] = converseId;
        IMUtil.notify(target, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.CONVERSE_UPDATEDM, converseVo);


        // 更新dmlist
        participantList.forEach(participant -> {
            userdmlistService.addConverse(participant, converseId);
        });

        // 如果创建的是一个多人会话(非双人), 发送系统消息
        if (participantList.size() > 2) {
            // 把除了userId的其他用户的详细信息查出来
            UserIdsRequest userIdsRequest = new UserIdsRequest();
            userIdsRequest.setUserIds(memberIds);
            List<UserInfoDto> userInfoList = userService.getUserInfoList(userIdsRequest);
            // 组装字符串
            String concatenated = userInfoList.stream()
                    .map(UserInfoDto::getNickname)
                    .reduce((nickname1, nickname2) -> nickname1 + ", " + nickname2)
                    .orElse("");

            SendMsgDto sendMsgDto = new SendMsgDto();
            sendMsgDto.setContent(String.format("%s 邀请 %s 加入会话", user.getNickname(),concatenated));
            sendMsgDto.setConverseId(converseId);
            messageService.sendSysMessage(sendMsgDto);
        }
        return converseVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConverseVo appendDMConverseMembers(User user, AppendDMConverseMemberRequest appendMemberRequest) {
        // todo: 判断members是否已经在会话
        // 判空
        if (ObjectUtils.isEmpty(user) || StringUtils.isEmpty(user.getId()) || ObjectUtils.isEmpty(appendMemberRequest)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        String userId = user.getId();
        String converseId = appendMemberRequest.getConverseId();
        String[] memberIds = appendMemberRequest.getMembersIds();
        
        // 判断会话是否存在
        LambdaQueryWrapper<Converse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Converse::getConverseId, converseId);
        List<Converse> converse = baseMapper.selectList(wrapper);

        if (ObjectUtils.isEmpty(converse)) {
            throw new GlobalException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 将所有的memberIds插入到数据库对应上conversId
        List<Converse> converseList = new ArrayList<>();
        for (int i = 0; i < memberIds.length; i ++ ) {
            Converse tmp = new Converse();
            tmp.setConverseId(converseId);
            tmp.setType(ConverseTypeEnum.CONVERSE_TYPE_MULTI.getMessage());
            tmp.setMember(memberIds[i]);
            converseList.add(tmp);
        }

        this.saveBatch(converseList);
        // 组装Vo
        // 通过ConverseId查出所有的member
        List<Converse> allMemberList = baseMapper.selectList(wrapper);
        List<String> members = allMemberList.stream()
                .map(Converse::getMember)
                .collect(Collectors.toList());
        ConverseVo converseVo = new ConverseVo();
        converseVo.setId(converseId);
        converseVo.setType(ConverseTypeEnum.CONVERSE_TYPE_MULTI.getMessage());
        converseVo.setMembers(members);
        // 广播更新会话列表
        String[] target = new String[1];
        target[0] = converseId;
        IMUtil.notify(target, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.CONVERSE_UPDATEDM, converseVo);


        // 更新dmlist todo: 异步处理
        members.forEach(participant -> {
            userdmlistService.addConverse(participant, converseId);
        });

        // 发送系统消息
        // 把除了userId的其他用户的详细信息查出来
        UserIdsRequest userIdsRequest = new UserIdsRequest();
        userIdsRequest.setUserIds(memberIds);
        List<UserInfoDto> userInfoList = userService.getUserInfoList(userIdsRequest);
        // 组装字符串
        String concatenated = userInfoList.stream()
                .map(UserInfoDto::getNickname)
                .reduce((nickname1, nickname2) -> nickname1 + ", " + nickname2)
                .orElse("");

        SendMsgDto sendMsgDto = new SendMsgDto();
        sendMsgDto.setContent(String.format("%s 邀请 %s 加入会话", user.getNickname(),concatenated));
        sendMsgDto.setConverseId(converseId);
        messageService.sendSysMessage(sendMsgDto);
        return converseVo;
    }

    @Override
    public ConverseVo findConverseInfo(String userId, String converseId) {
        // 判空
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(converseId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询
        LambdaQueryWrapper<Converse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Converse::getConverseId, converseId);
        List<Converse> converseList = baseMapper.selectList(wrapper);

        if (converseList.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_ERROR);
        }

        boolean isMemberExists = converseList.stream()
                .map(Converse::getMember)
                .anyMatch(member -> member.equals(userId));

        if (!isMemberExists) {
            throw new GlobalException("没有获取会话信息权限", ErrorCode.NOT_FOUND_ERROR.getCode());
        }

        List<String> members = converseList.stream().map(Converse::getMember).collect(Collectors.toList());
        ConverseVo converseVo = new ConverseVo();
        converseVo.setId(converseId);
        converseVo.setType(converseList.get(0).getType());
        converseVo.setMembers(members);
        converseVo.setCreatedAt(converseList.get(0).getCreateAt());
        converseVo.setUpdatedAt(converseList.get(0).getUpdateAt());
        return converseVo;
    }

    @Override
    public Set<String> getDMConverseIds(String userId) {
        List<Userdmlist> userDMLists = userdmlistService.list(new LambdaQueryWrapper<Userdmlist>().eq(Userdmlist::getUserId, userId));
        return userDMLists.stream().map(Userdmlist::getConverseId).collect(Collectors.toSet());
    }
}
