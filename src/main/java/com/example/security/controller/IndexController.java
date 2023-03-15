package com.example.security.controller;

import com.example.security.config.auth.PrincipalDetails;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 일반 로그인 시 데이터 조회 방법
    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails){
        // PrincipalDetails를 상속했으므로 UserDetails대신 PrincipalDetails 타입을 이용해 User객체를 불러올 수 있다.
        System.out.println("/test/login ================");
        PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
        System.out.println("authentication:" + principalDetails.getUser());
//        System.out.println("userDetails : " + userDetails.getUsername());
        System.out.println("userDetails : " + userDetails.getUser());
        return "세선 정보 확인하기";
    }

    // 소셜 api를 이용한 로그인 시 데이터 확인 방법
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication,
                                               @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/login ================");
        OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
        System.out.println("authentication:" + oauth2User.getAttributes());
        System.out.println("oauth2USer:" + oauth.getAttributes());
        return "OAuth 세선 정보 확인하기";
    }

    @GetMapping({"/"})
    public String index(){
        // mustache 템플릿을 이용하여 생성
        return "index";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        return "user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public String manager(){
        return "manager";
    }

    // login은 spring security가 갖고있는 기본 페이지와 이름이 겹침
    // securityConfig 파일 작성 후 security의 login 페이지로 안가짐(작동안함)
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");
        // 회원가입, 비밀번호 = 1234 -> 시큐리티로 로그인 불가, 패스워드 암호화가 되지 않았기 때문
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")  // 특정 메서드에 권한을 주는 방법(한개의권한)
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    // 여러개의 권한을 걸고 싶을 때
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }


}
