    package com.project.catalog_service.batch.processor;

import com.project.catalog_service.dto.data.ProductColorCsvDto;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.service.ImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductColorItemProcessor implements ItemProcessor<ProductColorCsvDto, ProductColor> {


    private final ImageService imageService;

    @Override
    public ProductColor process(ProductColorCsvDto item) throws Exception {
        
        // CSV 파일의 color_image 경로를 S3에 업로드하고 URL을 받습니다.
        String imageUrl = null;
        if (item.getColor_image() != null && !item.getColor_image().isEmpty()) {
            try {
                // CSV 경로에서 resources/images 폴더를 기준으로 하는 상대 경로를 만듭니다.
                // 예: "images\red_shoe.jpg" -> "images/red_shoe.jpg"
                String cleanedPath = item.getColor_image().trim().replace("\\", "/");
                // ImageService의 upload 함수를 호출하여 S3에 이미지 업로드
                imageUrl = imageService.upload(cleanedPath);
            } catch (Exception e) {
                System.err.println("Failed to upload image from path: " + item.getColor_image());
                e.printStackTrace();
            }
        }
        
        return ProductColor.builder()
        //.colorId(item.getColor_id())
                .color(item.getColor())
                .colorImage(imageUrl) // 업로드된 이미지 URL 설정
                .version(0L)
                .build();
    }
    
    
}