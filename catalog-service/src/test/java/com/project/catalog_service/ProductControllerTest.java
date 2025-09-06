package com.project.catalog_service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.catalog_service.controller.ProductController;
import com.project.catalog_service.service.ProductService;
import com.project.common.dto.ProductColorDto; // 이 import 추가
import com.project.common.dto.ProductDto;
import com.project.common.dto.ProductInfoDto; // 이 import 추가
import com.project.common.response.MessageResponse; // 이 import 추가
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

// 아래의 Mockito static import를 추가하세요.
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong; // 이 import 추가
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// 아래의 MockMvcRequestBuilders static import를 추가하세요.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put; // 이 import 추가

// 아래의 MockMvcResultMatchers static import를 추가하세요.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 컨트롤러가 의존하는 서비스를 가짜(Mock) 객체로 주입합니다.
    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

      @Test
    @DisplayName("전체 상품 목록 조회 API는 200 OK를 반환해야 한다")
    void getProducts_shouldReturn200Ok() throws Exception {
        // given
        when(productService.warmUpProductCaches()).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/product/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("페이지네이션 API는 200 OK와 상품 목록을 반환해야 한다")
    void getProductsWithPagination_shouldReturnProducts() throws Exception {
        // given
        ProductDto mockProduct = ProductDto.builder().id("1").name("Test Product").build();
        when(productService.getProductsByCategory(anyString(), anyLong(), anyInt()))
                .thenReturn(Collections.singletonList(mockProduct));

        // when & then
        mockMvc.perform(get("/api/product/products/page")
                .param("category", "test")
                .param("cursorId", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(mockProduct))));
    }

    @Test
    @DisplayName("productId로 상품 조회 API는 200 OK를 반환해야 한다")
    void getProductById_shouldReturnProductDto() throws Exception {
        // given
        ProductDto mockProduct = ProductDto.builder().id("1").name("Test Product").build();
        when(productService.getProductById(anyLong())).thenReturn(mockProduct);

        // when & then
        mockMvc.perform(get("/api/{productId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockProduct)));
    }
    
    @Test
    @DisplayName("유효하지 않은 productId에 대해 400 Bad Request를 반환해야 한다")
    void getProductById_shouldReturn400_whenIdIsInvalid() throws Exception {
        // given
        when(productService.getProductById(anyLong())).thenThrow(new RuntimeException("Product not found"));
        
        // when & then
        mockMvc.perform(get("/api/{productId}", 999))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new MessageResponse("Product not found"))));
    }

    @Test
    @DisplayName("slug로 상품 조회 API는 200 OK를 반환해야 한다")
    void getProductInfo_shouldReturnProductInfo() throws Exception {
        // given
        ProductDto mockProduct = ProductDto.builder().slug("test-slug").build();
        when(productService.getProductsBySlug(anyString())).thenReturn(Collections.singletonList(mockProduct));

        // when & then
        mockMvc.perform(get("/api/product/{slug}", "test-slug"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockProduct)));
    }

    @Test
    @DisplayName("cart 상품 정보 API는 200 OK를 반환해야 한다")
    void getCartProductInfo_shouldReturn200Ok() throws Exception {
        // given
        ProductInfoDto mockDto = ProductInfoDto.builder().id("1").build();
        when(productService.getCartProductInfo(anyLong(), anyInt(), anyInt())).thenReturn(mockDto);

        // when & then
        mockMvc.perform(get("/api/cart/{product_id}", 1)
                .param("style", "0")
                .param("size", "0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("색상 정보 API는 200 OK를 반환해야 한다")
    void getColorInfo_shouldReturn200Ok() throws Exception {
        // given
        ProductColorDto mockDto = ProductColorDto.builder().id("1").build();
        when(productService.getColorAttributeInfo(anyLong())).thenReturn(mockDto);

        // when & then
        mockMvc.perform(get("/api/color/{colorId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("별점 업데이트 API는 성공적으로 호출되어야 한다")
    void updateRating_shouldReturn200Ok() throws Exception {
        // given
        // when & then
        mockMvc.perform(put("/api/product/rating")
                .param("id", "1")
                .param("rating", "4.5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리/서브카테고리 API 호출 시 200 OK와 상품 목록을 반환해야 한다")
    void getProductsBySubcategory_withValidParameters_shouldReturnProducts() throws Exception {
        // given (가정)
        String categoryName = "testCategory";
        String subcategoryName = "testSubcategory";
        int page = 0;

        // 반환할 가짜 데이터 생성
        ProductDto mockProductDto = ProductDto.builder().name("Test Product").build();
        List<ProductDto> mockProducts = Collections.singletonList(mockProductDto);
        
        // productService의 특정 메소드 호출 시, 가짜 데이터를 반환하도록 설정합니다.
        when(productService.getProductsByCategoryAndSubcategory(anyString(), anyString(), anyInt()))
            .thenReturn(mockProducts);

        // when & then (실행 & 검증)
        mockMvc.perform(get("/api/product/products/by-subcategory")
                .param("category", categoryName)
                .param("subcategory", subcategoryName)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 상태 코드가 200 OK인지 확인
                .andExpect(content().json(objectMapper.writeValueAsString(mockProducts))); // 반환된 JSON이 예상 데이터와 일치하는지 확인
    }
}