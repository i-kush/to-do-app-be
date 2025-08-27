package com.kush.todo;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public final class TestDataBuilder {

    public static final UUID DEFAULT_TENANT_ID = UUID.randomUUID();

    private TestDataBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> Page<T> buildPage(Supplier<T> objectGenerator, int size) {
        return new PageImpl<>(IntStream.range(0, size)
                                       .mapToObj(i -> objectGenerator.get())
                                       .toList());
    }

}
