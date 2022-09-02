package com.springbootwebfluxclientapp.services;

import com.springbootwebfluxclientapp.dto.Product;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    public Flux<Product> findAll();
    public Mono<Product> findById(String id);
    public Mono<Product> save(Product product);
    public Mono<Product> update(Product product, String id);
    public Mono<Void> delete(String id);
    public Mono<Product> upload(FilePart file, String id);
}
