package com.project.catalog_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.dto.CategoryDto;
import com.project.common.dto.ProductDetailDto;
import com.project.common.dto.ProductDto;
import com.amazonaws.services.dynamodbv2.xspec.NULL;
import com.project.catalog_service.dto.SearchResultDto;
import com.project.common.dto.SubCategoryDto;
import com.project.catalog_service.dto.request.SearchParamsRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.ProductColorRepository;
import com.project.catalog_service.repository.ProductDetailsRepository;
import com.project.catalog_service.repository.ProductQARepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.repository.ProductSkuRepository;
import com.project.catalog_service.repository.SubCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final ProductRepository productRepository;

    private final ProductSkuRepository productskuRepository;

    private final ProductDetailsRepository productDetailsRepository;


    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    @Transactional(readOnly = true)
    public List<Product> findAllByCategory(Long category, Pageable pageRequest) {

        Page<Product> productPage = productRepository.findAllByCategory_CategoryId(category, pageRequest);

        return productPage.getContent();
    }

    @Transactional(readOnly = true)
    public List<Long> getProductIds(SearchParamsRequest params) {

        return productskuRepository.findProductIDBySizeAndPriceAndColor(params.getLowPrice(),
                params.getHighPrice(), params.getSize(), params.getColor());

    }

    @Transactional(readOnly = true)
    public List<Product> findProductBySearchParams(List<Long> productIds, Long categoryId, SearchParamsRequest params) {
        return productRepository.findProductBySearchParams(params.getSearch(), categoryId,
                params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                params.getRating(), productIds);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll(Pageable pageRequest) {

        Page<Product> productPage = productRepository.findAll(pageRequest);

        return productPage.getContent();
    }

    public SearchResultDto searchProductsByPage(SearchParamsRequest params) {

        Long categoryId = null;
        List<Product> products = null;

        if (null != params.getCategory()) {
            categoryId = Long.parseLong(params.getCategory());
        }

        Pageable pageRequest = createPageRequestUsing(params.getPage() - 1, params.getPageSize());

        // 1. Filter Category
        if (null != categoryId) {
            products = findAllByCategory(categoryId, pageRequest);
        } else {
            products = findAll(pageRequest);
        }

        if (!products.isEmpty()) {

            // reuslt
            List<Product> filteredProducts = new ArrayList<Product>();

            // 2. Filter Product name

            List<Product> nameFilteredProducts = new ArrayList<Product>();

            if (null != params.getSearch()) {
                for (Product p : products) {
                    if (p.getName() == params.getSearch()) {
                        nameFilteredProducts.add(p);
                    }
                }
            }

            if (!nameFilteredProducts.isEmpty()) {

                // 3. Filter Product brand
                List<Product> brandFilteredProducts = new ArrayList<Product>();

                for (Product p : nameFilteredProducts) {
                    if (params.getBrand() == p.getBrand()) {

                        brandFilteredProducts.add(p);
                    }
                }

                if (!brandFilteredProducts.isEmpty()) {

                    for (Product p : brandFilteredProducts) {

                        for (ProductDetails details : p.getDetails()) {

                            if (params.getMaterial() == details.getValue() ||
                                    params.getStyle() == details.getValue() ||
                                    params.getGender() == details.getValue()) {

                                filteredProducts.add(p);
                            }
                        }
                    }
                } else {

                    for (Product p : nameFilteredProducts) {

                        for (ProductDetails details : p.getDetails()) {

                            if (params.getMaterial() == details.getValue() ||
                                    params.getStyle() == details.getValue() ||
                                    params.getGender() == details.getValue()) {

                                filteredProducts.add(p);
                            }
                        }
                    }
                }

            } else {

                // 3. Filter Product brand
                List<Product> brandFilteredProducts = new ArrayList<Product>();

                for (Product p : products) {
                    if (params.getBrand() == p.getBrand()) {

                        brandFilteredProducts.add(p);
                    }
                }

                if (!brandFilteredProducts.isEmpty()) {

                    for (Product p : brandFilteredProducts) {

                        for (ProductDetails details : p.getDetails()) {

                            if (params.getMaterial() == details.getValue() ||
                                    params.getStyle() == details.getValue() ||
                                    params.getGender() == details.getValue()) {

                                filteredProducts.add(p);
                            }
                        }
                    }
                } else {

                    for (Product p : products) {

                        for (ProductDetails details : p.getDetails()) {

                            if (params.getMaterial() == details.getValue() ||
                                    params.getStyle() == details.getValue() ||
                                    params.getGender() == details.getValue()) {

                                filteredProducts.add(p);
                            }
                        }
                    }
                }

            }

            List<ProductDto> pageContent = null;

            int totalProducts = 0;

            if (!filteredProducts.isEmpty()) {

                pageContent = filteredProducts.stream().map(product -> {

                    ProductDto dto = Product.convertToDto(product);
                    return dto;
                }).collect(Collectors.toList());

                totalProducts = pageContent.size();

            } else {

                pageContent = products.stream().map(product -> {

                    ProductDto dto = Product.convertToDto(product);
                    return dto;
                }).collect(Collectors.toList());

                totalProducts = pageContent.size();

            }

            List<String> brandDB = productRepository.findBrandsByCategoryId(categoryId);

            List<CategoryDto> categoryDtos = categoryRepository.findAll().stream().map(category -> {
                return new CategoryDto(Long.toString(category.getCategoryId()),
                        category.getCategoryName(),
                        category.getSlug());
            }).collect(Collectors.toList());

            List<SubCategoryDto> subCategoryDtos = subCategoryRepository.findAll().stream().map(
                    subcategory -> {
                        return new SubCategoryDto(Long.toString(subcategory.getSubcategoryId()),
                                new CategoryDto(Long.toString(subcategory.getCategory().getCategoryId()),
                                        subcategory.getCategory().getCategoryName(),
                                        subcategory.getCategory().getSlug()),
                                subcategory.getSubcategoryName(), subcategory.getSlug());
                    }).collect(Collectors.toList());

            SearchResultDto result = SearchResultDto.builder()
                    .product(new PageImpl<>(pageContent, pageRequest, totalProducts))
                    .categories(categoryDtos)
                    .subCategories(subCategoryDtos)
                    .colors(getColors(categoryId))
                    .sizes(getSizes(categoryId))
                    .details(getDetails(categoryId))
                    .brandsDB(brandDB)
                    .totalProducts(totalProducts)
                    .build();

            return result;

        }
        return null;
    }

    //@Cacheable(value="search_cache", cacheManager = "redisCacheManager")
    public SearchResultDto searchProducts(SearchParamsRequest params) {

        List<Long> productIds = null;
        List<Product> products = null;
        Long categoryId = null;

        if (params.getCategory() != null)
            categoryId = Long.parseLong(params.getCategory());

        Pageable pageRequest = createPageRequestUsing(params.getPage() - 1, params.getPageSize());

        productIds = getProductIds(params);
        if (!productIds.isEmpty()) {

            products = findProductBySearchParams(productIds, categoryId, params);

            int totalProducts = productRepository.countProductsBySearchParams(params.getSearch(), categoryId,
                    params.getStyle(), params.getBrand(), params.getMaterial(),                    
                    params.getGender(), params.getRating(), productIds);

            List<ProductDto> pDtos = products.stream().map(product -> {

                ProductDto dto = Product.convertToDto(product);
                return dto;
            }).collect(Collectors.toList());

            
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), totalProducts);

            PageImpl<ProductDto> contents = null;
            List<ProductDto> pageContent = null;

            if (!pDtos.isEmpty()) {
                pageContent = pDtos.subList(start, end);
                contents = new PageImpl<>(pageContent, pageRequest, pDtos.size());
            } else {
                // return empty content
                pageContent = new ArrayList<ProductDto>();
                contents = new PageImpl<>(pageContent, pageRequest, 0);
            }

            List<String> brandDB = productRepository.findBrandsByCategoryId(categoryId);

            List<CategoryDto> categoryDtos = categoryRepository.findAll().stream().map(category -> {
                return new CategoryDto(Long.toString(category.getCategoryId()), category.getCategoryName(),
                        category.getSlug());
            }).collect(Collectors.toList());

            List<SubCategoryDto> subCategoryDtos = subCategoryRepository.findAll().stream().map(
                    subcategory -> {
                        return new SubCategoryDto(Long.toString(subcategory.getSubcategoryId()),
                                new CategoryDto(Long.toString(subcategory.getCategory().getCategoryId()),
                                        subcategory.getCategory().getCategoryName(),
                                        subcategory.getCategory().getSlug()),
                                subcategory.getSubcategoryName(), subcategory.getSlug());
                    }).collect(Collectors.toList());

            SearchResultDto result = SearchResultDto.builder()
                    .product(contents)
                    .categories(categoryDtos)
                    .subCategories(subCategoryDtos)
                    .colors(getColors(categoryId))
                    .sizes(getSizes(categoryId))
                    .details(getDetails(categoryId))
                    .brandsDB(brandDB)
                    .totalProducts(totalProducts)
                    .build();

            return result;
        }

        // return empty content

        List<ProductDto> pageContent = new ArrayList<ProductDto>();
        PageImpl<ProductDto> contents = new PageImpl<>(pageContent, pageRequest, 0);
                
        List<String> brandDB = productRepository.findBrandsByCategoryId(categoryId);

        List<CategoryDto> categoryDtos = categoryRepository.findAll().stream().map(category -> {
            return new CategoryDto(Long.toString(category.getCategoryId()), category.getCategoryName(),
                    category.getSlug());
        }).collect(Collectors.toList());

        List<SubCategoryDto> subCategoryDtos = subCategoryRepository.findAll().stream().map(
                subcategory -> {
                    return new SubCategoryDto(Long.toString(subcategory.getSubcategoryId()),
                            new CategoryDto(Long.toString(subcategory.getCategory().getCategoryId()),
                                    subcategory.getCategory().getCategoryName(),
                                    subcategory.getCategory().getSlug()),
                            subcategory.getSubcategoryName(), subcategory.getSlug());
                }).collect(Collectors.toList());

        SearchResultDto result = SearchResultDto.builder()
                .product(contents)
                .categories(categoryDtos)
                .subCategories(subCategoryDtos)
                .colors(getColors(categoryId))
                .sizes(getSizes(categoryId))
                .details(getDetails(categoryId))
                .brandsDB(brandDB)
                .totalProducts(0)
                .build();

        return result;
            
    }

    @Transactional(readOnly = true)
    public List<String> getColors(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        return productskuRepository.findColorsByProductId(ids);
    }

    @Transactional(readOnly = true)
    public List<String> getColors(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        return productskuRepository.findColorsByProductId(ids);
    }

    @Transactional(readOnly = true)
    public List<String> getSizes(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        return productskuRepository.findSizesByProductId(ids);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDto> getDetails(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDto.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<String> getSizes(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        return productskuRepository.findSizesByProductId(ids);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDto> getDetails(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDto.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }

}
