package edu.final_year.backendwebm.product;

import edu.final_year.backendwebm.category.Category;
import edu.final_year.backendwebm.category.CategoryRepository;
import edu.final_year.backendwebm.exception.CategoryNotFoundException;
import edu.final_year.backendwebm.exception.ProductNotFoundException;
import edu.final_year.backendwebm.exception.TvaNotFoundException;
import edu.final_year.backendwebm.product.dto.ProductRequest;
import edu.final_year.backendwebm.product.dto.ProductResponse;
import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.store.StoreService;
import edu.final_year.backendwebm.tva.Tva;
import edu.final_year.backendwebm.tva.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TvaRepository tvaRepository;
    private final StoreService storeService;

    public ProductResponse create(UUID storeId, String userEmail, ProductRequest request) {
        Store store = storeService.findOwnedStore(storeId, userEmail);

        Product product = new Product();
        product.setStore(store);
        applyRequest(product, storeId, request);

        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> list(UUID storeId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);

        return productRepository.findAllByStoreId(storeId).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(UUID storeId, UUID productId, String userEmail, ProductRequest request) {
        storeService.findOwnedStore(storeId, userEmail);
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(ProductNotFoundException::new);

        applyRequest(product, storeId, request);

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID storeId, UUID productId, String userEmail) {
        storeService.findOwnedStore(storeId, userEmail);
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(ProductNotFoundException::new);

        productRepository.delete(product);
    }

    private void applyRequest(Product product, UUID storeId, ProductRequest request) {
        Category category = categoryRepository.findByIdAndStoreId(request.categoryId(), storeId)
                .orElseThrow(CategoryNotFoundException::new);
        Tva tva = tvaRepository.findById(request.tvaId())
                .orElseThrow(TvaNotFoundException::new);

        product.setName(request.name().trim());
        product.setCategory(category);
        product.setUnitOfMeasurement(request.unitOfMeasurement().trim());
        product.setCostPerUnit(request.costPerUnit());
        product.setSellPricePerUnit(request.sellPricePerUnit());
        product.setTva(tva);
    }
}
