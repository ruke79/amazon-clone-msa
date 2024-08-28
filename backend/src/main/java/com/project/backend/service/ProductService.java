package com.project.backend.service;

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

import com.project.backend.dto.CategoryDTO;
import com.project.backend.dto.ColorAttributeDTO;
import com.project.backend.dto.ProductDTO;
import com.project.backend.dto.ProductDetailDTO;
import com.project.backend.dto.ProductInfoDTO;
import com.project.backend.dto.ProductQADTO;
import com.project.backend.dto.ProductSkuDTO;
import com.project.backend.dto.ReviewDTO;
import com.project.backend.dto.ReviewStyleDTO;
import com.project.backend.dto.ReviewerDTO;
import com.project.backend.dto.SearchResultDTO;
import com.project.backend.dto.SizeAttributeDTO;
import com.project.backend.dto.SubCategoryDTO;
import com.project.backend.dto.UserDTO;
import com.project.backend.model.Product;
import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.ProductQA;
import com.project.backend.model.ProductSku;
import com.project.backend.model.Review;
import com.project.backend.model.SubCategory;
import com.project.backend.model.User;
import com.project.backend.model.WishList;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.ProductDetailsRepository;
import com.project.backend.repository.ProductQARepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.repository.ReviewRepository;
import com.project.backend.repository.SubCategoryRepository;
import com.project.backend.security.request.ProductRequest;
import com.project.backend.security.request.ReviewRequest;
import com.project.backend.security.request.SearchParamsRequest;
import com.project.backend.security.request.WishListRequest;

