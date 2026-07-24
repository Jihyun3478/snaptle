package com.snaptle.domain.user;

import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_kakao_id", columnNames = "kakao_id")
        }
)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "kakao_id")
    private String kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Builder
    private User(String email, String passwordHash, String kakaoId, String nickname,
                 String profileImageUrl, AuthProvider provider) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }

    public static User createEmailUser(String email, String passwordHash, String nickname) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .nickname(nickname)
                .provider(AuthProvider.EMAIL)
                .build();
    }

    public static User createKakaoUser(String kakaoId, String nickname, String profileImageUrl) {
        return User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(AuthProvider.KAKAO)
                .build();
    }

    public void updateKakaoProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
