package com.project.catalog_service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.catalog_service.dto.CategoryDto;
import com.project.catalog_service.dto.ColorAttributeDto;
import com.project.catalog_service.dto.ProductDto;
import com.project.catalog_service.dto.ProductDetailDto;
import com.project.catalog_service.dto.ProductInfoDto;
import com.project.catalog_service.dto.ProductQADTO;
import com.project.catalog_service.dto.ProductSkuDTO;
import com.project.catalog_service.dto.ReviewDTO;
import com.project.catalog_service.dto.ReviewStyleDTO;
import com.project.catalog_service.dto.ReviewerDTO;
import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.SizeAttributeDto;
import com.project.catalog_service.dto.SubCategoryDTO;
import com.project.catalog_service.dto.UserDTO;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductCategory;
import com.project.catalog_service.model.ProductColorAttribute;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.model.Review;
import com.project.catalog_service.model.SubCategory;
import com.project.catalog_service.model.User;
import com.project.catalog_service.model.WishList;
import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.ProductColorRepository;
import com.project.catalog_service.repository.ProductDetailsRepository;
import com.project.catalog_service.repository.ProductQARepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.repository.ProductSkuRepository;
import com.project.catalog_service.repository.ReviewRepository;
import com.project.catalog_service.repository.SubCategoryRepository;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ProductInfosLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ReviewRequest;
import com.project.catalog_service.dto.request.SearchParamsRequest;
import com.project.catalog_service.dto.request.WishListRequest;

