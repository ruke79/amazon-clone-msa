package com.project.catalog_service.repository;

import com.project.catalog_service.document.ProductColorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductColorDocumentRepository extends ElasticsearchRepository<ProductColorDocument, String> {
    List<ProductColorDocument> findByColor(String color);
}