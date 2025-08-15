package com.project.catalog_service.repository;

import com.project.catalog_service.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByNameContaining(String name);
    List<ProductDocument> findByBrand(String brand);
    List<ProductDocument> findByCategory(String category);
    List<ProductDocument> findByRatingGreaterThanEqual(float rating);
}