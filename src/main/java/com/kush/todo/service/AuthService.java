package com.kush.todo.service;

import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.LoginResponseDto;
import com.kush.todo.entity.AppUser;
import com.kush.todo.exception.UnauthorizedException;
import com.kush.todo.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final JwsHeader JWS_HEADER = JwsHeader.with(MacAlgorithm.HS256).build();

    private final JwtEncoder jwtEncoder;
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    @Value("${spring.security.oauth2.resourceserver.jwt.expiration-seconds}")
    private final int jwtExpirationInSeconds;

    @SuppressWarnings("PMD.ConfusingTernary") //False positive - if password is correct then we will nullify login attempts
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        AppUser user = appUserService.findByUsername(request.username())
                                     .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        if (user.isLocked()) {
            throw new UnauthorizedException("User is locked");
        }

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            appUserService.lockUserIfNeeded(user);
            throw new UnauthorizedException("Invalid username or password");
        } else if (user.loginAttempts() != null) {
            appUserService.nullifyLoginAttempts(user);
        }

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuedAt(now)
                                          .expiresAt(now.plusSeconds(jwtExpirationInSeconds))
                                          .subject(user.id().toString())
                                          .claim("scope", authMapper.toScope(appUserService.findUserPermission(user.id(), user.tenantId())))
                                          .claim("role", user.roleId().toString())
                                          .claim("tenant", user.tenantId().toString())
                                          .claim("username", user.username())
                                          .claim("email", user.email())
                                          .build();

        return LoginResponseDto.builder()
                               .accessToken(jwtEncoder.encode(JwtEncoderParameters.from(JWS_HEADER, claims)).getTokenValue())
                               .build();
    }
}
