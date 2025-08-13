package com.project.catalog_service.batch.processor;

import com.project.catalog_service.dto.data.ProductSkuCsvDto;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.repository.ProductColorRepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.service.ImageService;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductSkuItemProcessor implements ItemProcessor<ProductSkuCsvDto, ProductSku> {

    private final ProductRepository productRepository;
    private final ProductColorRepository productColorRepository;
    private final ImageService imageService;

    @Override
    public ProductSku process(ProductSkuCsvDto item) throws Exception {
        Product product = productRepository.findById(item.getProduct_id())
            .orElseThrow(() -> new IllegalArgumentException("Invalid product_id: " + item.getProduct_id()));
        ProductColor color = productColorRepository.findById(item.getColor_id())
            .orElseThrow(() -> new IllegalArgumentException("Invalid color_id: " + item.getColor_id()));

        List<String> imageUrls = new ArrayList<>();
        if (item.getImages() != null && !item.getImages().isEmpty()) {
            List<String> imagePaths = Arrays.asList(item.getImages().split(","));
            imageUrls = imagePaths.stream()
                    .map(path -> {
                        try {
                            // CSV 경로에서 resources/images 폴더를 기준으로 하는 상대 경로를 만듭니다.
                            // 예: "images\T-shirt.jpg" -> "images/T-shirt.jpg"
                            String cleanedPath = path.trim().replace("\\", "/");
                            // ClassPathResource는 classpath(resources 폴더)를 기준으로 파일을 찾습니다.
                            return imageService.upload(cleanedPath);
                        } catch (Exception e) {
                            System.err.println("Failed to upload image from path: " + path);
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(url -> url != null)
                    .collect(Collectors.toList());
        }    

        return ProductSku.builder()
            //.skuproductId(item.getSkuproduct_id())
            .sku(item.getSku())
            .images(imageUrls)
            .discount(item.getDiscount())
            .sold(item.getSold())
            .color(color)
            .product(product)
            .version(0L) // 초기 버전 설정
            .build();
    }
}