import jakarta.mail.Multipart;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductSkuRepository productskuRepository;

    @Autowired
    ProductDetailsRepository productDetailsRepository;

    @Autowired
    ProductQARepository productQARepository;

    @Autowired
    ReviewRepository reviewRepository;

    public ProductDTO getProductByName(String productName) {

        Product product = productRepository.findByName(productName);

        if (product != null) {

            ProductDTO dto = convertToDto(product);

            return dto;
        }
        return null;
    }

    public ProductDTO getProductById(Long productId) {

        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {

            ProductDTO dto = convertToDto(product.get());

            return dto;
        }
        return null;
    }

    public ProductDTO getProductBySlug(String slug) {

        Product product = productRepository.findBySlug(slug);

        if (product != null) {

            ProductDTO dto = convertToDto(product);

            return dto;
        }
        return null;
    }

    private ProductDTO convertToDto(Product product) {

        // CategoryDTO parent = CategoryDTO.builder()
        // .id(Long.toString(product.getCategory().getCategoryId()))
        // .name(product.getCategory().getCategoryName())
        // .slug(product.getCategory().getSlug()).build();

        // List<SubCategoryDTO> dtos =
        // product.getSubCategories().stream().map(subcategory ->
        // new SubCategoryDTO(Long.toString(subcategory.getSubcategoryId()), parent,
        // subcategory.getSubcategoryName())).
        // collect(Collectors.toList());

        List<String> subcategoryIds = product.getSubCategories().stream().map(subcategory -> {
            return Long.toString(subcategory.getSubcategoryId());
        })
                .collect(Collectors.toList());

        List<ProductDetailDTO> details = product.getDetails().stream().map(detail -> {
            return ProductDetailDTO.builder()
                    .name(detail.getName())
                    .value(detail.getValue()).build();
        }).collect(Collectors.toList());

        List<ReviewDTO> reviews = product.getReviews().stream().map(review -> 
        {
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

        List<ProductQADTO> questions =   product.getQuestions().stream().map(q -> 
        {
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

            Set<SizeAttributeDTO> sizes = sku.getSizes().stream().map(item -> {
                SizeAttributeDTO size = new SizeAttributeDTO(Long.toString(item.getSizeId()),
                        item.getSize(), item.getQuantity(), item.getPrice());
                return size;
            }).collect(Collectors.toSet());

            ColorAttributeDTO color = new ColorAttributeDTO(Long.toString(sku.getColor().getColorId()),
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

        return ProductDTO.builder()
                .id(Long.toString(product.getProductId()))
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .slug(product.getSlug())
                // .category(new
                // CategoryDTO(Long.toString(product.getCategory().getCategoryId()),
                // product.getCategory().getCategoryName(), product.getCategory().getSlug()))
                // .subCategories(dtos)
                .category(Long.toString(product.getCategory().getCategoryId()))
                .subCategories(subcategoryIds)
                .details(details)
                .reviews(reviews)
                .questions(questions)
                .sku_products(skus)
                .refund_policy(product.getRefund_policy())
                .rating(product.getRating())
                .num_reviews(product.getNum_reviews())
                .shipping(product.getShipping())
                .build();
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

        if (request.getQuestions() != null) {

            // request.getQuestions().forEach(q -> q.setProduct(product)); -

            // for(ProductQA i : product.getQuestions())
            // log.info(i.getQuestion());
        }

        product.setQuestions(request.getQuestions());
        product.getQuestions().forEach(question -> {
            question.setProduct(product);
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

    public SearchResultDTO searchProducts(SearchParamsRequest params) {

        List<Long> productIds = null;
        List<Product> products = null;
        Long categoryId = null;

        if (params.getCategory() != null)
            categoryId = Long.parseLong(params.getCategory());

        productIds = productskuRepository.findProductIDBySizeAndPriceAndColor(params.getLowPrice(),
                params.getHighPrice(), params.getSize(), params.getColor());

        products = productRepository.findProductBySearchParams(params.getSearch(), categoryId,
                params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                params.getRating(), productIds);

        int totalProducts = productRepository.countProductsBySearchParams(params.getSearch(), categoryId,
                params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
                params.getRating(), productIds);

        List<ProductDTO> pDtos = products.stream().map(product -> {

            ProductDTO dto = convertToDto(product);
            return dto;
        }).collect(Collectors.toList());

        Pageable pageRequest = createPageRequestUsing(params.getPage() - 1, params.getPageSize());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), totalProducts);

        log.info(Integer.toString(start));
        log.info(Integer.toString(end));

        List<ProductDTO> pageContent = pDtos.subList(start, end);

        // List<String> subs = null;
        // List<SubCategory> subcategories =
        // categoryRepository.findSubCategoriesByCategoryName(params.getCategory());
        // subs = subcategories.stream().map(sub-> {return
        // sub.getSubcategoryName();}).collect(Collectors.toList());

        List<String> brandDB = productRepository.findBrandsByCategoryId(categoryId);

        List<CategoryDTO> categoryDTOs = categoryRepository.findAll().stream().map(category -> {
            return new CategoryDTO(Long.toString(category.getCategoryId()), category.getCategoryName(),
                    category.getSlug());
        }).collect(Collectors.toList());

        List<SubCategoryDTO> subCategoryDTOs = subCategoryRepository.findAll().stream().map(
                subcategory -> {
                    return new SubCategoryDTO(Long.toString(subcategory.getSubcategoryId()),
                            new CategoryDTO(Long.toString(subcategory.getCategory().getCategoryId()),
                                    subcategory.getCategory().getCategoryName(),
                                    subcategory.getCategory().getSlug()),
                            subcategory.getSubcategoryName());
                }).collect(Collectors.toList());

        SearchResultDTO result = SearchResultDTO.builder()
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

    public List<ProductDetailDTO> getDetails(String categoryName) {

        List<Long> ids = productRepository.findProductIDsByCategoryName(categoryName);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDTO.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }

    public List<String> getSizes(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        return productskuRepository.findSizesByProductId(ids);
    }

    public List<ProductDetailDTO> getDetails(Long categoryId) {

        List<Long> ids = productRepository.findProductIDsByCategoryId(categoryId);

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();// findDistinctAllByProductProductIdIn(ids);

        return details.stream().map(detail -> {
            return ProductDetailDTO.builder().name(detail.getName()).value(detail.getValue()).build();
        }).collect(Collectors.toList());

    }

    public ProductInfoDTO getCartProductInfo(String productId, int style, int size) {

        Optional<Product> data = productRepository.findById(Long.parseLong(productId));

        if (data.isPresent()) {

            Product product = data.get();
                
            int discount = product.getSku_products().get(style).getDiscount();
            int priceBefore = product.getSku_products().get(style).getSizes().get(size).getPrice();
            int price = discount > 0  ? priceBefore - priceBefore / discount : priceBefore;

            ColorAttributeDTO color = ColorAttributeDTO.builder()
            .id(Long.toString(product.getSku_products().get(style).getColor().getColorId()))
            .color(product.getSku_products().get(style).getColor().getColor())
            .colorImage(product.getSku_products().get(style).getColor().getColorImage())
            .build();

            List<String> subcategoryIds = product.getSubCategories().stream().map(subcategory -> {
                return Long.toString(subcategory.getSubcategoryId()); })
               .collect(Collectors.toList());
            
               List<ProductDetailDTO> details = product.getDetails().stream().map(detail->
               {
                     return ProductDetailDTO.builder()
                     .name(detail.getName())
                     .value(detail.getValue()).build();          
               }).collect(Collectors.toList());

               List<ProductQADTO> questions =   product.getQuestions().stream().map(q -> 
                {
                    return ProductQADTO.builder()
                    .question(q.getQuestion())
                    .answer(q.getAnswer())
                    .build();
                }).collect(Collectors.toList());

               List<ReviewDTO> reviews = product.getReviews().stream().map(review -> 
               {
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


            ProductInfoDTO dto = ProductInfoDTO.builder()
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
            .qty(0)
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
