package com.linkhub.portal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.model.common.*;
import com.linkhub.common.model.dto.user.*;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.pojo.UserSetting;
import com.linkhub.common.model.vo.UserVo;
import com.linkhub.common.utils.MapUtils;
import com.linkhub.common.utils.R;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IUserService;

import com.linkhub.portal.service.impl.EmailService;
import com.linkhub.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author CYY&winter
 * @since 2022-11-20
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Validated // 对于单个参数的校验必须加上注解
public class UserController {
    @Autowired
    IUserService userService;
    @Autowired
    EmailService emailService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public R register(@RequestBody @Validated RegisterUser registerUser) {
        int flag = userService.register(registerUser);
        return flag > 0 ? R.ok().message("注册成功") : R.error().message("注册失败");
    }

    @GetMapping("/code/{mail}")
    @ApiOperation("获取验证码by mail")
    public R getCode(@PathVariable @Email String mail) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, mail);
        User user = userService.getOne(wrapper);
        if (ObjectUtils.isEmpty(user)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "邮箱未注册，请先注册该邮箱");
        }
        emailService.sendVerifyCode(mail);
        return R.ok().message("发送验证码成功！");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R login(@RequestBody @Validated LoginForm loginForm){
        String token = userService.login(loginForm.getEmail(), loginForm.getPassword());
        return R.ok()
                .data("token",token)
                .data("tokenHead",tokenHead);
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forget")
    public R forgetPassword(@RequestBody @Validated ForgetPassForm forgetPassForm){
        return userService.forgetPassword(forgetPassForm.getOtp(),forgetPassForm.getEmail(),forgetPassForm.getPassword());
    }

    @ApiOperation("更新密码")
    @PutMapping("/updatePass")
    public R updatePassword(@RequestBody @Validated UpdatePassForm updatePassForm){
        return userService.updatePassword(updatePassForm.getOldPassword(),updatePassForm.getNewPassword());
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/userDetail")
    public R getUserDetail(){
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return R.ok().data("data", userVo);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("/editDetail")
    public R editUserDetail(@Validated UpdateUserDto updateUserDto) {
        int flag = userService.updateUserDetail(updateUserDto);
        return flag > 0 ? R.ok() : R.error();
    }

    @ApiOperation("创建临时用户")
    @PostMapping("/createTemporaryUser")
    public R createTemporaryUser(@RequestBody UserNameRequest userNameRequest) {
        String token = userService.createTemporaryUser(userNameRequest);
        return R.ok()
                .data("token",tokenHead + token);
    }

    @ApiOperation("认领临时用户")
    @PostMapping("/claimTemporaryUser")
    public R claimTemporaryUser(@RequestBody @Validated ClaimUserDto claimUserDto) {
        String token = userService.claimTemporaryUser(claimUserDto);
        return R.ok()
                .data("token",tokenHead + token);
    }

    @ApiOperation("记住登录（使用token登录）")
    @PostMapping("/resolveToken")
    public R resolveToken(@RequestBody TokenRequest tokenRequest) {
        // 通过token解析到id，然后查询用户信息返回
        UserInfoDto userInfoDto  = userService.resolveToken(tokenRequest);
        Map<String, Object> res = MapUtils.convertToMap(userInfoDto);
        return R.ok()
                .data(res)
                .data("token", tokenRequest.getToken()); // todo: 这里会出现两个data，转map?
    }

    @ApiOperation("使用唯一标识名搜索用户名")
    @PostMapping("/searchUserWithUniqueName")
    public R searchUserWithUniqueName(@RequestBody UniqueNameRequest uniqueNameRequest) {
        // uniqueName 由 nickname + '-' + discriminator 组成 注意 # 在URL中会被解析为位置标志符，拿不到
        UserInfoDto userInfoDto  = userService.searchUserWithUniqueName(uniqueNameRequest);
        Map<String, Object> res = MapUtils.convertToMap(userInfoDto);
        return R.ok()
                .data(res);
    }

    @ApiOperation("获取用户设置")
    @GetMapping("/getUserSettings")
    public R getUserSettings() {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        UserSetting userSetting  = userService.getUserSettings(user);
        Map<String, Object> res = MapUtils.convertToMap(userSetting);
        return R.ok()
                .data(res);
    }

    @ApiOperation("批量获取好友信息")
    @PostMapping("/getUserInfoList")
    public R getUserInfoList(@RequestBody  UserIdsRequest userIdsRequest) {
        List<UserInfoDto> userInfoDtoList = userService.getUserInfoList(userIdsRequest);
        Map<String, Object> res = MapUtils.convertToMap(userInfoDtoList);
        return R.ok()
                .data(res);
    }

    @ApiOperation("添加好友")
    @PostMapping("/add")
    public R addFriend(@RequestBody  FriendRequest friendRequest) {
        LinkhubUserDetails userDetails = SecurityUtils.getLoginObj();
        User user = userDetails.getUser();
        AddFriendDto addFriendDto = userService.addFriend(user, friendRequest);
        Map<String, Object> res = MapUtils.convertToMap(userInfoDtoList);
        return R.ok()
                .data(res);
    }
}
