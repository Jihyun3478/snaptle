package com.snaptle.global.security.oauth2;

import com.snaptle.domain.user.User;
import com.snaptle.domain.user.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        User user = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId())
                .map(existing -> {
                    existing.updateKakaoProfile(kakaoUserInfo.getNickname(), kakaoUserInfo.getProfileImageUrl());
                    return existing;
                })
                .orElseGet(() -> userRepository.save(User.createKakaoUser(
                        kakaoUserInfo.getKakaoId(),
                        kakaoUserInfo.getNickname(),
                        kakaoUserInfo.getProfileImageUrl()
                )));

        return new OAuth2UserPrincipal(user.getId(), oAuth2User.getAttributes());
    }
}
