package com.project.catalog_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.catalog_service.document.CategoryDocument;
import com.project.catalog_service.document.ProductColorDocument;
import com.project.catalog_service.document.ProductDetailsDocument;
import com.project.catalog_service.document.ProductDocument;
import com.project.catalog_service.document.ProductQADocument;
import com.project.catalog_service.document.ProductSizeDocument;
import com.project.catalog_service.document.ProductSkuDocument;
import com.project.catalog_service.document.SubcategoryDocument;
import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.request.SearchParamsRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.repository.CategoryDocumentRepository;
import com.project.catalog_service.repository.ProductColorDocumentRepository;
import com.project.catalog_service.repository.ProductDetailsDocumentRepository;
import com.project.catalog_service.repository.ProductDocumentRepository;
import com.project.catalog_service.repository.ProductQADocumentRepository;
import com.project.catalog_service.repository.ProductSizeDocumentRepository;
import com.project.catalog_service.repository.ProductSkuDocumentRepository;
import com.project.catalog_service.repository.SubcategoryDocumentRepository;
import com.project.catalog_service.service.ProductService;
import com.project.catalog_service.service.SearchService;
import com.project.common.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class SearchController {

    private final SearchService searchService;
    private final ProductDocumentRepository productDocumentRepository;
    private final CategoryDocumentRepository categoryDocumentRepository;
    private final ProductColorDocumentRepository productColorDocumentRepository;
    private final ProductDetailsDocumentRepository productDetailsDocumentRepository;
    private final ProductQADocumentRepository productQADocumentRepository;
    private final ProductSkuDocumentRepository productSkuDocumentRepository;
    private final ProductSizeDocumentRepository productSizeDocumentRepository;
    private final SubcategoryDocumentRepository subcategoryDocumentRepository;

    private final JobLauncher jobLauncher;
    // @Qualifier("productIndexingJob") // 이 라인을 추가
    // private final Job productIndexingJob;
    private final Map<String, Job> allJobs; // List<Job> 대신 Map<String, Job>을 사용하여 Job 이름으로 특정 Job을 주입

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(SearchParamsRequest params) {

        SearchResultDto dto = searchService.searchProducts(params);

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @GetMapping("/products/all")
    public Iterable<ProductDocument> getAllProducts() {
        return productDocumentRepository.findAll();
    }

    @GetMapping("/categories/all")
    public Iterable<CategoryDocument> getAllCategories() {
        return categoryDocumentRepository.findAll();
    }

    @GetMapping("/product-colors/all")
    public Iterable<ProductColorDocument> getAllProductColors() {
        return productColorDocumentRepository.findAll();
    }

    @GetMapping("/product-details/all")
    public Iterable<ProductDetailsDocument> getAllProductDetails() {
        return productDetailsDocumentRepository.findAll();
    }

    @GetMapping("/product-qa/all")
    public Iterable<ProductQADocument> getAllProductQAs() {
        return productQADocumentRepository.findAll();
    }

    @GetMapping("/product-skus/all")
    public Iterable<ProductSkuDocument> getAllProductSkus() {
        return productSkuDocumentRepository.findAll();
    }

    @GetMapping("/product-sizes/all")
    public Iterable<ProductSizeDocument> getAllProductSizes() {
        return productSizeDocumentRepository.findAll();
    }

    @GetMapping("/subcategories/all")
    public Iterable<SubcategoryDocument> getAllSubcategories() {
        return subcategoryDocumentRepository.findAll();
    }

    @PostMapping("/products/sync")
    public ResponseEntity<String> syncProducts() throws Exception {
        // allJobs 맵에서 "productIndexingJob"이라는 이름의 Job을 가져와 실행
        Job productIndexingJob = allJobs.get("indexingJob");

        if (productIndexingJob == null) {
            return ResponseEntity.internalServerError().body("Product indexing job not found!");
        }

        // --- JobParameters에 newIndexName 추가 ---
        // 현재 시간을 기반으로 새로운 인덱스 이름을 동적으로 생성합니다.
        String newIndexName = "products-" + System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("newIndexName", newIndexName) // newIndexName을 JobParameters에 추가
                .toJobParameters();

        jobLauncher.run(productIndexingJob, jobParameters);
        return ResponseEntity.ok("Batch synchronization job started!");
    }

    // Test
    @GetMapping("/search/category")
    public ResponseEntity<?> searchProductsByCategory(@RequestParam("category") Long category, int pageNo,
            int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        List<Product> products = searchService.findAllByCategory(category, pageRequest);

        return new ResponseEntity<>(products, HttpStatus.OK);

    }

}
