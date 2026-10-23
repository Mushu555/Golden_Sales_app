package com.goldensales.backend.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ProductResponse> search(String query, Pageable pageable) {
        Page<Product> products = (query == null || query.isBlank())
                ? productRepository.findByActiveTrue(pageable)
                : productRepository.findByActiveTrueAndNameContainingIgnoreCase(query.trim(), pageable);
        return products.map(ProductResponse::from);
    }

    public ProductResponse getById(Long id) {
        return ProductResponse.from(findActive(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findActive(id);
        apply(product, request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = findActive(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findActive(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description() == null ? null : request.description().trim());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
    }
}
