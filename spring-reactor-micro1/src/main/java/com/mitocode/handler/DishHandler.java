package com.mitocode.handler;

import com.mitocode.dto.DishDTO;
import com.mitocode.model.Dish;
import com.mitocode.service.IDishService;
import com.mitocode.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class DishHandler {

    private final IDishService service;
    private final Validator validator;
    private final RequestValidator requestValidator;

    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), DishDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<DishDTO> monoDishDTO = req.bodyToMono(DishDTO.class);

        /*return monoDishDTO
                .flatMap(e -> {
                    Errors errors = new BeanPropertyBindingResult(e, DishDTO.class.getName());
                    validator.validate(e, errors);

                    if(errors.hasErrors()){
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(error -> new ValidationDTO(error.getField(), error.getDefaultMessage()))
                                .collectList()
                                .flatMap(list -> ServerResponse.badRequest()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(list))
                                );
                    }else{
                        return service.save(this.convertToEntity(e))
                                .map(this::convertToDto)
                                .flatMap(dto -> ServerResponse
                                        .created(URI.create(req.uri().toString().concat("/").concat(e.getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(dto))
                                );
                    }
                });*/

        return monoDishDTO
                .flatMap(requestValidator::validate)
                .flatMap(e -> service.save(this.convertToEntity(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                );
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        return req.bodyToMono(DishDTO.class)
                .map( e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(requestValidator::validate)
                .flatMap(e -> service.update(convertToEntity(e), id))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.delete(id)
                .flatMap(result -> {
                    if(result){
                        return ServerResponse.noContent().build();
                    }else{
                        return ServerResponse.notFound().build();
                    }
                });
    }

    private DishDTO convertToDto(Dish model) {
        return mapper.map(model, DishDTO.class);
    }

    private Dish convertToEntity(DishDTO dto) {
        return mapper.map(dto, Dish.class);
    }
}
