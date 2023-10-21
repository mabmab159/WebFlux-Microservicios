package com.mitocode.controller;

import com.mitocode.dto.InvoiceDTO;
import com.mitocode.model.Invoice;
import com.mitocode.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService service;
    @Qualifier("invoiceMapper")
    private final ModelMapper mapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<InvoiceDTO>>> findAll() {
        Flux<InvoiceDTO> fx = service.findAll().map(this::convertToDto);

        return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(this::convertToDto) //e -> this.convertToDto(e)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> save(@RequestBody InvoiceDTO dto, final ServerHttpRequest req) {
        return service.save(convertToEntity(dto))
                //localhost:8080/invoicees/123123
                .map(e -> ResponseEntity.created(
                        URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> update(@RequestBody InvoiceDTO dto, @PathVariable("id") String id) {
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

    ////////////////////

    @GetMapping("generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id){
        return service.generateReport(id)
                .map(bytes -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private InvoiceDTO convertToDto(Invoice model){
        return mapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToEntity(InvoiceDTO dto){
        return mapper.map(dto, Invoice.class);
    }


}
