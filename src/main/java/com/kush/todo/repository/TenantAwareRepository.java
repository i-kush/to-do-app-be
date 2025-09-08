package com.kush.todo.repository;

import com.kush.todo.constant.Messages;
import com.kush.todo.exception.NotFoundException;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface TenantAwareRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

    @Override
    default Iterable<T> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Iterable<T> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Page<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> findById(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean existsById(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Iterable<T> findAllById(Iterable<ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteById(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void delete(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteAllById(Iterable<? extends ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteAll(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException();
    }

    Page<T> findAllByTenantId(Pageable pageable, UUID tenantId);

    Optional<T> findByIdAndTenantId(ID id, UUID tenantId);

    default T findByIdAndTenantIdRequired(ID id, UUID tenantId) {
        return findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(String.format(Messages.PATTERN_NOT_FOUND, id)));
    }

    boolean existsByIdAndTenantId(ID id, UUID tenantId);

    Iterable<T> findAllByIdAndTenantId(Iterable<ID> ids, UUID tenantId);
}