import jakarta.mail.Multipart;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Requiredargsconstructor
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final ProductSkuRepository productskuRepository;

    private final ProductDetailsRepository productDetailsRepository;

    private final ProductColorRepository productColorRepository;

    private final ProductQARepository productQARepository;

    private final ReviewRepository reviewRepository;

    
    public List<ProductDto> getProductsByName(String productName) {

        List<Product> products = productRepository.findByName(productName);

        List<ProductDto> result = new ArrayList<>();

        if (products != null) {

            for (Product p : products) {
                ProductDto dto = convertToDto(p);
                result.add(dto);
            }

            return result;
        }
        return null;
    }

    public ProductDto getProductById(Long productId) {

        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {

            ProductDto dto = convertToDto(product.get());

            return dto;
        }
        return null;
    }

    public List<ProductDto> getProductsBySlug(String slug) {

        List<Product> products = productRepository.findBySlug(slug);

        List<ProductDto> result = new ArrayList<>();
        if (products != null) {

            for (Product p : products) {
                ProductDto dto = convertToDto(p);
                result.add(dto);
            }

            return result;
        }
        return null;
    }

    private ProductDto convertToDto(Product product) {

        CategoryDto parent = CategoryDto.builder()
                .id(Long.toString(product.getCategory().getCategoryId()))
                .name(product.getCategory().getCategoryName())
                .slug(product.getCategory().getSlug()).build();

        List<SubCategoryDTO> subCategories = product.getSubCategories().stream()
                .map(subcategory -> new SubCategoryDTO(Long.toString(subcategory.getSubcategoryId()), parent,
                        subcategory.getSubcategoryName(), subcategory.getSlug()))
                .collect(Collectors.toList());

        List<ProductDetailDto> details = product.getDetails().stream().map(detail -> {
            return ProductDetailDto.builder()
                    .name(detail.getName())
                    .value(detail.getValue()).build();
        }).collect(Collectors.toList());

        List<ReviewDTO> reviews = product.getReviews().stream().map(review -> {
            return ReviewDTO.builder()
                    .images(review.getImages())
                    .rating(review.getRating())
                    .fit(review.getFit())
                    .review(review.getReview())
                    .reviewedBy(ReviewerDTO.builder()
                            .name(review.getReviewedBy().getUserName())
                            .image(review.getReviewedBy().getImage())
                            .build())
                    .likes(review.getLikes())
                    .size(review.getSize())
                    .style(ReviewStyleDTO.builder()
                            .color(review.getStyle().getColor())
                            .image(review.getStyle().getImage())
                            .build())
                    .build();

        }).collect(Collectors.toList());

        List<ProductQADTO> questions = product.getQuestions().stream().map(q -> {
            return ProductQADTO.builder()
                    .question(q.getQuestion())
                    .answer(q.getAnswer())
                    .build();
        }).collect(Collectors.toList());

        List<ProductSkuDTO> skus = product.getSku_products().stream().map(sku -> {

            List<String> base64Image = new ArrayList<String>();
            for (String image : sku.getImages()) {
                log.info("image: " + image);
                base64Image.add(image);
            }

            Set<SizeAttributeDto> sizes = sku.getSizes().stream().map(item -> {
                SizeAttributeDto size = new SizeAttributeDto(Long.toString(item.getSizeId()),
                        item.getSize(), item.getQuantity(), item.getPrice());
                return size;
            }).collect(Collectors.toSet());

            ColorAttributeDto color = new ColorAttributeDto(Long.toString(sku.getColor().getColorId()),
                    sku.getColor().getColor(), sku.getColor().getColorImage());

            ProductSkuDTO dto = ProductSkuDTO.builder()
                    .id(Long.toString(sku.getSkuproductId()))
                    .sku(sku.getSku())
                    .images(base64Image)
                    .discount(sku.getDiscount())
                    .sold(sku.getSold())
                    .sizes(sizes)
                    .color(color)
                    .build();

            return dto;
        }).collect(Collectors.toList());

        return ProductDto.builder()
                .id(Long.toString(product.getProductId()))
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .slug(product.getSlug())
                .category(parent)
                .subCategories(subCategories)
                .details(details)
                .reviews(reviews)
                .questions(questions)
                .sku_products(skus)
                .refund_policy(product.getRefund_policy())
                .rating(product.getRating())
                .num_reviews(product.getNum_reviews())
                .shipping(product.getShipping())
                .createdAt(product.getCreatedAt().toString())
                .build();

                
    }
    

    public List<ProductSku> load(ProductInfosLoadRequest request) throws IOException {

        List<ProductSku> result = new ArrayList<>();

        for (ProductInfoLoadRequest pr : request.getProducts()) {

            ProductSku sku = null;

            sku = createCategorysAndLoadProduct(pr, pr.getImages(), pr.getColor().getColorImage());
            result.add(sku);

        }

        return result;
    }

    public ProductSku createCategorysAndLoadProduct(ProductInfoLoadRequest request, List<String> images,
            String colorImage) throws IOException {

        if (request.getParent() != null && request.getParent().length() > 0) {

            Optional<Product> existed = productRepository.findById(Long.parseLong(request.getParent()));

            if (existed.isPresent()) {

                ProductSku newSku = loadSku(request, existed.get(), images, colorImage);
                existed.get().getSku_products().add(newSku);

                productRepository.save(existed.get());

                return newSku;
            }
        } else {

            Product product = new Product();

            product.setBrand(request.getBrand());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setSlug(request.getSlug());

            if (request.getShippingFee() != null)
                product.setShipping(Integer.parseInt(request.getShippingFee()));

            ProductCategory category = categoryRepository.findByCategoryName(request.getCategory().getName());

            if (null != category) {

                product.setCategory(category);

                List<String> subcategoryNames = new ArrayList<>();
                for (SubCategoryDTO subcategory : request.getSubCategories()) {
                    subcategoryNames.add(subcategory.getName());
                }

                List<SubCategory> subCategories = subCategoryRepository.findByCategory_CategoryNameAndSubcategoryNameIn(
                        category.getCategoryName(), subcategoryNames);

                if (subCategories != null) {

                    product.setSubCategories(subCategories);

                    productRepository.save(product);

                    return loadSku(request, product, images, colorImage);
                }
            } else {

                category = new ProductCategory();
                category.setCategoryName(request.getCategory().getName());
                category.setSlug(request.getCategory().getSlug());

                category = categoryRepository.save(category);

                product.setCategory(category);

                List<SubCategory> subCategories = new ArrayList<>();

                for (SubCategoryDTO dto : request.getSubCategories()) {

                    SubCategory subcategory = new SubCategory();
                    subcategory.setSubcategoryName(dto.getName());

                    subcategory.setCategory(category);

                    subcategory.setSlug(dto.getSlug());

                    subCategories.add(subcategory);

                    subCategoryRepository.save(subcategory);
                }

                product.setSubCategories(subCategories);

                productRepository.save(product);

                return loadSku(request, product, images, colorImage);

            }
        }

        return null;
    }

    public ProductSku addProduct(ProductRequest request, List<String> images, String colorImage) throws IOException {

        if (request.getParent() != null && request.getParent().length() > 0) {

            Optional<Product> existed = productRepository.findById(Long.parseLong(request.getParent()));

            if (existed.isPresent()) {

                ProductSku newSku = createSku(request, existed.get(), images, colorImage);
                existed.get().getSku_products().add(newSku);

                productRepository.save(existed.get());

                return newSku;
            }
        } else {

            Product product = new Product();

            product.setBrand(request.getBrand());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setSlug(request.getSlug());

            if (request.getShippingFee() != null)
                product.setShipping(Integer.parseInt(request.getShippingFee()));

            Optional<ProductCategory> category = categoryRepository.findById(Long.parseLong(request.getCategory()));

            if (category != null) {

                product.setCategory(category.get());

                List<Long> subcategoryIds = new ArrayList<>();
                for (String subcategory : request.getSubCategories()) {
                    log.info(subcategory);
                    subcategoryIds.add(Long.parseLong(subcategory));
                }

                List<SubCategory> subCategories = subCategoryRepository.findBySubcategoryIdIn(subcategoryIds);

                if (subCategories != null) {

                    product.setSubCategories(subCategories);

                    productRepository.save(product);

                    return createSku(request, product, images, colorImage);
                }
            }
        }

        return null;
    }

    private ProductSku loadSku(ProductInfoLoadRequest request, Product product, List<String> images,
            String colorImage) {

        ProductSku skuProject = new ProductSku();

        skuProject.setSku(request.getSku());

        if (request.getDiscount() != null)
            skuProject.setDiscount(Integer.parseInt(request.getDiscount()));

        skuProject.setColor(ProductColorAttribute.builder().colorImage(colorImage)
                .color(request.getColor().getColor()).build());

        // ArrayList<String> bytes = new ArrayList<String>();
        // for(MultipartFile image : images) {
        // bytes.add(encodeFileToBase64((image)));

        // }
        // if (bytes.size() > 0)
        // skuProject.setImages(bytes);
        // skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));

        skuProject.setImages(images);
        // cloudinary에 저장된 이미지 URL
        skuProject.getColor().setColorImage(colorImage);

        productColorRepository.save(skuProject.getColor());

        request.getSizes().forEach(size -> size.setSku_product(skuProject));

        skuProject.setSizes(request.getSizes());

        skuProject.setProduct(product);

        if (request.getDetails() != null) {

            product.setDetails(request.getDetails());

            product.getDetails().forEach(detail -> {
                detail.setProduct(product);
            });
        }

        product.setQuestions(request.getQuestions());
        product.getQuestions().forEach(question -> {
            question.setProduct(product);
            productQARepository.save(question);
        });

        return productskuRepository.save(skuProject);

    }

    private ProductSku createSku(ProductRequest request, Product product, List<String> images, String colorImage) {

        ProductSku skuProject = new ProductSku();

        skuProject.setSku(request.getSku());

        if (request.getDiscount() != null)
            skuProject.setDiscount(Integer.parseInt(request.getDiscount()));

        skuProject.setColor(request.getColor());

        // ArrayList<String> bytes = new ArrayList<String>();
        // for(MultipartFile image : images) {
        // bytes.add(encodeFileToBase64((image)));

        // }
        // if (bytes.size() > 0)
        // skuProject.setImages(bytes);
        // skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));

        skuProject.setImages(images);
        // cloudinary에 저장된 이미지 URL
        skuProject.getColor().setColorImage(colorImage);

        request.getSizes().forEach(size -> size.setSku_product(skuProject));

        skuProject.setSizes(request.getSizes());

        skuProject.setProduct(product);

        if (request.getDetails() != null) {

            product.setDetails(request.getDetails());

            product.getDetails().forEach(detail -> {
                detail.setProduct(product);
            });
        }

        product.setQuestions(request.getQuestions());
        product.getQuestions().forEach(question -> {
            question.setProduct(product);
            productQARepository.save(question);
        });

        return productskuRepository.save(skuProject);

    }

    private String encodeFileToBase64(MultipartFile file) {
        try {

            byte[] fileBytes = file.getBytes();
            return Base64.encodeBase64String(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decodeBase64ToFile(String encodedString) {
        return Base64.decodeBase64(encodedString);
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    public SearchResultDto searchProducts(SearchParamsRequest params) {

        List<Long> productIds = null;
        List<Product> products = null;
        Long categoryId = null;

        if (params.getCategory() != null)
            categoryId = Long.parseLong(params.getCategory());

        productIds = productskuRepository.findProductIDBySizeAndPriceAndColor(params.getLowPrice(),
                params.getHighPrice(), params.getSize(), params.getColor());

        if (!productIds.isEmpty()) {

            products = productRepository.findProductBySearchParams(params.getSearch(), categoryId,
                    params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                    params.getRating(), productIds);

            int totalProducts = productRepository.countProductsBySearchParams(params.getSearch(), categoryId,
                    params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                    params.getRating(), productIds);

            List<ProductDto> pDtos = products.stream().map(product -> {

                ProductDto dto = convertToDto(product);
                return dto;
            }).collect(Collectors.toList());

            Pageable pageRequest = createPageRequestUsing(params.getPage() - 1, params.getPageSize());
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), totalProducts);

            log.info(Integer.toString(start));
            log.info(Integer.toString(end));

            List<ProductDto> pageContent = pDtos.subList(start, end);

            // List<String> subs = null;
            // List<SubCategory> subcategories =
            // categoryRepository.findSubCategoriesByCategoryName(params.getCategory());
            // subs = subcategories.stream().map(sub-> {return
            // sub.getSubcategoryName();}).collect(Collectors.toList());

            List<String> brandDB = productRepository.findBrandsByCategoryId(categoryId);

            List<CategoryDto> categoryDTOs = categoryRepository.findAll().stream().map(category -> {
                return new CategoryDto(Long.toString(category.getCategoryId()), category.getCategoryName(),
                        category.getSlug());
            }).collect(Collectors.toList());

            List<SubCategoryDTO> subCategoryDTOs = subCategoryRepository.findAll().stream().map(
                    subcategory -> {
                        return new SubCategoryDTO(Long.toString(subcategory.getSubcategoryId()),
                                new CategoryDto(Long.toString(subcategory.getCategory().getCategoryId()),
                                        subcategory.getCategory().getCategoryName(),
                                        subcategory.getCategory().getSlug()),
                                subcategory.getSubcategoryName(), subcategory.getSlug());
                    }).collect(Collectors.toList());

            SearchResultDto result = SearchResultDto.builder()
                    .product(new PageImpl<>(pageContent, pageRequest, pDtos.size()))
                    .categories(categoryDTOs)
                    .subCategories(subCategoryDTOs)
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

    public ProductInfoDto getCartProductInfo(Long productId, int style, int size) {

        Optional<Product> data = productRepository.findById(productId);

        if (data.isPresent()) {

            Product product = data.get();

            int discount = product.getSku_products().get(style).getDiscount();
            int priceBefore = product.getSku_products().get(style).getSizes().get(size).getPrice();
            int price = discount > 0 ? priceBefore - priceBefore / discount : priceBefore;

            ColorAttributeDto color = ColorAttributeDto.builder()
                    .id(Long.toString(product.getSku_products().get(style).getColor().getColorId()))
                    .color(product.getSku_products().get(style).getColor().getColor())
                    .colorImage(product.getSku_products().get(style).getColor().getColorImage())
                    .build();

            List<String> subcategoryIds = product.getSubCategories().stream().map(subcategory -> {
                return Long.toString(subcategory.getSubcategoryId());
            })
                    .collect(Collectors.toList());

            List<ProductDetailDto> details = product.getDetails().stream().map(detail -> {
                return ProductDetailDto.builder()
                        .name(detail.getName())
                        .value(detail.getValue()).build();
            }).collect(Collectors.toList());

            List<ProductQADTO> questions = product.getQuestions().stream().map(q -> {
                return ProductQADTO.builder()
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .build();
            }).collect(Collectors.toList());

            List<ReviewDTO> reviews = product.getReviews().stream().map(review -> {
                return ReviewDTO.builder()
                        .images(review.getImages())
                        .rating(review.getRating())
                        .fit(review.getFit())
                        .review(review.getReview())
                        .reviewedBy(ReviewerDTO.builder()
                                .name(review.getReviewedBy().getUserName())
                                .image(review.getReviewedBy().getImage())
                                .build())
                        .size(review.getSize())
                        .style(ReviewStyleDTO.builder()
                                .color(review.getStyle().getColor())
                                .image(review.getStyle().getImage())
                                .build())
                        .build();

            }).collect(Collectors.toList());

            ProductInfoDto dto = ProductInfoDto.builder()
                    .id(Long.toString(product.getProductId()))
                    .style(style)
                    .name(product.getName())
                    .description(product.getDescription())
                    .slug(product.getSlug())
                    .sku(product.getSku_products().get(style).getSku())
                    .brand(product.getBrand())
                    .shipping(product.getShipping())
                    .images(product.getSku_products().get(style).getImages())
                    .color(color)
                    .size(product.getSku_products().get(style).getSizes().get(size).getSize())
                    .price(price)
                    .priceBefore(priceBefore)
                    .qty(1)
                    .quantity(product.getSku_products().get(style).getSizes().get(size).getQuantity())
                    .category(Long.toString(product.getCategory().getCategoryId()))
                    .subCategories(subcategoryIds)
                    .questions(questions)
                    .details(details)
                    .reviews(reviews)
                    .discount(discount)
                    .build();

            return dto;
        }

        return null;
    }

}
