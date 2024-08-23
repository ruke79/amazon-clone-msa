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
import com.project.backend.dto.ProductSkuDTO;
import com.project.backend.dto.SearchResultDTO;
import com.project.backend.dto.SizeAttributeDTO;
import com.project.backend.dto.SubCategoryDTO;
import com.project.backend.dto.UserDTO;
import com.project.backend.model.Product;
import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.ProductQA;
import com.project.backend.model.ProductSku;
import com.project.backend.model.SubCategory;
import com.project.backend.model.User;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.ProductDetailsRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.repository.SubCategoryRepository;
import com.project.backend.security.request.ProductRequest;
import com.project.backend.security.request.SearchParamsRequest;

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
    ProductRepository  productRepository;

    @Autowired
    ProductSkuRepository productskuRepository;

    @Autowired
    ProductDetailsRepository productDetailsRepository;
    


    public ProductDTO getProductByName(String productName ) {

          Product product = productRepository.findByName(productName);

        if (product != null) {

            ProductDTO dto = convertToDto(product);

            return dto;
        }
        return null;
    }

    public ProductDTO getProductById(Long productId ) {

        Optional<Product> product = productRepository.findById(productId);
        
      if (product.isPresent()) {

        log.info("GetProductById");

          ProductDTO dto = convertToDto(product.get());

          return dto;
      }
      return null;
  }

    public ProductDTO getProductBySlug(String slug ) {

        Product product = productRepository.findBySlug(slug);

        log.info(Long.toString(product.getProductId()));

      if (product != null) {

          ProductDTO dto = convertToDto(product);

          return dto;
      }
      return null;
  }

    private ProductDTO convertToDto(Product product) {
         
        List<String> dtos = product.getSubCategories().stream().map(subcategory -> subcategory.getSubcategoryName()).
        // new SubCategoryDTO(subcategory.getSubcategoryId(), subcategory.getSubcategoryName())).
        collect(Collectors.toList());

        Set<ProductDetailDTO> details = product.getDetails().stream().map(detail->
        {
              return ProductDetailDTO.builder()
              .name(detail.getName())
              .value(detail.getValue()).build();          
        }).collect(Collectors.toSet());
        

        Set<ProductSkuDTO> skus = product.getSku_products().stream().map(sku -> 
            {   

                
                List<String> base64Image = new ArrayList<String>();                
                for (String image : sku.getImages()) {
                    log.info("image: " + image);
                    base64Image.add(image);                    
                }

                

                Set<SizeAttributeDTO> sizes = sku.getSizes().stream().map(item-> {
                    SizeAttributeDTO size = new SizeAttributeDTO(item.getSizeId(),
                    item.getSize(), item.getQuantity(), item.getPrice());
                    return size;                    
                }).collect(Collectors.toSet());  
                
                ColorAttributeDTO color = new ColorAttributeDTO(sku.getColor().getColorId(), 
                sku.getColor().getColor(), sku.getColor().getColorImage());
                
                
                ProductSkuDTO dto = ProductSkuDTO.builder()
                    .skuproductId(sku.getSkuproductId())
                    .sku(sku.getSku())
                    .images(base64Image)
                    .discount(sku.getDiscount())
                    .sold(sku.getSold())
                    .sizes(sizes)
                    .color(color)
                    .build();
                

                return dto;                
            }
        ).collect(Collectors.toSet());

        
        return  ProductDTO.builder()
            .productId(product.getProductId())
            .name(product.getName())
            .description(product.getDescription())
            .brand(product.getBrand())
            .slug(product.getSlug())
            .category(new CategoryDTO(product.getCategory().getCategoryId(), product.getCategory().getCategoryName(), product.getCategory().getSlug()))                        
            .subCategories(dtos)
            .details(details)
            .reviews(product.getReviews())
            .questions(product.getQuestions())
            .sku_products(skus)
            .refund_policy(product.getRefund_policy())
            .rating(product.getRating())
            .num_reviews(product.getNum_reviews())
            .shipping(product.getShipping())                   
            .build();        
    }

    public ProductSku addProduct(ProductRequest request, List<String> images, String colorImage ) throws IOException {

        Product product = new Product();

        product.setBrand(request.getBrand());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSlug(request.getSlug());

                
        
       
        
              
        if(request.getShippingFee() != null)
            product.setShipping(Integer.parseInt(request.getShippingFee()));

                        
        ProductCategory category = categoryRepository.findByCategoryName(request.getCategory());

        log.info(category.getCategoryName());
        

        if ( category != null)
        {        
            
            product.setCategory(category);

            List<SubCategory> subCategories = subCategoryRepository.findBySubcategoryNameIn(request.getSubCategories());

            

            if (subCategories != null) {                
                                
                
                product.setSubCategories(subCategories);

                
                ProductSku skuProject = new ProductSku();

                skuProject.setSku(request.getSku());

                if (request.getDiscount() != null)
                    skuProject.setDiscount(Integer.parseInt(request.getDiscount()));
                
                skuProject.setColor(request.getColor());

                // ArrayList<String> bytes = new ArrayList<String>();
                // for(MultipartFile image : images) {                    
                //     bytes.add(encodeFileToBase64((image)));
                    
                // }
                // if (bytes.size() > 0)
                //     skuProject.setImages(bytes);                
                // skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));                  

                for(String image : images) {
                    log.info("Image: " + image);
                }

                skuProject.setImages(images);
                // cloudinary에 저장된 이미지 URL
                skuProject.getColor().setColorImage(colorImage);
                    


                request.getSizes().forEach(size->size.setSku_product(skuProject));

                skuProject.setSizes(request.getSizes());       

                skuProject.setProduct(product);

                if (request.getDetails() != null) {                  
                           
                    product.setDetails(request.getDetails());            

                    product.getDetails().forEach(detail -> {
                        detail.setProduct(product); 
                        log.info(detail.getValue());                
                    } );          
                }
                
                if (request.getQuestions() != null ) {
                    request.getQuestions().forEach(q -> q.setProduct(product));
                    product.setQuestions(request.getQuestions());  
                }
                

                productRepository.save(product);

                return productskuRepository.save(skuProject);
            }
        }

        return null;
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
        return  Base64.decodeBase64(encodedString);
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }


    public SearchResultDTO searchProducts(SearchParamsRequest params) {

                
        List<Long> productIds = null;
        List<Product> products = null;
        // if(params.getSort().equals("popular")) {
            
        //     productIds = productskuRepository.findProductIDBySizeAndPriceAndColorOrderBySoldDesc
        //     (params.getLowPrice(), params.getHighPrice(), params.getSize(), params.getColor());

        //     products = productRepository.findProductBySearchParamsOrderByRatingDesc(params.getSearch(), categoryId, 
        // params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
        // params.getRating(), productIds);

        // products.forEach(product-> log.info(product.getName()));

                    
        // }
        // else 
        {
             productIds = productskuRepository.findProductIDBySizeAndPriceAndColor
             (params.getLowPrice(), params.getHighPrice(), params.getSize(), params.getColor());

            products = productRepository.findProductBySearchParams(params.getSearch(), params.getCategory(), 
        params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
        params.getRating(), productIds);
        }   
        
        int totalProducts = productRepository.countProductsBySearchParams(params.getSearch(), params.getCategory(), 
        params.getStyle(), params.getBrand(), params.getMaterial(), params.getGender(),
        params.getRating(), productIds);

        
        List<ProductDTO> pDtos = products.stream().map(product -> {

            ProductDTO dto = convertToDto(product);     
            return dto;
        }).collect(Collectors.toList());

        
        
        Pageable pageRequest = createPageRequestUsing(params.getPage(), params.getPageSize());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), pDtos.size());

        log.info(Integer.toString(start));
        log.info(Integer.toString(end));

        List<ProductDTO> pageContent = pDtos.subList(start, end);

        // List<String> subs = null;        
        // List<SubCategory> subcategories  = categoryRepository.findSubCategoriesByCategoryName(params.getCategory());
        // subs =  subcategories.stream().map(sub-> {return sub.getSubcategoryName();}).collect(Collectors.toList());
        
        

        List<String> brandDB = productRepository.findBrandsByCategoryId(params.getCategory());
        
        List<CategoryDTO> categoryDTOs = categoryRepository.findAll().stream().map(category ->
         { return new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getSlug()); }
        ).collect(Collectors.toList());

        List<SubCategoryDTO> subCategoryDTOs = subCategoryRepository.findAll().stream().map(
            subcategory->{ return new SubCategoryDTO(subcategory.getSubcategoryId(), 
                new CategoryDTO(subcategory.getCategory().getCategoryId(), subcategory.getCategory().getCategoryName(), 
                subcategory.getCategory().getSlug()),
                subcategory.getSubcategoryName());}        
        ).collect(Collectors.toList());

        SearchResultDTO result = SearchResultDTO.
            builder().product(new PageImpl<>(pageContent, pageRequest, pDtos.size()))
            .categories(categoryDTOs)
            .subCategories(subCategoryDTOs)
            .colors(getColors(params.getCategory()))
            .sizes(getSizes(params.getCategory()))
            .details(getDetails(params.getCategory()))
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

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();//findDistinctAllByProductProductIdIn(ids);
        
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

        List<ProductDetails> details = productDetailsRepository.findDistinctAll();//findDistinctAllByProductProductIdIn(ids);
        
        return details.stream().map(detail -> {
            return ProductDetailDTO.builder().name(detail.getName()).value(detail.getValue()).build();            
        }).collect(Collectors.toList());

    }

}
