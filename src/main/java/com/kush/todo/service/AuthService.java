package com.kush.todo.service;

import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.LoginResponseDto;
import com.kush.todo.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final JwsHeader JWS_HEADER = JwsHeader.with(MacAlgorithm.HS256).build();

    private final JwtEncoder jwtEncoder;
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.resourceserver.jwt.expiration-seconds}")
    private final int jwtExpirationInSeconds;

    public LoginResponseDto login(LoginRequestDto request) {
        AppUserResponseDto user = appUserService.findByUsername(request.username())
                                                .orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new UnauthorizedException();
        }

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer("todo-backend")
                                          .issuedAt(now)
                                          .expiresAt(now.plusSeconds(jwtExpirationInSeconds))
                                          .subject(user.id().toString())
                                          .claim("scope", "READ WRITE") //ToDo set actual permissions
                                          .claim("role", List.of("ROLE1", "ROLE2")) //ToDo set actual roles
                                          .claim("tenant", user.tenantId().toString())
                                          .build();

        return new LoginResponseDto(jwtEncoder.encode(JwtEncoderParameters.from(JWS_HEADER, claims)).getTokenValue());
    }
}
