package com.project.catalog_service.repository;

import com.project.catalog_service.document.ProductSizeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSizeDocumentRepository extends ElasticsearchRepository<ProductSizeDocument, String> {
    List<ProductSizeDocument> findBySkuproductId(String skuproductId);
    List<ProductSizeDocument> findBySize(String size);
    List<ProductSizeDocument> findByQuantityLessThan(int quantity);
}