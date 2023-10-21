package com.mitocode.controller;

import com.mitocode.dto.DishDTO;
import com.mitocode.model.Dish;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IDishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService service;
    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<DishDTO>>> findAll() {
        //ModelMapper mapper = new ModelMapper();
        //Flux<DishRecord> fx = service.findAll().map(e -> new DishRecord(e.getId(), e.getName(), e.getPrice(), e.getStatus()));
        Flux<DishDTO> fx = service.findAll().map(this::convertToDto);

        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(this::convertToDto) //e -> this.convertToDto(e)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<DishDTO>> save(@Valid @RequestBody DishDTO dto, final ServerHttpRequest req) {
        return service.save(convertToEntity(dto))
                //localhost:8080/dishes/123123
                .map(this::convertToDto)
                .map(e -> ResponseEntity.created(
                        URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                        //.build()
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DishDTO>> update(@Valid @RequestBody DishDTO dto, @PathVariable("id") String id) {
        return Mono.just(dto)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(e -> service.update(convertToEntity(dto), id))
                .map(this::convertToDto)
                .map(e -> ResponseEntity
                        .ok()
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id) {
        return service.delete(id)
                .flatMap(result -> {
                    if(result){
                        return Mono.just(ResponseEntity.noContent().build());
                    }else{
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    ////////////////////////////////////////
    private DishDTO dishHateoas;

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<DishDTO>> getHateoas(@PathVariable("id") String id){
        //localhost:8080/dishes/{id}
        Mono<Link> monoLink = linkTo(methodOn(DishController.class).findById(id)).withSelfRel().toMono();

        //PRACTICA ES COMUN PERO NO RECOMENDADA
        /*return service.findById(id)
                .map(this::convertToDto)
                .flatMap(d -> {
                    this.dishHateoas = d;
                    return monoLink;
                })
                .map(link -> EntityModel.of(this.dishHateoas,link));*/

        //PRACTICA INTERMEDIA
        /*return service.findById(id)
                .map(this::convertToDto)
                .flatMap(d -> monoLink.map(link -> EntityModel.of(d, link)));*/

        //PRACTICA IDEAL
        return service.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink, EntityModel::of); //(d, link) -> EntityModel.of(d, link)
    }

    ///pageable/////
    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<DishDTO>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size
    ){
        return service.getPage(PageRequest.of(page, size))
                .map(pageSupport -> {
                    PageSupport<DishDTO> dtoPageSupport = new PageSupport<>(
                            pageSupport.getContent().stream().map(this::convertToDto).toList(),
                            pageSupport.getPageNumber(),
                            pageSupport.getPageSize(),
                            pageSupport.getTotalElements()
                    );

                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(dtoPageSupport);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private DishDTO convertToDto(Dish model){
        return mapper.map(model, DishDTO.class);
    }

    private Dish convertToEntity(DishDTO dto){
        return mapper.map(dto, Dish.class);
    }

    /*@GetMapping("/test")
    public Mono<Object> test()  {
        int x = 5;
        System.out.println("Valor de x al inicio: " + x);

        Mono<Object> m1 = service.test1(x)
                .flatMap(result1 -> {
                    // Trabaja con el resultado de test1 aquí
                    System.out.println("test1: " + result1);
                    return service.test2(result1);
                })
                .map(result2 -> {
                    // Trabaja con el resultado de test2 aquí
                    System.out.println("test2: " + result2);
                    return ResponseEntity.ok().body(result2);
                });

        //x = 10;

        System.out.println("Valor de x al final: " + x);

        return m1;
    }*/
}
