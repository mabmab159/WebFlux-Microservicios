package com.mitocode.service.impl;

import com.mitocode.model.Dish;
import com.mitocode.repo.IDishRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }

    /*public DishServiceImpl(IDishRepo repo) {
        this.repo = repo;
    }*/


    /*public Mono<Integer> test1(int x) {
        x = 6;
        return Mono.just(x).delayElement(Duration.ofSeconds(2));
    }

    public Mono<Integer> test2(int x)  {
        x = 7;
        return Mono.just(x).delayElement(Duration.ofSeconds(2));
    }
*/
}
