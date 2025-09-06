package com.project.catalog_service.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductDto;
import com.project.common.dto.ProductDetailDto;
import com.project.common.dto.ProductInfoDto;
import com.project.common.dto.ProductQADto;
import com.project.common.dto.ProductSkuDto;
import com.project.common.dto.SubCategoryDto;
import com.project.common.message.dto.request.OrderProductRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.Category;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductSize;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.model.ProductSubcategory;
import com.project.catalog_service.model.Subcategory;

import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.ProductColorRepository;

import com.project.catalog_service.repository.ProductQARepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.repository.ProductSizeRepository;
import com.project.catalog_service.repository.ProductSkuRepository;

import com.project.catalog_service.repository.SubcategoryRepository;
import com.project.catalog_service.util.CursorPagenation;
import com.project.common.util.FileUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;
import com.project.catalog_service.dto.response.ProductResponse;

import com.project.catalog_service.mapper.ProductMapper;

//import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final SubcategoryRepository subCategoryRepository;

    private final ProductRepository productRepository;

    private final ProductSizeRepository productSizeRepository;

    private final ProductSkuRepository productskuRepository;

    private final ProductColorRepository productColorRepository;

    private final ProductQARepository productQARepository;

    private final ImageService imageService;

    // private final ObjectMapper objectMapper;

    // @Resource(name = "redisTemplate")
    // private ValueOperations<String, Object> productsOps;

    @Cacheable(value = "products", key = "#categoryName + '_' + #cursor + '_' + #pageSize")
    public List<ProductDto> getProductsByCategory(String categoryName, Long cursor, int pageSize) {
        PageRequest pageRequest = PageRequest.of(0, pageSize + 1);

        Page<Product> productsPage;
        if (cursor < 0) {
            productsPage = productRepository.findAllByCategory_CategoryNameOrderByProductIdDesc(categoryName,
                    pageRequest);
        } else {
            productsPage = productRepository.findAllByCategory_CategoryNameAndProductIdLessThanOrderByProductIdDesc(
                    categoryName, cursor, pageRequest);
        }

        CursorPagenation<Product> productCursor = CursorPagenation.of(productsPage.getContent(), pageSize);
        return ProductResponse.of(productCursor, productsPage.getContent().size()).getContents();
    }
    @Cacheable(value = "products", key = "#productName")    
    public List<ProductDto> getProductsByName(String productName) {

        List<Product> products = productRepository.findByName(productName);

        List<ProductDto> result = new ArrayList<>();

        if (products != null) {

            for (Product p : products) {
                ProductDto dto = ProductMapper.toDto(p);
                result.add(dto);
            }

            return result;
        }
        return null;
    }

    // public List<ProductDto> getProducts() {

    // List<Product> products = productRepository.findAll();
    // List<ProductDto> response = new ArrayList<ProductDto>();

    // List<ProductDto> cachedData =
    // objectMapper.convertValue(productsOps.get("products"),
    // new TypeReference<List<ProductDto>>() {});

    // if (cachedData != null) {
    // log.info("redis products");
    // return cachedData;
    // }

    // for (Product product : products) {
    // List<ProductDto> sameNameeProducts = getProductsByName(product.getName());

    // for (ProductDto p : sameNameeProducts) {
    // response.add(p);
    // }
    // }

    // productsOps.set("products" , response, 60, TimeUnit.MINUTES);

    // return response;
    // }

    // @Transactional
    // public void updateRating(Long productId, float rating) {

    // productRepository.updateRating(productId, rating);

    // Product product = productRepository.findById(productId).get();

    // List<ProductDto> cachedData =
    // objectMapper.convertValue(productsOps.get("products:"+product.getSlug()),
    // new TypeReference<List<ProductDto>>() {});

    // if (cachedData != null) {
    // cachedData.get(0).setRating(rating);
    // }
    // productsOps.set("products:"+product.getSlug(), cachedData);
    // }

    // @Transactional(readOnly = true)
    // @Cacheable(value="product_cache", cacheManager = "redisCacheManager",
    // key="#p0", unless = "#result == null")
    // public ProductDto getProductById(Long productId) {

    // Optional<Product> product = productRepository.findById(productId);

    // if (product.isPresent()) {

    // ProductDto dto = Product.convertToDto(product.get());

    // return dto;
    // }
    // return null;
    // }

    // @Transactional(readOnly = true)
    // public List<ProductDto> getProductsBySlug(String slug) {

    // List<Product> products = productRepository.findBySlug(slug);

    // List<ProductDto> cachedData =
    // objectMapper.convertValue(productsOps.get("products:"+slug),
    // new TypeReference<List<ProductDto>>() {});

    // if (cachedData != null) {
    // log.info("redis products");
    // return cachedData;
    // }

    // List<ProductDto> result = new ArrayList<>();
    // if (products != null) {

    // for (Product p : products) {
    // ProductDto dto = Product.convertToDto(p);
    // result.add(dto);
    // }

    // productsOps.set("products:" + slug , result, 60, TimeUnit.MINUTES);

    // return result;
    // }

    // return null;
    // }

    @Cacheable(value = "products", key = "'all'")
    public List<ProductDto> getProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }



     /**
     * 모든 카테고리/서브카테고리별 상품 목록을 순회하며 캐시를 미리 채웁니다 (Warm-up).
     */

    @PostConstruct // <-- 애플리케이션 시작 시 이 메서드를 자동으로 실행
    public List<ProductDto> warmUpProductCaches(int pageSize) {
        
        log.info("Starting to warm up product caches...");        

        // 1. 모든 카테고리 목록을 가져옵니다.
        List<Category> allCategories = categoryRepository.findAll();
        List<ProductDto> products = new ArrayList<>();

        for (Category category : allCategories) {
            // 2. 현재 카테고리에 속한 모든 서브카테고리 목록을 가져옵니다.
            List<Subcategory> allSubcategories = subCategoryRepository.findByCategory(category);
            
            for (Subcategory subcategory : allSubcategories) {
                int page = 0;
                
                    log.info("Fetching products for Category: {}, Subcategory: {}, Page: {}",
                            category.getCategoryName(), subcategory.getSubcategoryName(), page);

                    // 3. getProductsByCategoryAndSubcategory 메소드를 호출하여 캐시를 채웁니다.
                    products.addAll( getProductsByCategoryAndSubcategory(
                            category.getCategoryName(), subcategory.getSubcategoryName(), page, pageSize));

                
                    page++; // 다음 페이지로 이동
                
            }
        }
        log.info("Product cache warm-up completed.");
        return products;
    }

     /**
     * 카테고리 및 서브카테고리별로 상품을 10개씩 페이지네이션하여 조회합니다.
     * @param categoryName
     * @param subcategoryName
     * @param page
     * @return
     */
    @Cacheable(value = "products", key = "#categoryName + '_' + #subcategoryName + '_' + #page")
    public List<ProductDto> getProductsByCategoryAndSubcategory(String categoryName, String subcategoryName, int page, int pageSize) {
         // 기본 페이지 크기를 10으로 설정합니다.
        // 한 페이지에 1개씩 가져오도록 PageRequest 객체를 생성합니다.
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        // ProductRepository에 새로운 쿼리 메소드를 추가하여 사용해야 합니다.
        // 이 쿼리 메소드는 categoryName과 subcategoryName을 기준으로 결과를 필터링합니다.
        // 이 메소드명은 JPA 규칙을 따르며, ProductSubcategory 엔티티를 통해 조인됩니다.
        Page<Product> productsPage = productRepository
                .findAllByCategory_CategoryNameAndSubcategories_Subcategory_SubcategoryName(
                        categoryName, subcategoryName, pageRequest);

        return productsPage.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "product", key = "#productId")
    public ProductDto getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Cacheable(value = "products", key = "#slug")
    public List<ProductDto> getProductsBySlug(String slug) {
        List<Product> products = productRepository.findBySlug(slug);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @CachePut(value = "product", key = "#productId")
    public ProductDto updateRating(Long productId, float rating) {
        productRepository.updateRating(productId, rating);
        Product product = productRepository.findById(productId).orElseThrow();
        return convertToDto(product);
    }

    
    public List<ProductSku> load(List<ProductInfoLoadRequest> products, List<MultipartFile> images,
            List<MultipartFile> colorImages) throws IOException {

        List<ProductSku> result = new ArrayList<>();

        int i = 0;
        for (ProductInfoLoadRequest pr : products) {

            ProductSku sku = null;

            sku = load(pr, images.get(i), colorImages.get(i));
            result.add(sku);
            i++;
        }

        return result;
    }

    
    public ProductSku load(ProductInfoLoadRequest request, MultipartFile images, MultipartFile colorImage)
            throws IOException {
        Product product = createOrFindProduct(request);
        return loadSku(request, product, images, colorImage);
    }

    @Transactional
    @CachePut(value = "product", key = "#result.productId")
    private Product createOrFindProduct(ProductInfoLoadRequest request) {
        if (request.getParent() != null && !request.getParent().isEmpty()) {
            return productRepository.findById(Long.parseLong(request.getParent()))
                    .orElseThrow(() -> new IllegalArgumentException("Parent product not found"));
        } else {
            Category category = createOrFindCategory(request.getCategory().getName(), request.getCategory().getSlug());
            List<Subcategory> subcategories = createOrFindSubcategories(category, request.getSubCategories());

            Product product = Product.builder()
                    .brand(request.getBrand())
                    .name(request.getName())
                    .description(request.getDescription())
                    .slug(request.getSlug())
                    .shipping(new BigDecimal(request.getShippingFee()))
                    .category(category)
                    .build();

            Set<ProductSubcategory> productSubcategories = subcategories.stream()
                    .map(sub -> ProductSubcategory.builder().product(product).subcategory(sub).build())
                    .collect(Collectors.toSet());
            product.setSubcategories(productSubcategories);

            productRepository.save(product);
            return product;
        }
    }

    private Category createOrFindCategory(String categoryName, String slug) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName, slug)));
    }

    private List<Subcategory> createOrFindSubcategories(Category category, List<SubCategoryDto> subCategoryDtos) {
        List<String> subcategoryNames = subCategoryDtos.stream()
                .map(SubCategoryDto::getName)
                .collect(Collectors.toList());
        List<Subcategory> existingSubcategories = subCategoryRepository
                .findByCategory_CategoryNameAndSubcategoryNameIn(category.getCategoryName(), subcategoryNames);

        List<Subcategory> newSubcategories = subCategoryDtos.stream()
                .filter(dto -> existingSubcategories.stream()
                        .noneMatch(existing -> existing.getSubcategoryName().equals(dto.getName())))
                .map(dto -> new Subcategory(dto.getName(), dto.getSlug(), category))
                .collect(Collectors.toList());

        subCategoryRepository.saveAll(newSubcategories);
        existingSubcategories.addAll(newSubcategories);
        return existingSubcategories;
    }

    @Transactional
    @CachePut(value = "product", key = "#result.productId")
    public ProductSku addProduct(ProductRequest request, List<MultipartFile> images, MultipartFile colorImage)
            throws IOException {

        if (request.getParent() != null && request.getParent().length() > 0) {

            Optional<Product> existed = productRepository.findById(Long.parseLong(request.getParent()));

            if (existed.isPresent()) {

                ProductSku newSku = createSku(request, existed.get(), images, colorImage);
                existed.get().getSkus().add(newSku);

                productRepository.save(existed.get());

                return newSku;
            }
        } else {

            Product product = Product.builder()
                    .brand(request.getBrand())
                    .name(request.getName())
                    .description(request.getDescription())
                    .slug(request.getSlug())
                    .build();

            product.setShipping(new BigDecimal(request.getShippingFee()));

            Optional<Category> category = categoryRepository.findById(Long.parseLong(request.getCategory()));

            if (category != null) {

                product.setCategory(category.get());

                List<Long> subcategoryIds = new ArrayList<>();
                for (String subcategory : request.getSubCategories()) {
                    log.info(subcategory);
                    subcategoryIds.add(Long.parseLong(subcategory));
                }

                List<Subcategory> subCategories = subCategoryRepository.findBySubcategoryIdIn(subcategoryIds);

                if (subCategories != null) {

                    Set<ProductSubcategory> list = new HashSet<>();

                    for (Subcategory sub : subCategories) {

                        ProductSubcategory productSubCategory = ProductSubcategory.builder()
                                .product(product)
                                .subcategory(sub)
                                .build();

                        list.add(productSubCategory);
                    }
                    product.setSubcategories(list);

                    productRepository.save(product);

                    return createSku(request, product, images, colorImage);
                }
            }
        }

        return null;
    }
    
    private ProductSku loadSku(ProductInfoLoadRequest request, Product product, MultipartFile image,
            MultipartFile colorImage) throws IOException {

        ProductSku skuProject = ProductSku.builder()
                .sku(request.getSku())
                .build();

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

        String filename = FileUtil.getRandomFilename();
        String filepath = imageService.upload(image, filename);
        imageUrls.add(filepath);

        skuProject.setImages(imageUrls);

        filename = "color_" + FileUtil.getRandomFilename();
        String colorImageUrl = imageService.upload(colorImage, filename);

        skuProject.setColor(ProductColor.builder().colorImage(colorImageUrl)
                .color(request.getColor().getColor()).build());

        productColorRepository.save(skuProject.getColor());

        request.getSizes().forEach(size -> size.setSku(skuProject));

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

            // With cascade=CascadeType.MERGE and orphanRemoval=true, saving the parent
            // product entity will automatically handle saving the children.
            // Manual saving with `productQARepository.save(question)` is often not needed.
            // productQARepository.save(question);
        });

        return productskuRepository.save(skuProject);

    }

    private ProductSku createSku(ProductRequest request, Product product, List<MultipartFile> images,
            MultipartFile colorImage) throws IOException {

        ProductSku skuProject = ProductSku.builder()
                .sku(request.getSku())
                .build();

        if (request.getDiscount() != null)
            skuProject.setDiscount(Integer.parseInt(request.getDiscount()));

        skuProject.setColor(request.getColor());

        List<String> imageUrls = new ArrayList<String>();

        for (MultipartFile image : images) {
            String filename = FileUtil.getRandomFilename();
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

        String filename = "color_" + FileUtil.getRandomFilename();
        String colorImageUrl = imageService.upload(colorImage, filename);

        skuProject.getColor().setColorImage(colorImageUrl);

        request.getSizes().forEach(size -> size.setSku(skuProject));

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

    public ProductColorDto getColorAttributeInfo(Long colorId) {

        ProductColor color = productColorRepository.findById(colorId).orElse(null);

        if (null != color) {

            ProductColorDto dto = ProductColorDto.builder()
                    .id(Long.toString(color.getColorId()))
                    .color(color.getColor())
                    .colorImage(color.getColorImage())
                    .build();

            return dto;

        }

        log.info("color is null : " + Long.toString(colorId));

        return null;
    }

    public ProductInfoDto getCartProductInfo(Long productId, int style, int size) {

        Optional<Product> data = productRepository.findById(productId);

        if (data.isPresent()) {

            Product product = data.get();

            Optional<ProductSku> optionalSku = product.getSkus().stream()
                    .skip(style) // get(style)과 유사하게 특정 순서의 요소를 건너뛰지만, Set은 순서가 없으므로 정확한 결과를 보장하지 않습니다.
                    .findFirst();

            if (optionalSku.isPresent()) {

                ProductSku sku = optionalSku.get();

                BigDecimal discount = new BigDecimal(sku.getDiscount());
                BigDecimal priceBefore = sku.getSizes().get(size).getPrice();

                BigDecimal price = discount.compareTo(new BigDecimal(0)) > 0
                        ? priceBefore.subtract(priceBefore.divide(discount, 2, RoundingMode.HALF_UP))
                        : priceBefore;

                ProductColorDto color = ProductColorDto.builder()
                        .id(Long.toString(sku.getColor().getColorId()))
                        .color(sku.getColor().getColor())
                        .colorImage(sku.getColor().getColorImage())
                        .build();

                List<String> subcategoryIds = product.getSubcategories().stream().map(productSubcategory -> {
                    return Long.toString(productSubcategory.getSubcategory().getSubcategoryId());
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

                ProductInfoDto dto = ProductInfoDto.builder()
                        .id(Long.toString(product.getProductId()))
                        .style(style)
                        .name(product.getName())
                        .description(product.getDescription())
                        .slug(product.getSlug())
                        .sku(sku.getSku())
                        .brand(product.getBrand())
                        .shipping(product.getShipping().toPlainString())
                        .images(sku.getImages())
                        .color(color)
                        .size(sku.getSizes().get(size).getSize())
                        .price(price.toPlainString())
                        .priceBefore(priceBefore.toPlainString())
                        .qty(1)
                        .quantity(sku.getSizes().get(size).getQuantity())
                        .category(Long.toString(product.getCategory().getCategoryId()))
                        .subCategories(subcategoryIds)
                        .questions(questions)
                        .details(details)
                        .discount(sku.getDiscount())
                        .build();

                return dto;
            }

        }
        log.info("product is null : " + Long.toString(productId));
        return null;

    }

    private ProductDto convertToDto(Product product) {
        return ProductMapper.toDto(product); // Product.java에 있는 static 메서드를 사용
    }

    private Product getProductModel(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product.get();
        }
        return null;
    }

    private ProductSku getProductSkuModel(Long productId, String size, Long colorId) {
        Optional<ProductSku> sku = productskuRepository.findByProductProductIdAndSizesSizeAndColorColorId(productId,
                size, colorId);
        if (sku.isPresent()) {
            return sku.get();
        }
        return null;
    }

    private ProductSize getProductSizeModel(Long skuId, String size) {
        Optional<ProductSize> productSize = productSizeRepository.findBySkuSkuproductIdAndSize(skuId, size);
        if (productSize.isPresent()) {
            return productSize.get();
        }
        return null;
    }

    // @Transactional
    // private ProductSku updateSoldValue(Long productId, String slug, String size,
    // Long colorId, int qty) {

    // ProductSku sku = getProductSkuModel(productId, size, colorId);
    // if (sku != null) {
    // int sold = sku.getSold() + qty;
    // sku.setSold(sold);

    // List<ProductDto> cachedData =
    // objectMapper.convertValue(productsOps.get("products:"+slug),
    // new TypeReference<List<ProductDto>>() {});

    // if (!cachedData.isEmpty()) {

    // cachedData.get(0).getSkus().stream().forEach(
    // item -> {
    // if (item.hasSize(size) && item.hasColor(Long.toString(colorId))) {
    // item.setSold(sold);
    // }
    // }
    // );

    // productsOps.set("products:"+slug, cachedData);

    // }

    // sku = productskuRepository.save(sku);
    // return sku;
    // }

    // return null;
    // }

    // @Transactional
    // private ProductSize updateProductSize_Qty(String slug, Long skuId, String
    // size, int qty) {
    // ProductSize productSize = getProductSizeModel(skuId, size);

    // if (productSize != null) {

    // if (productSize.getQuantity() >= qty) {

    // int currQty = productSize.getQuantity() - qty;
    // productSize.setQuantity(currQty);

    // List<ProductDto> cachedData =
    // objectMapper.convertValue(productsOps.get("products:"+slug),
    // new TypeReference<List<ProductDto>>() {});

    // if (!cachedData.isEmpty()) {
    // cachedData.get(0).getSkus().stream().forEach(
    // item -> {
    // if (item.hasSize(size)) {
    // item.getSize(size).setQuantity(currQty);
    // }
    // }
    // );

    // productsOps.set("products:"+slug, cachedData);

    // }

    // productSize = productSizeRepository.save(productSize);
    // return productSize;
    // }
    // }

    // return null;
    // }

    // A transactional public method that orchestrates the update
    @Transactional
     @CachePut(value = "product", key = "#result.productId")
    public ProductSku updateSoldValue(Long productId, String slug, String size, Long colorId, int qty) {
        Optional<ProductSku> optionalSku = productskuRepository
                .findByProductProductIdAndSizesSizeAndColorColorId(productId, size, colorId);

        if (optionalSku.isPresent()) {
            ProductSku sku = optionalSku.get();
            int sold = sku.getSold() + qty;
            sku.setSold(sold);

            // 1. Database Update
            productskuRepository.save(sku);

            // 2. Cache Update
            // This method call will trigger the @CachePut and update the cache.
            updateProductDtoCache(slug, sku);

            return sku;
        }
        return null;
    }

    // This method updates the cache with the latest data.
    // It is public so that the @CachePut annotation can be applied by Spring.
    @Transactional
    @CachePut(value = "products", key = "#slug")
    public List<ProductDto> updateProductDtoCache(String slug, ProductSku updatedSku) {
        // Here, we re-fetch the product from the DB or a separate method
        // to ensure the DTO list contains the latest data.
        List<Product> products = productRepository.findBySlug(slug);

        // Convert entities to DTOs and update the specific SKU's sold value
        List<ProductDto> updatedDtos = products.stream()
                .map(product -> {
                    ProductDto dto = convertToDto(product);
                    // Find and update the specific SKU in the DTO list
                    dto.getSkus().stream()
                            .filter(skuDto -> skuDto.getSku().equals(updatedSku.getSku()))
                            .findFirst()
                            .ifPresent(skuDto -> skuDto.setSold(updatedSku.getSold()));
                    return dto;
                })
                .collect(Collectors.toList());

        return updatedDtos;
    }

    @Transactional
    @CachePut(value = "product", key = "#result.skuId")
    public ProductSize updateProductSize_Qty(String slug, Long skuId, String size, int qty) {
        ProductSize productSize = getProductSizeModel(skuId, size);

        if (productSize != null) {
            if (productSize.getQuantity() >= qty) {
                int currQty = productSize.getQuantity() - qty;
                productSize.setQuantity(currQty);

                // 데이터베이스 저장
                productSize = productSizeRepository.save(productSize);

                // 데이터베이스 업데이트 후 캐시를 갱신하는 메서드 호출
                refreshProductsCache(slug);

                return productSize;
            }
        }
        return null;
    }

    /**
     * @CachePut 어노테이션을 사용하여 Redis 캐시를 갱신합니다.
     *           이 메서드는 오직 캐시를 업데이트하는 역할만 담당합니다.
     */
    @Transactional
    @CachePut(value = "products", key = "#slug")
    public List<ProductDto> refreshProductsCache(String slug) {
        log.info("Refreshing cache for slug: {}", slug);
        List<Product> products = productRepository.findBySlug(slug);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductSize productUpdated(ProductUpdateRequest request) {

        if (request != null) {

            List<OrderProductRequest> orderProducts = request.getOrderProducts();

            if (!orderProducts.isEmpty()) {

                for (OrderProductRequest item : orderProducts) {

                    Product product = getProductModel(item.getProductId());

                    if (product != null) {

                        ProductSku sku = updateSoldValue(product.getProductId(), product.getSlug(), item.getSize(),
                                item.getColorId(),
                                item.getQty());

                        if (sku != null) {

                            ProductSize productSize = updateProductSize_Qty(product.getSlug(), sku.getSkuproductId(),
                                    item.getSize(),
                                    item.getQty());
                            return productSize;
                        }
                    }
                }

            }

        }
        return null;
    }
}
