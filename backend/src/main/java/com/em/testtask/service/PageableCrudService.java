package com.em.testtask.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PageableCrudService<T, ID> {
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    T save(T entity);
    T updateById(ID id, T entity);
    void deleteById(ID id);
    Page<T> findAll(Pageable pageable);
}
