package com.mitocode.handler;

import com.mitocode.dto.InvoiceDTO;
import com.mitocode.model.Invoice;
import com.mitocode.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private final IInvoiceService service;

    @Qualifier("invoiceMapper")
    private final ModelMapper mapper;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), InvoiceDTO.class);
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
        Mono<InvoiceDTO> monoInvoiceDTO = req.bodyToMono(InvoiceDTO.class);

        return monoInvoiceDTO
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

        return req.bodyToMono(InvoiceDTO.class)
                .map( e -> {
                    e.setId(id);
                    return e;
                })
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

    private InvoiceDTO convertToDto(Invoice model) {
        return mapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToEntity(InvoiceDTO dto) {
        return mapper.map(dto, Invoice.class);
    }
}
