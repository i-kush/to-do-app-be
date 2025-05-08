package com.kush.todo;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.entity.Tenant;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public final class TestDataBuilder {

    private TestDataBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> Page<T> buildPage(Supplier<T> objectGenerator, int size) {
        return new PageImpl<>(IntStream.range(0, size)
                                       .mapToObj(i -> objectGenerator.get())
                                       .toList());
    }

    public static TenantRequestDto buildTenantRequestDto() {
        return TenantRequestDto.builder()
                               .name(UUID.randomUUID().toString())
                               .build();
    }

    public static Tenant buildTenant() {
        Tenant tenant = new Tenant();

        tenant.setId(UUID.randomUUID());
        tenant.setName(UUID.randomUUID().toString());
        tenant.setCreated(Instant.now());
        tenant.setUpdated(Instant.now().minus(10, ChronoUnit.DAYS));

        return tenant;
    }
}
