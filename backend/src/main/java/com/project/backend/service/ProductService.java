package com.project.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.backend.dto.CategoryDTO;
import com.project.backend.dto.ColorAttributeDTO;
import com.project.backend.dto.ProductDTO;
import com.project.backend.dto.ProductSkuDTO;
import com.project.backend.dto.SizeAttributeDTO;
import com.project.backend.dto.SubCategoryDTO;
import com.project.backend.dto.UserDTO;
import com.project.backend.model.Product;
import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductQA;
import com.project.backend.model.ProductSku;
import com.project.backend.model.SubCategory;
import com.project.backend.model.User;
import com.project.backend.repository.CategoryRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.repository.SubCategoryRepository;
import com.project.backend.security.request.ProductRequest;

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

    public ProductDTO getProduct(String productName ) {

          Product product = productRepository.findByName(productName);

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

        Set<ProductSkuDTO> skus = product.getSku_products().stream().map(sku -> 
            {   

                List<String> base64Image = new ArrayList<String>();
                for (String image : sku.getImages()) {
                    base64Image.add(image);
                }

                

                Set<SizeAttributeDTO> sizes = sku.getSizes().stream().map(item-> {
                    SizeAttributeDTO size = new SizeAttributeDTO(item.getSizeId(),
                    item.getSize(), item.getQuantity(), item.getPrice());
                    return size;                    
                }).collect(Collectors.toSet());  
                
                ColorAttributeDTO color = new ColorAttributeDTO(sku.getColor().getColorId(), 
                sku.getColor().getColor(), sku.getColor().getColorImage());
                
                
                ProductSkuDTO dto = new ProductSkuDTO(
                    sku.getSkuproductId(),
                    sku.getSku(),
                    base64Image,
                    sku.getDiscount(),
                    sku.getSold(),
                    sizes,
                    color
                );

                return dto;                
            }
        ).collect(Collectors.toSet());

        
        return  new ProductDTO(
            product.getProductId(),
            product.getName(),
            product.getDescription(),
            product.getBrand(),
            product.getSlug(),
            new CategoryDTO(product.getCategory().getCategoryName(), product.getCategory().getSlug()),
            dtos,
            product.getDetails(),
            product.getReviews(),
            product.getQuestions(),
            skus,
            product.getRefund_policy(),
            product.getRating(),
            product.getNum_reviews(),
            product.getShipping()                   
        );
    }

    public ProductSku addProduct(ProductRequest request, MultipartFile[] images, MultipartFile colorImage ) throws IOException {

        Product product = new Product();

        product.setBrand(request.getBrand());
        product.setName(request.getName());
        product.setDescription(request.getDescription());

        request.getDetails().forEach(detail -> detail.setProduct(product));
        product.setDetails(request.getDetails());

        
        request.getQuestions().forEach(q -> q.setProduct(product));
        
        product.setQuestions(request.getQuestions());  
              
        if(request.getShippingFee().length() > 0)
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

                ArrayList<String> bytes = new ArrayList<String>();
                for(MultipartFile image : images) {
                    //String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                    bytes.add(encodeFileToBase64((image)));
                    //bytes.add(encodeFileToBase64(image).getBytes());
                }
                if (bytes.size() > 0)
                    skuProject.setImages(bytes);

                skuProject.getColor().setColorImage(encodeFileToBase64(colorImage));                  
                    //StringUtils.cleanPath(colorImage.getOriginalFilename()).getBytes());


                request.getSizes().forEach(size->size.setSku_product(skuProject));

                skuProject.setSizes(request.getSizes());       

                skuProject.setProduct(product);

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

}
