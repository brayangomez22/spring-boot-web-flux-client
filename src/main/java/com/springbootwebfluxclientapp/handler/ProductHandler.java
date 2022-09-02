package com.springbootwebfluxclientapp.handler;

import com.springbootwebfluxclientapp.dto.Product;
import com.springbootwebfluxclientapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductHandler {

    @Autowired
    private ProductService productService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(productService.findById(id), Product.class));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(product -> productService.save(product).flatMap(p -> ServerResponse
                        .created(URI.create("api/v2/products/".concat(p.getId())))
                        .contentType(APPLICATION_JSON)
                        .bodyValue(p)
                )
        ).onErrorResume(error -> {
            WebClientResponseException errorResponse = (WebClientResponseException) error;
            if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ServerResponse.badRequest()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(errorResponse.getResponseBodyAsString());
            }
            return Mono.error(errorResponse);
        });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        return errorHandler(productMono
                .flatMap(product -> productService.update(product, id))
                .flatMap(product -> ServerResponse
                        .created(URI.create("api/v2/products/".concat(product.getId())))
                        .contentType(APPLICATION_JSON)
                        .bodyValue(product)
                ));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(productService.delete(id).then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        String id = request.pathVariable("id");

        return errorHandler(request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> productService.upload(file, id))
                .flatMap(product -> ServerResponse
                        .created(URI.create("api/products/".concat(id)))
                        .contentType(APPLICATION_JSON)
                        .bodyValue(product)
                ));
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response
                .onErrorResume(error -> {
                    WebClientResponseException errorResponse = (WebClientResponseException) error;
                    if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                        Map<String, Object> body = new HashMap<>();
                        body.put("error", errorResponse.getMessage());
                        body.put("timestamp", new Date());
                        body.put("status", errorResponse.getStatusCode().value());
                        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                    }
                    return Mono.error(errorResponse);
                });
    }
}