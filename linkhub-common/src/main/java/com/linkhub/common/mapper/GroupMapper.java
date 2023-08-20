package com.linkhub.common.mapper;

import com.linkhub.common.model.pojo.Group;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkhub.common.model.vo.GroupVo;

import java.util.List;

/**
 * <p>
 * 群组 info Mapper 接口
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
public interface GroupMapper extends BaseMapper<Group> {

    /**
    * 查询完整的 Group 信息(member, roles, panels, basicInfo)
    */
    GroupVo selectGroupVoById(String groupId);
}
