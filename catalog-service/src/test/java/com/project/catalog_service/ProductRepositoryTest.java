import com.project.catalog_service.repository.ProductRepository; // 리포지토리 패키지 경로에 맞게 수정
import com.project.catalog_service.model.*;
import com.project.catalog_service.repository.*;
import com.project.common.dto.*;
import com.project.common.util.FileUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // 실제 DB 사용 (선택 사항)
@ActiveProfiles("test") // application-test.yml 프로파일 사용
public class ProductRepositoryTest {
    
    @Mock private ProductRepository productRepository;
    @Mock private ProductSizeRepository productSizeRepository;
    @Mock private ProductSkuRepository productskuRepository;
    @Mock private ProductColorRepository productColorRepository;
    @Mock private ProductQARepository productQARepository;

    
    // 테스트를 위한 초기 데이터 설정
    // @BeforeEach 등 메소드를 사용하여 테스트마다 초기화 가능
    // 또는 resources/data.sql 파일을 이용해 초기 데이터 세팅
    private Product product;
    private ProductDto productDto;
    private Category category;
    private Subcategory subcategory;

     @BeforeEach
     void setUp() {
        category = Category.builder().categoryId(1L).categoryName("fashion").build();
        subcategory = Subcategory.builder().subcategoryId(1L).subcategoryName("men's clothing").category(category).build();
        
        product = Product.builder()
                .productId(1L)
                .name("TestProduct")
                .description("Test Description")
                .brand("TestBrand")
                .slug("test-product")
                .category(category)
                .build();
                
        // ProductDto 변환 로직에 필요한 관계 설정
        ProductSubcategory productSubcategory = ProductSubcategory.builder().product(product).subcategory(subcategory).build();
        Set<ProductSubcategory> subcategories = new HashSet<>();
        subcategories.add(productSubcategory);
        product.setSubcategories(subcategories);
        product.setDetails(Collections.emptySet());
        product.setQuestions(Collections.emptySet());
        product.setSkus(Collections.emptySet());

        productDto = Product.convertToDto(product);
    }

    @Test
    void findProductBySearchParams_상품ID_목록_검색_테스트() {
        // Given: 테스트에 필요한 데이터 준비
        // 예를 들어, Product 엔티티를 생성하고 저장
        // Product product1 = new Product(...);
        // Product product2 = new Product(...);
        // productRepository.saveAll(Arrays.asList(product1, product2));

        // 검색 파라미터
        String name = null;
        Long categoryId = null;
        String style = null;
        String brand = null;
        String material = null;
        String gender = null;
        Float rating = null;
        List<Long> productIds = Arrays.asList(1L, 2L); // 검색할 상품 ID 목록

        // When: findProductBySearchParams 메서드 호출
        List<Product> products = productRepository.findProductBySearchParams(
            name, categoryId, style, brand, material, gender, rating, productIds
        );

        // Then: 결과 검증
        assertThat(products).isNotNull();
        assertEquals(2, products.size());
        // 추가적인 상세 검증: 예) products.stream().map(Product::getProductId).contains(1L, 2L)
    }

    @Test
    void findProductBySearchParams_이름과_평점_검색_테스트() {
        // Given: 테스트 데이터 준비
        // ... (필요하다면 데이터 저장)

        // 검색 파라미터
        String name = "Laptop";
        Long categoryId = null;
        String style = null;
        String brand = null;
        String material = null;
        String gender = null;
        Float rating = 4.0f;
        List<Long> productIds = null; // productIds가 NULL인 경우

        // When: findProductBySearchParams 메서드 호출
        List<Product> products = productRepository.findProductBySearchParams(
            name, categoryId, style, brand, material, gender, rating, productIds
        );

        // Then: 결과 검증
        // 예를 들어, name에 "Laptop"이 포함되고 rating이 4.0 이상인 상품이
        // 1개 있다고 가정
        assertThat(products).isNotNull();
        assertEquals(1, products.size());
    }
    
    // countProductsBySearchParams 메서드에 대한 테스트도 유사하게 작성
    @Test
    void countProductsBySearchParams_총_개수_검색_테스트() {
        // Given: 테스트 데이터 준비
        // ...

        // 검색 파라미터
        String name = "Phone";
        Long categoryId = 1L;
        String style = null;
        String brand = null;
        String material = null;
        String gender = null;
        Float rating = null;
        List<Long> productIds = null;

        // When: countProductsBySearchParams 메서드 호출
        int count = productRepository.countProductsBySearchParams(
            name, categoryId, style, brand, material, gender, rating, productIds
        );

        // Then: 결과 검증
        // 예시: "Phone" 이름이 포함되고 categoryId가 1인 상품이 5개 있다고 가정
        assertEquals(5, count);
    }
}