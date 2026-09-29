package edu.final_year.backendwebm.category;

import edu.final_year.backendwebm.category.dto.CategoryResponse;
import edu.final_year.backendwebm.category.dto.CreateCategoryRequest;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreService storeService;

    public CategoryResponse create(Long storeId, String userEmail, CreateCategoryRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Category category = new Category();
        category.setName(request.name().trim());
        category.setStore(store);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> list(Long storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return categoryRepository.findAllByStoreId(storeId).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
