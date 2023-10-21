package com.mitocode.service;

import com.mitocode.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICRUD<T, ID> {

    Mono<T> save(T t);

    Mono<T> update(T t, ID id);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<Boolean> delete(ID id);
    Mono<PageSupport<T>> getPage(Pageable page);
}
