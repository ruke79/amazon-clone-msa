package com.project.catalog_service.batch.config;

import com.project.catalog_service.document.*;
import com.project.catalog_service.model.Category;
import com.project.catalog_service.model.Product;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.ProductSku;
import com.project.catalog_service.model.ProductSize;
import com.project.catalog_service.model.Subcategory;
import com.project.catalog_service.repository.*;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;

@Configuration
public class ElasticsearchBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductDocumentRepository productDocumentRepository;
    private final CategoryDocumentRepository categoryDocumentRepository;
    private final ProductColorDocumentRepository productColorDocumentRepository;
    private final ProductDetailsDocumentRepository productDetailsDocumentRepository;
    private final ProductQADocumentRepository productQADocumentRepository;
    private final ProductSkuDocumentRepository productSkuDocumentRepository;
    private final ProductSizeDocumentRepository productSizeDocumentRepository;
    private final SubcategoryDocumentRepository subcategoryDocumentRepository;

    @Value("${chunk.size:1000}")
    private int CHUNK_SIZE;

    @Value("${elasticsearch.alias.name.product:product-alias}")
    private String productAliasName;
    @Value("${elasticsearch.alias.name.category:category-alias}")
    private String categoryAliasName;
    @Value("${elasticsearch.alias.name.product-color:product-color-alias}")
    private String productColorAliasName;
    @Value("#{jobParameters['newIndexName']}")
    private String newIndexName;

    public ElasticsearchBatchConfig(JobRepository jobRepository,
                                    @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager,
                                    @Qualifier("metaTransactionManager") PlatformTransactionManager metaTransactionManager,
                                    EntityManagerFactory entityManagerFactory,
                                    ElasticsearchOperations elasticsearchOperations,
                                    ProductDocumentRepository productDocumentRepository,
                                    CategoryDocumentRepository categoryDocumentRepository,
                                    ProductColorDocumentRepository productColorDocumentRepository,
                                    ProductDetailsDocumentRepository productDetailsDocumentRepository,
                                    ProductQADocumentRepository productQADocumentRepository,
                                    ProductSkuDocumentRepository productSkuDocumentRepository,
                                    ProductSizeDocumentRepository productSizeDocumentRepository,
                                    SubcategoryDocumentRepository subcategoryDocumentRepository) {
        this.jobRepository = jobRepository;
        this.batchTransactionManager = batchTransactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.elasticsearchOperations = elasticsearchOperations;
        this.productDocumentRepository = productDocumentRepository;
        this.categoryDocumentRepository = categoryDocumentRepository;
        this.productColorDocumentRepository = productColorDocumentRepository;
        this.productDetailsDocumentRepository = productDetailsDocumentRepository;
        this.productQADocumentRepository = productQADocumentRepository;
        this.productSkuDocumentRepository = productSkuDocumentRepository;
        this.productSizeDocumentRepository = productSizeDocumentRepository;
        this.subcategoryDocumentRepository = subcategoryDocumentRepository;
    }

    // --- Readers ---
    @Bean
    public JpaPagingItemReader<Product> productReader() {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.subcategories LEFT JOIN FETCH p.details LEFT JOIN FETCH p.questions LEFT JOIN FETCH p.skus")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Category> categoryReader() {
        return new JpaPagingItemReaderBuilder<Category>()
                .name("categoryReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Category c")
                .pageSize(CHUNK_SIZE)
                .build();
    }
    
    @Bean
    public JpaPagingItemReader<ProductColor> productColorReader() {
        return new JpaPagingItemReaderBuilder<ProductColor>()
                .name("productColorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT pc FROM ProductColor pc")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ProductDetails> productDetailsReader() {
        return new JpaPagingItemReaderBuilder<ProductDetails>()
                .name("productDetailsReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT pd FROM ProductDetails pd")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ProductQA> productQAReader() {
        return new JpaPagingItemReaderBuilder<ProductQA>()
                .name("productQAReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT qa FROM ProductQA qa")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ProductSku> productSkuReader() {
        return new JpaPagingItemReaderBuilder<ProductSku>()
                .name("productSkuReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT ps FROM ProductSku ps")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ProductSize> productSizeReader() {
        return new JpaPagingItemReaderBuilder<ProductSize>()
                .name("productSizeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT ps FROM ProductSize ps")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Subcategory> subcategoryReader() {
        return new JpaPagingItemReaderBuilder<Subcategory>()
                .name("subcategoryReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT s FROM Subcategory s")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    // --- Processors ---
    @Bean
    public ItemProcessor<Product, ProductDocument> productProcessor() {
        return product -> {
            List<ProductDocument.ProductSubcategoryDocument> subcategoryDocuments = product.getSubcategories().stream()
                    .map(sub -> ProductDocument.ProductSubcategoryDocument.builder()
                            .id(sub.getSubcategory().getSubcategoryId().toString())
                            .name(sub.getSubcategory().getSubcategoryName())
                            .build())
                    .collect(Collectors.toList());
            List<ProductDocument.ProductSkuDocument> skuDocuments = product.getSkus().stream()
                    .map(sku -> ProductDocument.ProductSkuDocument.builder()
                            .sku(sku.getSku())
                            .color(sku.getColor() != null ? sku.getColor().getColor() : null)
                            .price(sku.getSizes() != null && !sku.getSizes().isEmpty() ? sku.getSizes().get(0).getPrice().floatValue() : 0.0f)
                            .discount(sku.getDiscount())
                            .build())
                    .collect(Collectors.toList());
            return ProductDocument.builder()
                    .productId(product.getProductId().toString())
                    .name(product.getName())
                    .description(product.getDescription())
                    .brand(product.getBrand())
                    .category(product.getCategory().getCategoryName())
                    .subcategories(subcategoryDocuments)
                    .skus(skuDocuments)
                    .rating(product.getRating())
                    .shipping(product.getShipping().floatValue())
                    .createdAt(product.getCreatedAt())
                    .build();
        };
    }

    @Bean
    public ItemProcessor<Category, CategoryDocument> categoryProcessor() {
        return category -> CategoryDocument.builder()
                .categoryId(category.getCategoryId().toString())
                .categoryName(category.getCategoryName())
                .slug(category.getSlug())
                .createdAt(category.getCreatedAt().toString())
                .build();
    }

    @Bean
    public ItemProcessor<ProductColor, ProductColorDocument> productColorProcessor() {
        return color -> ProductColorDocument.builder()
                .colorId(color.getColorId().toString())
                .color(color.getColor())
                .build();
    }

    @Bean
    public ItemProcessor<ProductDetails, ProductDetailsDocument> productDetailsProcessor() {
        return details -> ProductDetailsDocument.builder()
                .pdetailId(details.getPdetailId().toString())
                .productId(details.getProduct().getProductId().toString())
                .name(details.getName())
                .value(details.getValue())
                .build();
    }

    @Bean
    public ItemProcessor<ProductQA, ProductQADocument> productQAProcessor() {
        return qa -> ProductQADocument.builder()
                .qaId(qa.getQaId().toString())
                .productId(qa.getProduct().getProductId().toString())
                .question(qa.getQuestion())
                .answer(qa.getAnswer())
                .createdAt(qa.getCreatedAt().toString())
                .build();
    }

    @Bean
    public ItemProcessor<ProductSku, ProductSkuDocument> productSkuProcessor() {
        return sku -> ProductSkuDocument.builder()
                .skuproductId(sku.getSkuproductId().toString())
                .productId(sku.getProduct().getProductId().toString())
                .sku(sku.getSku())
                .discount(sku.getDiscount())
                .sold(sku.getSold())
                .build();
    }

    @Bean
    public ItemProcessor<ProductSize, ProductSizeDocument> productSizeProcessor() {
        return size -> ProductSizeDocument.builder()
                .sizeId(size.getSizeId().toString())
                .skuproductId(size.getSku().getSkuproductId().toString())
                .size(size.getSize())
                .quantity(size.getQuantity())
                .price(size.getPrice().floatValue())
                .build();
    }

    @Bean
    public ItemProcessor<Subcategory, SubcategoryDocument> subcategoryProcessor() {
        return subcategory -> SubcategoryDocument.builder()
                .subcategoryId(subcategory.getSubcategoryId().toString())
                .categoryId(subcategory.getCategory().getCategoryId().toString())
                .subcategoryName(subcategory.getSubcategoryName())
                .slug(subcategory.getSlug())
                .build();
    }

    // --- Writers ---
    @Bean
    public ItemWriter<CategoryDocument> categoryWriter() {
        String indexName = newIndexName != null ? newIndexName + "-categories" : "categories";
        return new RepositoryItemWriterBuilder<CategoryDocument>()
                .repository(categoryDocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductColorDocument> productColorWriter() {
        String indexName = newIndexName != null ? newIndexName + "-product-colors" : "product-colors";
        return new RepositoryItemWriterBuilder<ProductColorDocument>()
                .repository(productColorDocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductDetailsDocument> productDetailsWriter() {
        String indexName = newIndexName != null ? newIndexName + "-product-details" : "product-details";
        return new RepositoryItemWriterBuilder<ProductDetailsDocument>()
                .repository(productDetailsDocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductQADocument> productQAWriter() {
        String indexName = newIndexName != null ? newIndexName + "-product-qa" : "product-qa";
        return new RepositoryItemWriterBuilder<ProductQADocument>()
                .repository(productQADocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductDocument> productWriter() {
        String indexName = newIndexName != null ? newIndexName + "-products" : "products";
        return new RepositoryItemWriterBuilder<ProductDocument>()
                .repository(productDocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductSkuDocument> productSkuWriter() {
        String indexName = newIndexName != null ? newIndexName + "-product-skus" : "product-skus";
        return new RepositoryItemWriterBuilder<ProductSkuDocument>()
                .repository(productSkuDocumentRepository)
                .methodName("save")
                .build();
    }
    
    @Bean
    public ItemWriter<ProductSizeDocument> productSizeWriter() {
        String indexName = newIndexName != null ? newIndexName + "-product-sizes" : "product-sizes";
        return new RepositoryItemWriterBuilder<ProductSizeDocument>()
                .repository(productSizeDocumentRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter<SubcategoryDocument> subcategoryWriter() {
        String indexName = newIndexName != null ? newIndexName + "-subcategories" : "subcategories";
        return new RepositoryItemWriterBuilder<SubcategoryDocument>()
                .repository(subcategoryDocumentRepository)
                .methodName("save")
                .build();
    }
    
    // --- Roll-over Tasklet ---
    @Bean
    public Tasklet aliasRollOverTasklet() {
        return (contribution, chunkContext) -> {
            String newIndex = chunkContext.getStepContext().getJobParameters().get("newIndexName").toString();

            List<String> existingIndices = elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).getAliases().keySet().stream()
                .filter(index -> !index.equals(newIndex))
                .collect(Collectors.toList());

            // AliasActionParameters의 withIndices 메서드는 가변인자를 받습니다.
            // The withIndices method of AliasActionParameters accepts a variable number of arguments.
            AliasAction addAction = new AliasAction.Add(
                AliasActionParameters.builder()
                    .withIndices(newIndex)
                    .withAliases(productAliasName)
                    .build()
            );

            if (!existingIndices.isEmpty()) {
                AliasAction removeAction = new AliasAction.Remove(
                    AliasActionParameters.builder()
                        .withIndices(existingIndices.get(0))
                        .withAliases(productAliasName)
                        .build()
                );
                
                AliasActions aliasActions = new AliasActions(addAction, removeAction);
                elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).alias(aliasActions);
                
                elasticsearchOperations.indexOps(IndexCoordinates.of(existingIndices.get(0))).delete();
            } else {
                AliasActions aliasActions = new AliasActions(addAction);
                elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).alias(aliasActions);
            }

            return RepeatStatus.FINISHED;
        };
    }
    
    // --- Steps ---
    @Bean
    public Step aliasRollOverStep() {
        return new StepBuilder("aliasRollOverStep", jobRepository)
                .tasklet(aliasRollOverTasklet(), batchTransactionManager)
                .build();
    }

    @Bean
    public Step categoryIndexingStep() {
        return new StepBuilder("categoryIndexingStep", jobRepository)
                .<Category, CategoryDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(categoryReader())
                .processor(categoryProcessor())
                .writer(categoryWriter())
                .build();
    }

    @Bean
    public Step productColorIndexingStep() {
        return new StepBuilder("productColorIndexingStep", jobRepository)
                .<ProductColor, ProductColorDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productColorReader())
                .processor(productColorProcessor())
                .writer(productColorWriter())
                .build();
    }

    @Bean
    public Step productDetailsIndexingStep() {
        return new StepBuilder("productDetailsIndexingStep", jobRepository)
                .<ProductDetails, ProductDetailsDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productDetailsReader())
                .processor(productDetailsProcessor())
                .writer(productDetailsWriter())
                .build();
    }

    @Bean
    public Step productQAIndexingStep() {
        return new StepBuilder("productQAIndexingStep", jobRepository)
                .<ProductQA, ProductQADocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productQAReader())
                .processor(productQAProcessor())
                .writer(productQAWriter())
                .build();
    }

    @Bean
    public Step productIndexingStep() {
        return new StepBuilder("productIndexingStep", jobRepository)
                .<Product, ProductDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productReader())
                .processor(productProcessor())
                .writer(productWriter())
                .build();
    }

    @Bean
    public Step productSkuIndexingStep() {
        return new StepBuilder("productSkuIndexingStep", jobRepository)
                .<ProductSku, ProductSkuDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productSkuReader())
                .processor(productSkuProcessor())
                .writer(productSkuWriter())
                .build();
    }

    @Bean
    public Step productSizeIndexingStep() {
        return new StepBuilder("productSizeIndexingStep", jobRepository)
                .<ProductSize, ProductSizeDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(productSizeReader())
                .processor(productSizeProcessor())
                .writer(productSizeWriter())
                .build();
    }

    @Bean
    public Step subcategoryIndexingStep() {
        return new StepBuilder("subcategoryIndexingStep", jobRepository)
                .<Subcategory, SubcategoryDocument>chunk(CHUNK_SIZE, batchTransactionManager)
                .reader(subcategoryReader())
                .processor(subcategoryProcessor())
                .writer(subcategoryWriter())
                .build();
    }
    
    // --- Job ---
    @Bean
    public Job indexingJob() {
        return new JobBuilder("indexingJob", jobRepository)
                .start(categoryIndexingStep())
                .next(productColorIndexingStep())
                .next(productDetailsIndexingStep())
                .next(productQAIndexingStep())
                .next(productIndexingStep())
                .next(productSkuIndexingStep())
                .next(productSizeIndexingStep())
                .next(subcategoryIndexingStep())
                .next(aliasRollOverStep())
                .build();
    }
}
