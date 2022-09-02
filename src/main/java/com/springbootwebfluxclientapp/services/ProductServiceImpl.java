package com.springbootwebfluxclientapp.services;

import com.springbootwebfluxclientapp.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.*;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private WebClient.Builder client;

    @Override
    public Flux<Product> findAll() {
        return client.build().get()
                .accept(APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Product.class));
    }

    @Override
    public Mono<Product> findById(String id) {
        return client.build().get()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(APPLICATION_JSON)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Product.class));
    }

    @Override
    public Mono<Product> save(Product product) {
        return client.build().post()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> update(Product product, String id) {
        return client.build().put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return client.build().delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Product> upload(FilePart file, String id) {
        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.asyncPart("file", file.content(), DataBuffer.class).headers(httpHeaders ->
                httpHeaders.setContentDispositionFormData("file", file.filename()));

        return client.build().post()
                .uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MULTIPART_FORM_DATA)
                .bodyValue(parts.build())
                .retrieve()
                .bodyToMono(Product.class);
    }
}
