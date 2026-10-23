package com.goldensales.backend;

import com.goldensales.backend.product.Product;
import com.goldensales.backend.product.ProductRepository;
import com.goldensales.backend.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BackendApplicationTests {
    @Test
    void searchReturnsMappedActiveProducts() {
        ProductRepository repository = mock(ProductRepository.class);
        Product product = new Product();
        product.setId(7L);
        product.setName("Rice");
        product.setPrice(new BigDecimal("42.50"));
        product.setStockQuantity(10);
        when(repository.findByActiveTrue(PageRequest.of(0, 20))).thenReturn(new PageImpl<>(List.of(product)));

        ProductService service = new ProductService(repository);
        var result = service.search(null, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("Rice", result.getContent().getFirst().name());
        verify(repository).findByActiveTrue(PageRequest.of(0, 20));
    }

    @Test
    void missingProductReturnsNotFound() {
        ProductRepository repository = mock(ProductRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ProductService service = new ProductService(repository);
        var exception = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.getById(99L));
        assertEquals(404, exception.getStatusCode().value());
    }
}
