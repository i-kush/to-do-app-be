package com.kush.todo;

import com.kush.todo.dto.Role;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TestDataBuilder {

    public static final UUID DEFAULT_TENANT_ID = UUID.randomUUID();
    public static final String DEFAULT_JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlN2M2NDZlMC01Y2I0LTQxYzctYjEyMy1mZDUzN2NkYmY5MjIiLCJyb2xlIjoiR0xPQkFMX0FETUlOIiwic2NvcGUiOiJSRUFEIFdSSVRFIiwiZXhwIjoxNzU2Mzk2MTQ0LCJpYXQiOjE3NTYzOTU4NDQsInRlbmFudCI6ImViMWE1NjFmLTczZTItNDk0Ni05MzdiLTEwMzlmYTRhZWQ3NiIsImVtYWlsIjoic3lzdGVtLWFkbWluQGt1c2gtdG8tZG8uY29tIiwidXNlcm5hbWUiOiJnbG9iYWwtYWRtaW4ifQ.1NgDV_F41hRItVWguOE1nEG2H8ewyySBJrlHIwksBec";

    private TestDataBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> Page<T> buildPage(Supplier<T> objectGenerator, int size) {
        return new PageImpl<>(IntStream.range(0, size)
                                       .mapToObj(i -> objectGenerator.get())
                                       .toList());
    }

    public static Jwt buildJwt() {
        return Jwt.withTokenValue(DEFAULT_JWT)
                  .header("alg", "HS256")
                  .subject(UUID.randomUUID().toString())
                  .claim("tenant", UUID.randomUUID().toString())
                  .claim("role", Role.USER.toString())
                  .claim("username", "u" + UUID.randomUUID())
                  .claim("email", "e" + UUID.randomUUID())
                  .build();
    }
}
