package com.example.security.config.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킴
// 로그인 완료 시 시큐리티 session을 만들어 줌 (Security ContextHolder)
// 오브젝트 => Authentication 타입 객체
// Authentication 안에 User 정보가 있어야 함
// User오브젝트타입 => UserDetails 타입 객체

// Security Session(세션) => Authentication(객체) => UserDetails(PrincipalDetails)(타입)

import com.example.security.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;  // 콤포지션

    public PrincipalDetails(User user){
        this.user = user;
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 안되었는지
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겼는지
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 계정 비밀번호 만료 기간 지났는지
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 휴면계정은 아닌지
        // ex) 1년동안 회원이 로그인을 안하면 휴면 계정으로 하기로 함
        // user model에 로그인시간 컬럼을 만들어서 현재시간-로긴시간 = > 1년 초과 시 return false;
        return true;
    }
}
