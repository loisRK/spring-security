package com.example.security.config;

import com.example.security.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity  // 활성화, spring security 필터(SecurityConfig 에서 설정할 내용)가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // secure 어노테이션 활성화, preAuthorize/postAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
   
    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해줌
    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // global로 모든 사용자에 대한 권한 설정
        http.authorizeRequests()
                // user로 가면 인증이 필요하다
                .antMatchers("/user/**").authenticated()
                // manager, admin은 다음과 같은 권한이 있어야 한다
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                // 이 외의 페이지는 모든 권한이 허용됨
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")  // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해 줌
                .defaultSuccessUrl("/")    // 로그인 성공 시 main 페이지로 이동
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                // 1.코드받기(인증) 2.엑세스토큰(권한) 3.사용자프로필 정보 가져오기 4-1.그 정보로 회원가입 자동 진행
                // 4-2.(이메일,전화번호,이름,아이디) 추가정보(주소, 등급)입력을 받게하는 부분
                .userInfoEndpoint()
                .userService(principalOauth2UserService);     // 구글 로그인 완료 후 후처리 필요. Tip. 코드x, (액세스토큰+사용자프로필정보 O)
    }
}
