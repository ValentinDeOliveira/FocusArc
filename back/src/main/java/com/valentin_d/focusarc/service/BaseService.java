package com.valentin_d.focusarc.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.function.Supplier;

public abstract class BaseService {
    protected <T, ID> T fetchOrThrow(final MongoRepository<T, ID> repository, final ID id,
                                     final Supplier<? extends RuntimeException> ex) {
        return repository.findById(id).orElseThrow(ex);
    }

    protected <T, ID> void existsOrThrow(final MongoRepository<T, ID> repository, final ID id,
                                         final Supplier<? extends RuntimeException> ex) {
        if (!repository.existsById(id)) {
            throw ex.get();
        }
    }
}