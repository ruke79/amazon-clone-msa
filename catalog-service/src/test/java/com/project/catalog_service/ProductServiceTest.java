import com.project.catalog_service.model.*;
import com.project.catalog_service.repository.*;
import com.project.catalog_service.service.ImageService;
import com.project.catalog_service.service.ProductService;
import com.project.common.dto.*;
import com.project.common.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private SubcategoryRepository subCategoryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductSizeRepository productSizeRepository;
    @Mock private ProductSkuRepository productskuRepository;
    @Mock private ProductColorRepository productColorRepository;
    @Mock private ProductQARepository productQARepository;
    @Mock private ImageService imageService;
    @Mock private FileUtil fileUtil;

    @InjectMocks
    private ProductService productService;

    // 테스트에 사용할 공통 객체
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
    @DisplayName("warmUpProductCaches는 모든 카테고리/서브카테고리 조합에 대해 캐시를 워밍업해야 한다")
    void warmUpProductCaches_shouldCallServiceForEachPage() {
        // given

        
        Category category2 = Category.builder().categoryId(2L).categoryName("consumer electronics").build();
        Subcategory subcategory2 = Subcategory.builder().subcategoryId(2L).subcategoryName("smartphones").category(category2).build();
        
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));
        when(subCategoryRepository.findByCategory(category)).thenReturn(Collections.singletonList(subcategory));
        when(subCategoryRepository.findByCategory(category2)).thenReturn(Collections.singletonList(subcategory2));

        // getProductsByCategoryAndSubcategory 메소드의 동작을 모의(mock)합니다.
        when(productService.getProductsByCategoryAndSubcategory(anyString(), anyString(), anyInt()))
            .thenReturn(Collections.nCopies(10, productDto))
            .thenReturn(Collections.nCopies(5, productDto)) // 두 번째 호출 시 5개 반환
            .thenReturn(Collections.emptyList()) // 세 번째 호출 시 빈 리스트 반환
            .thenReturn(Collections.emptyList());

        // when
        productService.warmUpProductCaches();

        // then (캐싱 로직이 제대로 호출되었는지 확인)
        verify(productService, times(2)).getProductsByCategoryAndSubcategory(eq(category.getCategoryName()), eq(subcategory.getSubcategoryName()), anyInt());
        verify(productService, times(1)).getProductsByCategoryAndSubcategory(eq(category2.getCategoryName()), eq(subcategory2.getSubcategoryName()), anyInt());
    }

    @Test
    @DisplayName("getProductsByCategoryAndSubcategory는 페이지네이션된 결과를 반환해야 한다")
    void getProductsByCategoryAndSubcategory_shouldReturnPaginatedResults() {
        // given
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAllByCategory_CategoryNameAndSubcategories_Subcategory_SubcategoryName(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(productPage);

        // when
        List<ProductDto> result = productService.getProductsByCategoryAndSubcategory("TestCategory", "TestSubcategory", 0);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestProduct", result.get(0).getName());
        verify(productRepository, times(1)).findAllByCategory_CategoryNameAndSubcategories_Subcategory_SubcategoryName(anyString(), anyString(), any(PageRequest.class));
    }

    @Test
    @DisplayName("getProductById는 유효한 ID에 대해 ProductDto를 반환해야 한다")
    void getProductById_shouldReturnProductDto_whenProductExists() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        
        // when
        ProductDto result = productService.getProductById(1L);
        
        // then
        assertNotNull(result);
        assertEquals("TestProduct", result.getName());
    }

    @Test
    @DisplayName("getProductById는 존재하지 않는 ID에 대해 null을 반환해야 한다")
    void getProductById_shouldReturnNull_whenProductDoesNotExist() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        ProductDto result = productService.getProductById(999L);
        
        // then
        assertNull(result);
    }
    
    @Test
    @DisplayName("updateRating는 별점 업데이트 후 캐시를 갱신해야 한다")
    void updateRating_shouldUpdateAndRefreshCache() {
        // given
        Product updatedProduct = Product.builder().productId(1L).rating(4.5F).name("UpdatedProduct").build();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(updatedProduct));
        
        // when
        ProductDto result = productService.updateRating(1L, 4.5F);

        // then
        verify(productRepository, times(1)).updateRating(1L, 4.5F);
        verify(productRepository, times(1)).findById(1L);
        assertNotNull(result);
        assertEquals(4.5F, result.getRating());
    }

    @Test
    @DisplayName("getCartProductInfo는 유효한 매개변수에 대해 ProductInfoDto를 반환해야 한다")
    void getCartProductInfo_shouldReturnProductInfoDto() {
        // given
        ProductSku sku = ProductSku.builder().skuproductId(1L).product(product).build();
        ProductSize size = ProductSize.builder().sizeId(1L).size("M").price(new BigDecimal("100.00")).quantity(10).sku(sku).build();
        ProductColor color = ProductColor.builder().colorId(1L).color("Red").colorImage("red.jpg").build();
        
        Set<ProductSku> skus = new HashSet<>();
        sku.setSizes(Collections.singletonList(size));
        sku.setColor(color);
        skus.add(sku);
        product.setSkus(skus);
        
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // when
        ProductInfoDto dto = productService.getCartProductInfo(1L, 0, 0);

        // then
        assertNotNull(dto);
        assertEquals("TestProduct", dto.getName());
        assertEquals(new BigDecimal("100.00").toPlainString(), dto.getPrice());
    }

    @Test
    @DisplayName("getCartProductInfo는 존재하지 않는 ID에 대해 null을 반환해야 한다")
    void getCartProductInfo_shouldReturnNull_whenProductDoesNotExist() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // when
        ProductInfoDto dto = productService.getCartProductInfo(999L, 0, 0);
        
        // then
        assertNull(dto);
    }
}