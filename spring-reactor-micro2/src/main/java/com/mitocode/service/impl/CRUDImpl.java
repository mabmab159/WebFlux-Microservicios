package com.mitocode.service.impl;

import com.mitocode.pagination.PageSupport;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.ICRUD;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(T t, ID id) {
        return getRepo().findById(id).flatMap(e -> getRepo().save(t));
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .hasElement()
                .flatMap(result -> {
                    if(result){
                        return getRepo().deleteById(id).thenReturn(true); //.then(Mono.just(true));
                    }else{
                        return Mono.just(false);
                    }
                });
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable page) {
        return getRepo().findAll() //Flux<T>
                .collectList() //Mono<List<T>>
                .map(list -> new PageSupport<>(
                        list.stream()
                                .skip(page.getPageNumber() * page.getPageSize())
                                .limit(page.getPageSize())
                                .collect(Collectors.toList()),
                        page.getPageNumber(),
                        page.getPageSize(),
                        list.size()
                ));
    }
}
