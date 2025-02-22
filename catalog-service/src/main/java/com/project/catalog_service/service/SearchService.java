package com.project.catalog_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.catalog_service.dto.CategoryDto;
import com.project.catalog_service.dto.ProductDetailDto;
import com.project.catalog_service.dto.ProductDto;
import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.SubCategoryDto;
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

    private final ProductColorRepository productColorRepository;

    private final ProductQARepository productQARepository;


     private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    public SearchResultDto searchProducts(SearchParamsRequest params) {

        List<Long> productIds = null;
        List<Product> products = null;
        Long categoryId = null;

        if (params.getCategory() != null)
            categoryId = Long.parseLong(params.getCategory());

        log.info(params.getSize());

        productIds = productskuRepository.findProductIDBySizeAndPriceAndColor(params.getLowPrice(),
                params.getHighPrice(), params.getSize(), params.getColor());

        if (!productIds.isEmpty()) {

            log.info("productIDs");

            products = productRepository.findProductBySearchParams(params.getSearch(), categoryId,
                    params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                    params.getRating(), productIds);

            int totalProducts = productRepository.countProductsBySearchParams(params.getSearch(), categoryId,
                    params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                    params.getRating(), productIds);

            log.info("totalProducts : " + Integer.toString(totalProducts));

            List<ProductDto> pDtos = products.stream().map(product -> {

                ProductDto dto = Product.convertToDto(product);
                return dto;
            }).collect(Collectors.toList());

            Pageable pageRequest = createPageRequestUsing(params.getPage() - 1, params.getPageSize());
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), totalProducts);

            List<ProductDto> pageContent = pDtos.subList(start, end);

            // List<String> subs = null;
            // List<SubCategory> subcategories =
            // categoryRepository.findSubCategoriesByCategoryName(params.getCategory());
            // subs = subcategories.stream().map(sub-> {return
            // sub.getSubcategoryName();}).collect(Collectors.toList());

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
                    .product(new PageImpl<>(pageContent, pageRequest, pDtos.size()))
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

    public List<String> getColors(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        return productskuRepository.findColorsByProductId(ids);
    }

    public List<String> getColors(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        return productskuRepository.findColorsByProductId(ids);
    }

    public List<String> getSizes(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        return productskuRepository.findSizesByProductId(ids);
    }

    public List<ProductDetailDto> getDetails(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDto.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }

    public List<String> getSizes(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        return productskuRepository.findSizesByProductId(ids);
    }

    public List<ProductDetailDto> getDetails(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDto.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }


}
