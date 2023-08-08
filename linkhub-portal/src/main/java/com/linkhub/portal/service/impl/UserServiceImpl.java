package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.AuthStatus;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.RedisPrefix;
import com.linkhub.common.model.dto.UpdateUserDto;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.mapper.UserMapper;
import com.linkhub.common.utils.R;
import com.linkhub.common.model.dto.RegisterUser;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IUserCacheService;
import com.linkhub.portal.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.security.util.JwtTokenUtil;
import com.linkhub.security.util.SecurityUtils;
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

import java.time.LocalDateTime;

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
    private IUserCacheService userCacheService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public int register(RegisterUser registerUser) {
        String username = registerUser.getUsername();
        String mail = registerUser.getMail();
        User user;
        // 检查用户名是否存在
        user = baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if(user != null) {
            throw new GlobalException("用户已存在", ErrorCode.PARAMS_ERROR.getCode());
        }
        // 检查邮箱是否重复
        user = baseMapper.selectOne(new QueryWrapper<User>().eq("mail", mail));
        if (user != null) {
            throw new GlobalException("邮箱已存在", ErrorCode.PARAMS_ERROR.getCode());
        }
        // 获取验证码
        String code = redisCache.getCacheObject(String.format(RedisPrefix.PREFIX_VERIFY_CODE, mail));
        if (code == null) {
            throw new GlobalException("请先获取验证码", ErrorCode.PARAMS_ERROR.getCode());
        }
        if(!code.equals(registerUser.getCode())) {
            throw new GlobalException("验证码有误", ErrorCode.PARAMS_ERROR.getCode());
        }
        // 验证成功后删除缓存中的验证码
        redisCache.deleteObject(String.format(RedisPrefix.PREFIX_VERIFY_CODE, mail));
        User newUser = new User();
        BeanUtils.copyProperties(registerUser, newUser);
        // 加密密码
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return baseMapper.insert(newUser);
    }
    /**
     * 用户登录
     * @return 登录成功返回token失败抛出异常交于GlobalExceptionHandler统一发送error消息
     *
     */
    @Override
    public String login(String username, String password) {
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("用户名或密码不正确");
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
     * @param username 用户名
     * @return 不存在user抛出springSecurity的内置异常UsernameNotFoundException,反之返回对象
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = getUserByUsername(username);
        if(user == null) throw new UsernameNotFoundException(ErrorCode.USERNAME_PASSWORD_ERROR.getMessage());
        LocalDateTime now = LocalDateTime.now();
        if(AuthStatus.USER_RESTRICT.getCode() == user.getStatus() && now.isAfter(user.getBannedEnd())){
            //更新状态
            user.setStatus(AuthStatus.OK.getCode());
            updateUser(user);
        }
        return new LinkhubUserDetails(user);
    }
    /**
     * 通过用户名从redis或者数据库获取用户，redis不存在会从数据库查询并将结果存入redis如果数据库中不存在则返回null
     * @param username 用户名
     * @return 用户对象或者null
     */
    @Override
    public User getUserByUsername(String username) {
        User user = userCacheService.getUserByUsername(username);
        if(user == null){
            user = userMapper.selectUserByUsername(username);
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
        Long userId = user.getUser().getId();
        User updateUser = new User();
        updateUser.setId(userId);
        BeanUtils.copyProperties(updateUserDto, updateUser);
        return updateUser(updateUser);
    }
}
