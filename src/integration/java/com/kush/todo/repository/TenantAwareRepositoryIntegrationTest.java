package com.kush.todo.repository;

import com.kush.todo.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class TenantAwareRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TenantAwareRepository tenantAwareRepository;

    @Test
    void finAllForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findAll());
    }

    @Test
    void findAllWithSortForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findAll(Sort.unsorted()));
    }

    @Test
    void findAllWithPageableForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findAll(Pageable.unpaged()));
    }

    @Test
    void findByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findById(1L));
    }

    @Test
    void existsByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.existsById(1L));
    }

    @Test
    void findAllByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findAllById(List.of(1L, 2L)));
    }

    @Test
    void deleteByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteById(1L));
    }

    @Test
    void deleteForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.delete(new Object()));
    }

    @Test
    void deleteAllByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAllById(List.of(1L, 2L)));
    }

    @Test
    void deleteAllIterableForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAll(List.of(new Object())));
    }

    @Test
    void deleteAllForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAll());
    }
}
