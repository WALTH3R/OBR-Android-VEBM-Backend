package edu.final_year.backendwebm.product;

import edu.final_year.backendwebm.product.dto.ProductRequest;
import edu.final_year.backendwebm.product.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@PathVariable Long storeId,
                                                    @Valid @RequestBody ProductRequest request,
                                                    Authentication authentication) {
        ProductResponse response = productService.create(storeId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ProductResponse> list(@PathVariable Long storeId, Authentication authentication) {
        return productService.list(storeId, authentication.getName());
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable Long storeId,
                                   @PathVariable Long productId,
                                   @Valid @RequestBody ProductRequest request,
                                   Authentication authentication) {
        return productService.update(storeId, productId, authentication.getName(), request);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long storeId,
                                        @PathVariable Long productId,
                                        Authentication authentication) {
        productService.delete(storeId, productId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
