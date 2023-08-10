package com.linkhub.portal.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.RedisPrefix;
import com.linkhub.common.mapper.UserSettingMapper;
import com.linkhub.common.model.dto.user.ClaimUserDto;
import com.linkhub.common.model.dto.user.UpdateUserDto;
import com.linkhub.common.model.dto.user.UserInfoDto;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.mapper.UserMapper;
import com.linkhub.common.model.pojo.UserSetting;
import com.linkhub.common.utils.R;
import com.linkhub.common.model.dto.user.RegisterUser;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IUserCacheService;
import com.linkhub.portal.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.security.util.JwtTokenUtil;
import com.linkhub.security.util.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CYY&winter
 * @since 2022-11-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    RedisCache redisCache;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private IUserCacheService userCacheService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String emailSuffix = ".temporary@linkhub.com";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int register(RegisterUser registerUser) {
        String nickname = registerUser.getNickname();
        String email = registerUser.getEmail();

        // check if mail is unique
        User user = baseMapper.selectUserByMail(email);
        if (ObjectUtils.isNotEmpty(user)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册");
        }

        // todo: verify mail
        User newUser = new User();
        String discriminator = generateDiscriminator(nickname, 10);
        BeanUtils.copyProperties(registerUser, newUser);
        newUser.setDiscriminator(discriminator);
        // encrypt pwd
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        baseMapper.insert(newUser);
        // insert userSetting record
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(newUser.getId());
        return userSettingMapper.insert(userSetting);
    }


    /**
     * 生成数据库唯一的 nickname 对应的随机数 discriminator
     * @param nickname:
     * @return: java.lang.String
     * @author: winter
     * @date: 2023/8/9 下午5:43
     * @description:
     */
    private String generateDiscriminator(String nickname, int restTimes) {
        if (restTimes <= 0) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "为当前用户分配随机码失败，请多次重试");
        }
        // generate 4-bit random numbers of string type
        String discriminator = RandomUtil.randomNumbers(4);
        // check if discriminator is unique
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNickname, nickname);
        wrapper.eq(User::getDiscriminator, discriminator);
        User user = baseMapper.selectOne(wrapper);
        if (ObjectUtils.isNotEmpty(user)) {
            return generateDiscriminator(nickname, --restTimes);
        }
        return discriminator;
    }

    /**
     * 用户登录
     * @return 登录成功返回token失败抛出异常交于GlobalExceptionHandler统一发送error消息
     *
     */
    @Override
    public String login(String email, String password) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("邮箱或密码不正确");
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return jwtTokenUtil.generateToken(userDetails);
        }catch (AuthenticationException e){
            throw new GlobalException(ErrorCode.USERNAME_PASSWORD_ERROR.getMessage(), ErrorCode.USERNAME_PASSWORD_ERROR.getCode());
        }
    }

    /**
     * 通过用户名获取user对象(实现UserDetails的对象)，更新user的禁言状态
     * @param email 用户名(邮箱)
     * @return 不存在user抛出springSecurity的内置异常UsernameNotFoundException,反之返回对象
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = getUserByUsername(email);
        if(user == null) throw new UsernameNotFoundException(ErrorCode.USERNAME_PASSWORD_ERROR.getMessage());
        return new LinkhubUserDetails(user);
    }
    /**
     * 通过用户名从redis或者数据库获取用户，redis不存在会从数据库查询并将结果存入redis如果数据库中不存在则返回null
     * @param email 用户名
     * @return 用户对象或者null
     */
    @Override
    public User getUserByUsername(String email) {
        User user = userCacheService.getUserByUsername(email);
        if(user == null){
            user = userMapper.selectUserByMail(email);
            if(user != null) userCacheService.setUser(user);
        }
        return user;
    }
    /**
     * 更新用户，通过用户名更新redis的user和通过id更新数据库的user
     * todo 考虑事务
     * @param user user
     */
    @Override
    public int updateUser(User user) {
        userCacheService.setUser(user);
        return userMapper.updateById(user);
    }
    /**
     * 忘记密码，验证验证码是否正确，再通过邮箱获取对应用户更改其密码
     *
     * @param code 验证码
     * @param email 邮箱
     * @param password 新密码
     * @return 是否成功
     */
    @Override
    public R forgetPassword(String code, String email, String password) {
        // 获取验证码
        String verifyCode = redisCache.getCacheObject(String.format(RedisPrefix.PREFIX_VERIFY_CODE, email));
        if (verifyCode == null) {
            throw new GlobalException("请先获取验证码", ErrorCode.PARAMS_ERROR.getCode());
        }
        if (!verifyCode.equals(code)) {
            throw new GlobalException("验证码有误", ErrorCode.PARAMS_ERROR.getCode());
        }
        // 检查邮箱是否能找到对应用户
        User user = userMapper.selectUserByMail(email);
        if(user == null){
            throw new GlobalException("邮箱有误", ErrorCode.PARAMS_ERROR.getCode());
        }
        // 更新密码
        user.setPassword(passwordEncoder.encode(password));
        updateUser(user);
        return R.ok().message("修改密码成功");
    }

    /**
     * 更新用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public R updatePassword(String oldPassword, String newPassword) {
        LinkhubUserDetails user = SecurityUtils.getLoginObj();
        if(user == null){
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR.getMessage(),ErrorCode.NO_AUTH_ERROR.getCode());
        }
        if(!passwordEncoder.matches(oldPassword,user.getUser().getPassword())){   //旧密码不匹配
            throw new GlobalException(ErrorCode.PASSWORD_ERROR.getMessage(),ErrorCode.PARAMS_ERROR.getCode());
        }
        user.getUser().setPassword(passwordEncoder.encode(newPassword));
        updateUser(user.getUser());
        return R.ok().message("密码修改成功");
    }

    @Override
    public int updateUserDetail(UpdateUserDto updateUserDto) {
        LinkhubUserDetails user = SecurityUtils.getLoginObj();
        String userId = user.getUser().getId();
        User updateUser = new User();
        updateUser.setId(userId);
        BeanUtils.copyProperties(updateUserDto, updateUser);
        return updateUser(updateUser);
    }

    /**
     * 创建一个临时账号
     * @param nickname
     * @return
     */
    @Override
    public String createTemporaryUser(String nickname) {
        // 设置为临时账户和设置用户名
        User temporaryUser = new User();
        temporaryUser.setNickname(nickname);
        temporaryUser.setTemporary(true);
        // 1.创建唯一标识符
        String discriminator = generateDiscriminator(nickname, 10);
        temporaryUser.setDiscriminator(discriminator);
        // 2.创建随机密码和随机邮箱
        String email = RandomStringUtils.randomAlphanumeric(10) + emailSuffix;
        temporaryUser.setEmail(email);
        String password = passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(10));
        temporaryUser.setPassword(password);
        // 3.User写入数据库
        baseMapper.insert(temporaryUser);
        // 4.UserSetting写入数据库
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(temporaryUser.getId());
        userSettingMapper.insert(userSetting);
        // 5.创建token并返回
        UserDetails userDetails = loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return jwtTokenUtil.generateToken(userDetails);
    }

    /**
     * 认领临时用户
     * @param claimUserDto 认领用户的dto
     * @return
     */
    @Override
    public String claimTemporaryUser(ClaimUserDto claimUserDto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(User::getId, claimUserDto.getUserId());
        User user = baseMapper.selectOne(wrapper);


        if (ObjectUtils.isEmpty(user)) {
            throw new GlobalException("要认领的用户不存在", ErrorCode.NOT_FOUND_ERROR.getCode());
        }

        if (!user.getTemporary()) {
            throw new GlobalException("该用户不是临时用户", ErrorCode.PARAMS_ERROR.getCode());
        }

        // 更新数据
        user.setEmail(claimUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(claimUserDto.getPassword()));
        user.setTemporary(false);
        // 更新到数据库
        baseMapper.updateById(user);
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public UserInfoDto resolveToken(String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token);
        // 通过email查询user信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = baseMapper.selectOne(wrapper);
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(user, userInfoDto);
        return userInfoDto;
    }

    @Override
    public UserInfoDto searchUserWithUniqueName(String uniqueName) {
        String[] parts = uniqueName.split("-"); // parts[0] 是nickName parts[1]是discriminator
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNickname, parts[0])
                .eq(User::getDiscriminator, parts[1]);
        User user = baseMapper.selectOne(wrapper);
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(user, userInfoDto);
        return userInfoDto;
    }
}
