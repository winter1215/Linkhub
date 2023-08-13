package com.linkhub.portal.security;

import com.linkhub.common.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author CYY
 * @date 2022年11月24日 下午10:30
 * @description
 */
@AllArgsConstructor
@Getter
@Setter
public class LinkhubUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
    * 邮箱是唯一的
    */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否被封禁
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getBanned();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
