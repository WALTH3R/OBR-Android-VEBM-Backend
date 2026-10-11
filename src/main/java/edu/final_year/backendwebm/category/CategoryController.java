package edu.final_year.backendwebm.category;

import edu.final_year.backendwebm.category.dto.CategoryResponse;
import edu.final_year.backendwebm.category.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@PathVariable UUID storeId,
                                                     @Valid @RequestBody CreateCategoryRequest request,
                                                     Authentication authentication) {
        CategoryResponse response = categoryService.create(storeId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CategoryResponse> list(@PathVariable UUID storeId, Authentication authentication) {
        return categoryService.list(storeId, authentication.getName());
    }
}
