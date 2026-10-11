package edu.final_year.backendwebm.category;

import edu.final_year.backendwebm.category.dto.CategoryResponse;
import edu.final_year.backendwebm.category.dto.CreateCategoryRequest;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreService storeService;

    public CategoryResponse create(UUID storeId, String userEmail, CreateCategoryRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Category category = new Category();
        category.setName(request.name().trim());
        category.setStore(store);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> list(UUID storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return categoryRepository.findAllByStoreId(storeId).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
