package com.snaptle.domain.auth;

import com.snaptle.domain.auth.dto.LoginRequest;
import com.snaptle.domain.auth.dto.SignUpRequest;
import com.snaptle.domain.auth.dto.TokenResponse;
import com.snaptle.domain.user.User;
import com.snaptle.domain.user.UserRepository;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import com.snaptle.global.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new SnaptleException(ErrorCode.DUPLICATE_EMAIL);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = userRepository.save(User.createEmailUser(request.email(), passwordHash, request.nickname()));

        return TokenResponse.ofBearer(jwtTokenProvider.createAccessToken(user.getId()));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new SnaptleException(ErrorCode.INVALID_CREDENTIALS));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new SnaptleException(ErrorCode.INVALID_CREDENTIALS);
        }

        return TokenResponse.ofBearer(jwtTokenProvider.createAccessToken(user.getId()));
    }
}
