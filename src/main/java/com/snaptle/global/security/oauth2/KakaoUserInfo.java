package com.snaptle.global.security.oauth2;

import java.util.Map;

public class KakaoUserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getKakaoId() {
        return String.valueOf(attributes.get("id"));
    }

    @SuppressWarnings("unchecked")
    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return "snaptle-user";
        }
        Object nickname = properties.get("nickname");
        return nickname != null ? nickname.toString() : "snaptle-user";
    }

    @SuppressWarnings("unchecked")
    public String getProfileImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        Object profileImage = properties.get("profile_image");
        return profileImage != null ? profileImage.toString() : null;
    }
}
