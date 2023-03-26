package com.example.security.config.oauth;

import com.example.security.config.auth.PrincipalDetails;
import com.example.security.config.oauth.provider.FacebookUserInfo;
import com.example.security.config.oauth.provider.GoogleUserInfo;
import com.example.security.config.oauth.provider.OAuth2UserInfo;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후처리 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // registrationId로 어떤 OAuth로 로그인했는지 확인할 수 있음, {clientName='Google'}
        System.out.println("getClientRegistration : "+userRequest.getClientRegistration());
        System.out.println("getAccessToken : "+userRequest.getAccessToken());
//        getAccessToken : org.springframework.security.oauth2.core.OAuth2AccessToken@800fd12

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인완료 -> code리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필 정보 받음
        System.out.println("getAttributes : "+oAuth2User.getAttributes());
//        getAttributes : {sub=115215194196672527373, name=Lois Kim, given_name=Lois, family_name=Kim,
//        picture=https://lh3.googleusercontent.com/a/AGNmyxZzZxIccaxAkey_suSdr9V9INh7cltDXZ88t8M=s96-c,
//        email=loiskim150@gmail.com, email_verified=true, locale=ko}

        // 각 소셜 서비스 별 로그인 진행 시키기
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("Google Login Request");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("Facebook Login Request");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else{
            System.out.println("구글과 페이스북만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();    // google
        String providerId = oAuth2UserInfo.getProviderId(); // 115215194196672527373
        String username = provider+"_"+providerId;  // google_115215194196672527373
        String email = oAuth2UserInfo.getEmail();
        String password = bCryptPasswordEncoder.encode("getinthere");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);    // 가입된 유저인지 확인
        if(userEntity == null){
            System.out.println("First Google login");
            // 기존 정보가 없는 경우(신규회원가입)
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("Already Google Logined. You are a member.");
        }

//        return super.loadUser(userRequest);
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());    // OAuth2User 타입 리턴
    }
}
