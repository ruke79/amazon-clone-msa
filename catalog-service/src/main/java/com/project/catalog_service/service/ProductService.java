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
import com.project.catalog_service.dto.ProductQADto;
import com.project.catalog_service.dto.ProductSkuDto;

import com.project.catalog_service.dto.SearchResultDto;
import com.project.catalog_service.dto.SizeAttributeDto;
import com.project.catalog_service.dto.SubCategoryDto;

import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductCategory;
import com.project.catalog_service.model.ProductColorAttribute;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.model.SubCategory;

import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.ProductColorRepository;
import com.project.catalog_service.repository.ProductDetailsRepository;
import com.project.catalog_service.repository.ProductQARepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.repository.ProductSkuRepository;

import com.project.catalog_service.repository.SubCategoryRepository;
import com.project.catalog_service.util.FileUtils;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ProductInfosLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.SearchParamsRequest;


//import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;

    private final ProductRepository productRepository;

    //private final CategoryService categoryService;

    private final ProductSkuRepository productskuRepository;

    //private final ProductDetailsRepository productDetailsRepository;

    private final ProductColorRepository productColorRepository;

    private final ProductQARepository productQARepository;
    
    private final ImageService imageService;



    
    public List<ProductDto> getProductsByName(String productName) {

        List<Product> products = productRepository.findByName(productName);

        List<ProductDto> result = new ArrayList<>();

        if (products != null) {

            for (Product p : products) {
                ProductDto dto = Product.convertToDto(p);
                result.add(dto);
            }

            return result;
        }
        return null;
    }

    public ProductDto getProductById(Long productId) {

        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {

            ProductDto dto = Product.convertToDto(product.get());

            return dto;
        }
        return null;
    }

    public List<ProductDto> getProductsBySlug(String slug) {

        List<Product> products = productRepository.findBySlug(slug);

        List<ProductDto> result = new ArrayList<>();
        if (products != null) {

            for (Product p : products) {
                ProductDto dto = Product.convertToDto(p);
                result.add(dto);
            }

            return result;
        }
        return null;
    }

    // public ProductDto convertToDto(Product product) {

    //     CategoryDto parent = CategoryDto.builder()
    //             .id(product.getCategory().getCategoryId())
    //             .name(product.getCategory().getCategoryName())
    //             .slug(product.getCategory().getSlug()).build();

    //     List<SubCategoryDto> subCategories = product.getSubCategories().stream()
    //             .map(subcategory -> new SubCategoryDto(subcategory.getSubcategoryId(), parent,
    //                     subcategory.getSubcategoryName(), subcategory.getSlug()))
    //             .collect(Collectors.toList());

    //     List<ProductDetailDto> details = product.getDetails().stream().map(detail -> {
    //         return ProductDetailDto.builder()
    //                 .name(detail.getName())
    //                 .value(detail.getValue()).build();
    //     }).collect(Collectors.toList());

    //     // List<ReviewDto> reviews = product.getReviews().stream().map(review -> {
    //     //     return ReviewDto.builder()
    //     //             .images(review.getImages())
    //     //             .rating(review.getRating())
    //     //             .fit(review.getFit())
    //     //             .review(review.getReview())
    //     //             .reviewedBy(ReviewerDto.builder()
    //     //                     .name(review.getReviewedBy().getUserName())
    //     //                     .image(review.getReviewedBy().getImage())
    //     //                     .build())
    //     //             .likes(review.getLikes())
    //     //             .size(review.getSize())
    //     //             .style(ReviewStyleDto.builder()
    //     //                     .color(review.getStyle().getColor())
    //     //                     .image(review.getStyle().getImage())
    //     //                     .build())
    //     //             .build();

    //     // }).collect(Collectors.toList());

    //     List<ProductQADto> questions = product.getQuestions().stream().map(q -> {
    //         return ProductQADto.builder()
    //                 .question(q.getQuestion())
    //                 .answer(q.getAnswer())
    //                 .build();
    //     }).collect(Collectors.toList());

    //     List<ProductSkuDto> skus = product.getSku_products().stream().map(sku -> {

    //         List<String> base64Image = new ArrayList<String>();
    //         for (String image : sku.getImages()) {
    //             log.info("image: " + image);
    //             base64Image.add(image);
    //         }

    //         Set<SizeAttributeDto> sizes = sku.getSizes().stream().map(item -> {
    //             SizeAttributeDto size = new SizeAttributeDto(item.getSizeId(),
    //                     item.getSize(), item.getQuantity(), item.getPrice());
    //             return size;
    //         }).collect(Collectors.toSet());

    //         ColorAttributeDto color = new ColorAttributeDto(sku.getColor().getColorId(),
    //                 sku.getColor().getColor(), sku.getColor().getColorImage());

    //         ProductSkuDto dto = ProductSkuDto.builder()
    //                 .id(sku.getSkuproductId())
    //                 .sku(sku.getSku())
    //                 .images(base64Image)
    //                 .discount(sku.getDiscount())
    //                 .sold(sku.getSold())
    //                 .sizes(sizes)
    //                 .color(color)
    //                 .build();

    //         return dto;
    //     }).collect(Collectors.toList());

    //     return ProductDto.builder()
    //             .id(product.getProductId())
    //             .name(product.getName())
    //             .description(product.getDescription())
    //             .brand(product.getBrand())
    //             .slug(product.getSlug())
    //             .category(parent)
    //             .subCategories(subCategories)
    //             .details(details)
    //             //.reviews(reviews)
    //             .questions(questions)
    //             .sku_products(skus)
    //             .refund_policy(product.getRefund_policy())
    //             .rating(product.getRating())
    //             //.num_reviews(product.getNum_reviews())
    //             .shipping(product.getShipping())
    //             .createdAt(product.getCreatedAt().toString())
    //             .build();

                
    // }
    

    public List<ProductSku> load(List<ProductInfoLoadRequest> products, List<MultipartFile> images, 
    List<MultipartFile> colorImages) throws IOException {

        List<ProductSku> result = new ArrayList<>();

        int i = 0; 
        for (ProductInfoLoadRequest pr : products) {

            ProductSku sku = null;

            sku = createCategorysAndLoadProduct(pr, images.get(i), colorImages.get(i));            
            result.add(sku);
            i++;

        }

        return result;
    }

    public ProductSku createCategorysAndLoadProduct(ProductInfoLoadRequest request, MultipartFile images,
    MultipartFile colorImage) throws IOException {

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
                for (SubCategoryDto subcategory : request.getSubCategories()) {
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

                for (SubCategoryDto dto : request.getSubCategories()) {

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

    public ProductSku addProduct(ProductRequest request, List<MultipartFile> images, MultipartFile colorImage) throws IOException {

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

    private ProductSku loadSku(ProductInfoLoadRequest request, Product product, MultipartFile image,
    MultipartFile colorImage) throws IOException {

        ProductSku skuProject = new ProductSku();

        skuProject.setSku(request.getSku());

        if (request.getDiscount() != null)
            skuProject.setDiscount(Integer.parseInt(request.getDiscount()));

        
        // ArrayList<String> bytes = new ArrayList<String>();
        // for(MultipartFile image : images) {
        // bytes.add(encodeFileToBase64((image)));

        // }
        // if (bytes.size() > 0)
        // skuProject.setImages(bytes);
        // skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));

        List<String> imageUrls = new ArrayList<String>();

        
        String filename = FileUtils.getRandomFilename();
        String filepath = imageService.upload(image, filename);
        imageUrls.add(filepath);                  
        
        skuProject.setImages(imageUrls);
        
        filename = "color_" + FileUtils.getRandomFilename();
        String colorImageUrl = imageService.upload(colorImage, filename);

        
        skuProject.setColor(ProductColorAttribute.builder().colorImage(colorImageUrl)
                .color(request.getColor().getColor()).build());

        
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

    private ProductSku createSku(ProductRequest request, Product product, List<MultipartFile> images, MultipartFile colorImage) throws IOException {

        ProductSku skuProject = new ProductSku();

        skuProject.setSku(request.getSku());

        if (request.getDiscount() != null)
            skuProject.setDiscount(Integer.parseInt(request.getDiscount()));

        skuProject.setColor(request.getColor());

        List<String> imageUrls = new ArrayList<String>();

        for(MultipartFile image : images) {
            String filename = FileUtils.getRandomFilename();
            String filepath = imageService.upload(image, filename);
            imageUrls.add(filepath);
        }

        // ArrayList<String> bytes = new ArrayList<String>();
        // for(MultipartFile image : images) {
        // bytes.add(encodeFileToBase64((image)));

        // }
        // if (bytes.size() > 0)
        // skuProject.setImages(bytes);
        // skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));

        skuProject.setImages(imageUrls);

        String filename = "color_" + FileUtils.getRandomFilename();
        String colorImageUrl = imageService.upload(colorImage, filename);

        skuProject.getColor().setColorImage(colorImageUrl);

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

    public ColorAttributeDto getColorAttributeInfo(Long colorId) {

        ProductColorAttribute color = productColorRepository.findById(colorId).orElse(null);

        
        if (null != color) {

            ColorAttributeDto dto = ColorAttributeDto.builder()
                    .id(Long.toString(color.getColorId()))
                    .color(color.getColor())
                    .colorImage(color.getColorImage())
                    .build();

            return dto;

        }

        log.info("color is null : " + Long.toString(colorId)) ;

        return null;
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

            List<ProductQADto> questions = product.getQuestions().stream().map(q -> {
                return ProductQADto.builder()
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .build();
            }).collect(Collectors.toList());

            // List<ReviewDto> reviews = product.getReviews().stream().map(review -> {
            //     return ReviewDto.builder()
            //             .images(review.getImages())
            //             .rating(review.getRating())
            //             .fit(review.getFit())
            //             .review(review.getReview())
            //             .reviewedBy(ReviewerDto.builder()
            //                     .name(review.getReviewedBy().getUserName())
            //                     .image(review.getReviewedBy().getImage())
            //                     .build())
            //             .size(review.getSize())
            //             .style(ReviewStyleDto.builder()
            //                     .color(review.getStyle().getColor())
            //                     .image(review.getStyle().getImage())
            //                     .build())
            //             .build();

            // }).collect(Collectors.toList());

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
                    //.reviews(reviews)
                    .discount(discount)
                    .build();

            return dto;
        }

        return null;
    }

}
