package com.project.catalog_service.service;

import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.request.SearchParamsRequest;
import com.project.catalog_service.mapper.CategoryMapper;
import com.project.catalog_service.mapper.ProductDetailsMapper;
import com.project.catalog_service.mapper.ProductMapper;
import com.project.catalog_service.mapper.SubcategoryMapper;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.QProduct;
import com.project.catalog_service.model.QProductDetails;
import com.project.catalog_service.repository.*;
import com.project.common.dto.CategoryDto;
import com.project.common.dto.ProductDetailDto;
import com.project.common.dto.ProductDto;
import com.project.common.dto.SubCategoryDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final JPAQueryFactory queryFactory;

    // @RequiredArgsConstructor가 있으므로 별도의 @Autowired 생성자는 필요 없습니다.
    // private final 필드들에 대한 생성자를 자동으로 생성합니다.

    private Pageable createPageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }

    
    @Cacheable(value = "search_cache", cacheManager = "redisCacheManager")
    public SearchResultDto searchProducts(SearchParamsRequest params) {
        Long categoryId = (params.getCategory() != null) ? Long.parseLong(params.getCategory()) : null;

        // 1. SKU, Price, Color, Size를 기반으로 Product ID 목록 조회
        List<Long> productIdsFromSku = productSkuRepository.findProductIDBySizeAndPriceAndColor(
                params.getLowPrice(), params.getHighPrice(), params.getSize(), params.getColor());

        // 2. Querydsl을 사용하여 모든 검색 조건(상품명, 브랜드, 상세 정보, 평점)을 조합하여 최종 상품 목록 조회
        // 이때, productIdsFromSku는 IN 절로 활용됩니다.
        QProduct product = QProduct.product;
        QProductDetails productDetails = QProductDetails.productDetails;
        BooleanBuilder builder = buildSearchPredicate(params, categoryId, productIdsFromSku);

        long totalProducts = queryFactory.select(product.countDistinct())
                .from(product)
                .leftJoin(product.details, productDetails)
                .where(builder)
                .fetchOne();

        Pageable pageRequest = createPageRequest(params.getPage() - 1, params.getPageSize());

        List<Product> products = queryFactory.selectFrom(product)
                .distinct()
                .leftJoin(product.details, productDetails)
                .where(builder)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        List<ProductDto> pageContent = products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());

        Page<ProductDto> productPage = new PageImpl<>(pageContent, pageRequest, totalProducts);
        
        // 3. 필터링 메뉴에 표시할 정보 조회
        List<String> brands = (categoryId != null) ? productRepository.findBrandsByCategoryId(categoryId)
                                                 : productRepository.findBrandsByCategoryName(params.getCategory());
        List<CategoryDto> categoryDtos = categoryRepository.findAll().stream().map(CategoryMapper::toDto).collect(Collectors.toList());
        List<SubCategoryDto> subCategoryDtos = subCategoryRepository.findAll().stream().map(SubcategoryMapper::toDto).collect(Collectors.toList());
        List<String> colors = getColors(categoryId);
        List<String> sizes = getSizes(categoryId);
        List<ProductDetailDto> details = getDetails(categoryId);

        // 4. 결과 DTO 구성 및 반환
        return SearchResultDto.builder()
                .product(productPage)
                .categories(categoryDtos)
                .subCategories(subCategoryDtos)
                .colors(colors)
                .sizes(sizes)
                .details(details)
                .brandsDB(brands)
                .totalProducts((int)totalProducts)
                .build();
    }

    private BooleanBuilder buildSearchPredicate(SearchParamsRequest params, Long categoryId, List<Long> productIdsFromSku) {
        QProduct product = QProduct.product;
        QProductDetails productDetails = QProductDetails.productDetails;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. productIds 조건 (SKU, Price, Color, Size 필터링 결과)
        if (productIdsFromSku != null && !productIdsFromSku.isEmpty()) {
            builder.and(product.productId.in(productIdsFromSku));
        }

        // 2. categoryId 조건
        if (categoryId != null) {
            builder.and(product.category.categoryId.eq(categoryId));
        }

        // 3. name 조건
        if (params.getSearch() != null && !params.getSearch().isEmpty()) {
            builder.and(product.name.likeIgnoreCase("%" + params.getSearch() + "%"));
        }

        // 4. brand 조건
        if (params.getBrand() != null && !params.getBrand().isEmpty()) {
            builder.and(product.brand.likeIgnoreCase("%" + params.getBrand() + "%"));
        }

        // 5. style, material, gender 조건 (OR 로직)
        if (params.getStyle() != null || params.getMaterial() != null || params.getGender() != null) {
            BooleanBuilder detailsBuilder = new BooleanBuilder();
            if (params.getStyle() != null && !params.getStyle().isEmpty()) {
                detailsBuilder.or(productDetails.value.likeIgnoreCase("%" + params.getStyle() + "%"));
            }
            if (params.getMaterial() != null && !params.getMaterial().isEmpty()) {
                detailsBuilder.or(productDetails.value.likeIgnoreCase("%" + params.getMaterial() + "%"));
            }
            if (params.getGender() != null && !params.getGender().isEmpty()) {
                detailsBuilder.or(productDetails.value.likeIgnoreCase("%" + params.getGender() + "%"));
            }
            builder.and(detailsBuilder);
        }

        // 6. rating 조건
        if (params.getRating() != 0.0f) {
            builder.and(product.rating.goe(params.getRating()));
        }

        return builder;
    }

    @Cacheable(value = "colors_cache", key = "#categoryId", cacheManager = "redisCacheManager")
    public List<String> getColors(Long categoryId) {
        if (categoryId == null) {
            return productSkuRepository.findColorsByProductId(null);
        }
        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);
        return productSkuRepository.findColorsByProductId(ids);
    }

    @Cacheable(value = "sizes_cache", key = "#categoryId", cacheManager = "redisCacheManager")
    public List<String> getSizes(Long categoryId) {
        if (categoryId == null) {
            return productSkuRepository.findSizesByProductId(null);
        }
        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);
        return productSkuRepository.findSizesByProductId(ids);
    }
    
    @Cacheable(value = "details_cache", key = "#categoryId", cacheManager = "redisCacheManager")
    public List<ProductDetailDto> getDetails(Long categoryId) {
        if (categoryId == null) {
            return productDetailsRepository.findDistinctAll().stream()
                    .map(ProductDetailsMapper::toDto)
                    .collect(Collectors.toList());
        }
        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);
        return productDetailsRepository.findDistinctAllByProductProductIdIn(ids).stream()
                .map(ProductDetailsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "products_by_category", key = "#category + '_' + #pageRequest.pageNumber", cacheManager = "redisCacheManager")
    public List<Product> findAllByCategory(Long category, Pageable pageRequest) {

        Page<Product> productPage = productRepository.findAllByCategory_CategoryId(category, pageRequest);

        return productPage.getContent();
    }
}