package com.kush.todo.repository;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.constant.Messages;
import com.kush.todo.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("unchecked")
class TenantAwareRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("appUserRepository") // we don't car actually what repo will be injected, it just should have this base interface
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
                                () -> tenantAwareRepository.findById(new ParameterizedTypeReference<>() {
                                }));
    }

    @Test
    void existsByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.existsById(new ParameterizedTypeReference<>() {
                                }));
    }

    @Test
    void findAllByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.findAllById(Collections.emptyList()));
    }

    @Test
    void deleteByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteById(new ParameterizedTypeReference<>() {
                                }));
    }

    @Test
    void deleteForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.delete(new ParameterizedTypeReference<>() {
                                }));
    }

    @Test
    void deleteAllByIdForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAllById(Collections.emptyList()));
    }

    @Test
    void deleteAllIterableForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAll(Collections.emptyList()));
    }

    @Test
    void deleteAllForbidden() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> tenantAwareRepository.deleteAll());
    }

    @Test
    void findByIdAndTenantIdNotFound() {
        ParameterizedTypeReference<UUID> id = new ParameterizedTypeReference<>() {
        };
        NotFoundException actual = Assertions.assertThrows(NotFoundException.class,
                                                           () -> tenantAwareRepository.findByIdAndTenantIdRequired(id, defaultTenantId));
        Assertions.assertEquals(String.format(Messages.PATTERN_NOT_FOUND, id), actual.getMessage());
    }
}
