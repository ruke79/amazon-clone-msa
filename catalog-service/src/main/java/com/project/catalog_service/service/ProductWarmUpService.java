package com.project.catalog_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.catalog_service.model.Category;
import com.project.catalog_service.model.Subcategory;
import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.SubcategoryRepository;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductWarmUpService {

     private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    /**
     * 모든 카테고리/서브카테고리별 상품 목록을 순회하며 캐시를 미리 채웁니다 (Warm-up).
     */    
    @PostConstruct
    @Transactional(readOnly = true) // 이 메서드에 트랜잭션을 적용
    public void warmUpProductCachesOnStartup() {
        log.info("Starting to warm up product caches...");
        List<Category> allCategories = categoryRepository.findAll();
        for (Category category : allCategories) {
            List<Subcategory> allSubcategories = subcategoryRepository.findByCategory(category);
            for (Subcategory subcategory : allSubcategories) {
                productService.getProductsByCategoryAndSubcategory(
                    category.getCategoryName(), subcategory.getSubcategoryName(), 0, 20);
            }
        }
        log.info("Product cache warm-up completed.");
    }

}
