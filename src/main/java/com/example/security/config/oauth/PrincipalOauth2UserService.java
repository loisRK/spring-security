package com.example.security.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로부터 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // registrationId로 어떤 OAuth로 로그인했는지 확인할 수 있음, {clientName='Google'}
        System.out.println("getClientRegistration : "+userRequest.getClientRegistration());

        System.out.println("getAccessToken : "+userRequest.getAccessToken());
//        getAccessToken : org.springframework.security.oauth2.core.OAuth2AccessToken@800fd12
        System.out.println("getAttributes : "+super.loadUser(userRequest).getAttributes());
//        getAttributes : {sub=115215194196672527373, name=Lois Kim, given_name=Lois, family_name=Kim, picture=https://lh3.googleusercontent.com/a/AGNmyxZzZxIccaxAkey_suSdr9V9INh7cltDXZ88t8M=s96-c, email=loiskim150@gmail.com, email_verified=true, locale=ko}

        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인완료 -> code리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필 정보 받음

        OAuth2User oAuth2User = super.loadUser(userRequest);

        return super.loadUser(userRequest);
    }
}
