package com.project.catalog_service.batch.processor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.catalog_service.dto.data.ProductSkuCsvDto;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.repository.ProductColorRepository;
import com.project.catalog_service.repository.ProductRepository;
import com.project.catalog_service.service.ImageService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSkuItemProcessorTest {

    @InjectMocks
    private ProductSkuItemProcessor processor;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductColorRepository productColorRepository;
    @Mock
    private ImageService imageService;

    private ProductSkuCsvDto csvDto;
    private Product product;
    private ProductColor color;

    // Helper method to create Product instance for testing
    private Product createProduct(Long id) {
        Product p = null;
        try {
            var constructor = Product.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            p = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Product instance for test", e);
        }
        return p;
    }

    @BeforeEach
    void setUp() throws Exception {
        csvDto = new ProductSkuCsvDto();
        csvDto.setProduct_id(1L);
        csvDto.setColor_id(1L);
        csvDto.setImages("images\\food\\Image_2.jpg,images\\food\\Image_9.jpg");

        product = createProduct(1L);

        try {
            var colorConstructor = ProductColor.class.getDeclaredConstructor();
            colorConstructor.setAccessible(true);
            color = colorConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ProductColor instance for test", e);
        }
        color.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productColorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(imageService.upload(anyString())).thenReturn("http://s3.url/uploaded-image.jpg");
    }

    @Test
    void process_should_upload_images_with_correct_paths() throws Exception {
        // When
        processor.process(csvDto);

        // Then
        verify(imageService).upload("images/food/Image_2.jpg");
        verify(imageService).upload("images/food/Image_9.jpg");
    }

    @Test
    void process_should_return_product_sku_with_image_urls() throws Exception {
        // When
        var result = processor.process(csvDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImages()).hasSize(2);
        assertThat(result.getImages()).containsOnly("http://s3.url/uploaded-image.jpg");
    }

    @Test
    void process_should_handle_null_images_column() throws Exception {
        // Given
        csvDto.setImages(null);
        
        // When
        var result = processor.process(csvDto);

        // Then
        assertThat(result.getImages()).isNotNull();
        assertThat(result.getImages()).isEmpty();
    }

    @Test
    void process_should_throw_exception_for_invalid_product_id() {
        // Given
        csvDto.setProduct_id(99L);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> processor.process(csvDto));
    }

    @Test
    void process_should_throw_exception_for_invalid_color_id() {
        // Given
        csvDto.setColor_id(99L);
        when(productColorRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> processor.process(csvDto));
    }
}