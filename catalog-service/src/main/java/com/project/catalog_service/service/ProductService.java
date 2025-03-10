package com.project.catalog_service.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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


import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductDto;
import com.project.common.dto.ProductDetailDto;
import com.project.common.dto.ProductInfoDto;
import com.project.common.dto.ProductQADto;



import com.project.common.dto.SubCategoryDto;
import com.project.common.message.dto.request.OrderProductRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductCategory;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductSize;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.model.SubCategory;

import com.project.catalog_service.repository.CategoryRepository;
import com.project.catalog_service.repository.ProductColorRepository;

import com.project.catalog_service.repository.ProductQARepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.repository.ProductSizeRepository;
import com.project.catalog_service.repository.ProductSkuRepository;

import com.project.catalog_service.repository.SubCategoryRepository;
import com.project.common.util.FileUtil;

import org.springframework.transaction.annotation.Transactional;

import com.project.catalog_service.dto.request.ProductInfoLoadRequest;
import com.project.catalog_service.dto.request.ProductRequest;


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

    private final ProductSizeRepository productSizeRepository;

    private final ProductSkuRepository productskuRepository;

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
                existed.get().getSkus().add(newSku);

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
                product.setShipping(new BigDecimal(request.getShippingFee()));

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
                existed.get().getSkus().add(newSku);

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
                product.setShipping(new BigDecimal(request.getShippingFee()));

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

        log.info("color is null : " + Long.toString(colorId)) ;

        return null;
    }
   
    public ProductInfoDto getCartProductInfo(Long productId, int style, int size) {

        Optional<Product> data = productRepository.findById(productId);

        if (data.isPresent()) {

            Product product = data.get();

            BigDecimal discount = new BigDecimal(product.getSkus().get(style).getDiscount());
            BigDecimal priceBefore = product.getSkus().get(style).getSizes().get(size).getPrice();
            
            
            BigDecimal price =  discount.compareTo(new BigDecimal(0)) > 0 ? 
              priceBefore.subtract(priceBefore.divide(discount, 2,RoundingMode.HALF_UP)) 
             : priceBefore;

            ProductColorDto color = ProductColorDto.builder()
                    .id(Long.toString(product.getSkus().get(style).getColor().getColorId()))
                    .color(product.getSkus().get(style).getColor().getColor())
                    .colorImage(product.getSkus().get(style).getColor().getColorImage())
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

            ProductInfoDto dto = ProductInfoDto.builder()
                    .id(Long.toString(product.getProductId()))
                    .style(style)
                    .name(product.getName())
                    .description(product.getDescription())
                    .slug(product.getSlug())
                    .sku(product.getSkus().get(style).getSku())
                    .brand(product.getBrand())
                    .shipping(product.getShipping().toPlainString())
                    .images(product.getSkus().get(style).getImages())
                    .color(color)
                    .size(product.getSkus().get(style).getSizes().get(size).getSize())
                    .price(price.toPlainString())
                    .priceBefore(priceBefore.toPlainString())
                    .qty(1)
                    .quantity(product.getSkus().get(style).getSizes().get(size).getQuantity())
                    .category(Long.toString(product.getCategory().getCategoryId()))
                    .subCategories(subcategoryIds)
                    .questions(questions)
                    .details(details)                    
                    .discount(product.getSkus().get(style).getDiscount())
                    .build();

            return dto;
        }

        return null;
    }

    @Transactional(readOnly =true)
    private Product getProductModel(Long productId) 
    {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product.get();
        }
        return null;
    }

    @Transactional(readOnly =true)
    private ProductSku getProductSkuModel(Long productId, String size, Long colorId) 
    {
        Optional<ProductSku> sku = productskuRepository.
        findByProductProductIdAndSizesSizeAndColorColorId(productId, size, colorId);
        if (sku.isPresent()) {
            return sku.get();
        }
        return null;
    }

    @Transactional(readOnly =true)
    private ProductSize getProductSizeModel(Long skuId, String size) 
    {
        Optional<ProductSize> productSize = productSizeRepository.findBySkuSkuproductIdAndSize(skuId, size);
        if (productSize.isPresent()) {
            return productSize.get();
        }
        return null;
    }

    @Transactional 
    private ProductSku updateSoldValue(Long productId, String size, Long colorId, int qty) {

        ProductSku sku = getProductSkuModel(productId, size, colorId);
        if( sku != null) {
            sku.setSold(qty);

            sku = productskuRepository.save(sku);
            return sku;                        
        }
        return null;
    }

    @Transactional
    private ProductSize updateProductSize_Qty(Long skuId, String size, int qty) {
        ProductSize productSize = getProductSizeModel(skuId, size);

        if ( productSize != null) {

            if ( productSize.getQuantity() >= qty) {
                productSize.setQuantity(productSize.getQuantity() - qty);
                productSize = productSizeRepository.save(productSize);
                return productSize;
            }            
        }

        return null;
    }


    

    @Transactional
    public ProductSize productUpdated(ProductUpdateRequest request) {

        if (request != null) {
        
            List<OrderProductRequest> orderProducts = request.getOrderProducts();

            if (!orderProducts.isEmpty()) {

                for(OrderProductRequest item: orderProducts) {

                    Product product = getProductModel(item.getProductId());

                    if (product != null) {

                        ProductSku sku = updateSoldValue(product.getProductId(), item.getSize(), item.getColorId(), item.getQty());

                        if (sku != null) {

                            ProductSize productSize = updateProductSize_Qty(sku.getSkuproductId(), item.getSize(), item.getQty());                    
                            return productSize;
                        }
                    }
                }

            }

        }
        return null;
    }
}
