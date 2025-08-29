// package com.project.catalog_service.batch.config;

// import com.project.catalog_service.document.*;
// import com.project.catalog_service.model.Category;
// import com.project.catalog_service.model.Product;
// import com.project.catalog_service.model.ProductColor;
// import com.project.catalog_service.model.ProductDetails;
// import com.project.catalog_service.model.ProductQA;
// import com.project.catalog_service.model.ProductSku;
// import com.project.catalog_service.model.ProductSize;
// import com.project.catalog_service.model.Subcategory;
// import com.project.catalog_service.repository.*;
// import jakarta.persistence.EntityManagerFactory;
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.core.step.tasklet.Tasklet;
// import org.springframework.batch.item.ItemProcessor;
// import org.springframework.batch.item.ItemWriter;
// import org.springframework.batch.item.database.JpaPagingItemReader;
// import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
// import org.springframework.batch.repeat.RepeatStatus;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
// import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.core.task.TaskExecutor;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
// import org.springframework.batch.core.JobParameters;
// import org.springframework.batch.core.JobParameter;
// import org.springframework.batch.core.scope.context.StepContext;
// import org.springframework.batch.core.step.tasklet.TaskletStep;
// import org.springframework.batch.item.Chunk;
// import org.springframework.batch.core.StepExecutionListener;
// import org.springframework.batch.core.StepExecution;

// import org.springframework.batch.core.listener.StepExecutionListenerSupport;

// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
// import org.springframework.data.elasticsearch.core.index.AliasAction;
// import org.springframework.data.elasticsearch.core.index.AliasActions;
// import org.springframework.data.elasticsearch.core.index.AliasActionParameters;

// @Configuration
// public class ElasticsearchBatchConfig {

//     private final JobRepository jobRepository;
//     private final PlatformTransactionManager batchTransactionManager;
//     private final EntityManagerFactory entityManagerFactory;
//     private final ElasticsearchOperations elasticsearchOperations;
//     private final ProductDocumentRepository productDocumentRepository;
//     private final CategoryDocumentRepository categoryDocumentRepository;
//     private final ProductColorDocumentRepository productColorDocumentRepository;
//     private final ProductDetailsDocumentRepository productDetailsDocumentRepository;
//     private final ProductQADocumentRepository productQADocumentRepository;
//     private final ProductSkuDocumentRepository productSkuDocumentRepository;
//     private final ProductSizeDocumentRepository productSizeDocumentRepository;
//     private final SubcategoryDocumentRepository subcategoryDocumentRepository;

//     @Value("${chunk.size:1000}")
//     private int CHUNK_SIZE;

//     @Value("${elasticsearch.alias.name.product:product-alias}")
//     private String productAliasName;
//     @Value("${elasticsearch.alias.name.category:category-alias}")
//     private String categoryAliasName;
//     @Value("${elasticsearch.alias.name.product-color:product-color-alias}")
//     private String productColorAliasName;

//     public ElasticsearchBatchConfig(JobRepository jobRepository,
//                                     @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager,
//                                     @Qualifier("metaTransactionManager") PlatformTransactionManager metaTransactionManager,
//                                     EntityManagerFactory entityManagerFactory,
//                                     ElasticsearchOperations elasticsearchOperations,
//                                     ProductDocumentRepository productDocumentRepository,
//                                     CategoryDocumentRepository categoryDocumentRepository,
//                                     ProductColorDocumentRepository productColorDocumentRepository,
//                                     ProductDetailsDocumentRepository productDetailsDocumentRepository,
//                                     ProductQADocumentRepository productQADocumentRepository,
//                                     ProductSkuDocumentRepository productSkuDocumentRepository,
//                                     ProductSizeDocumentRepository productSizeDocumentRepository,
//                                     SubcategoryDocumentRepository subcategoryDocumentRepository) {
//         this.jobRepository = jobRepository;
//         this.batchTransactionManager = batchTransactionManager;
//         this.entityManagerFactory = entityManagerFactory;
//         this.elasticsearchOperations = elasticsearchOperations;
//         this.productDocumentRepository = productDocumentRepository;
//         this.categoryDocumentRepository = categoryDocumentRepository;
//         this.productColorDocumentRepository = productColorDocumentRepository;
//         this.productDetailsDocumentRepository = productDetailsDocumentRepository;
//         this.productQADocumentRepository = productQADocumentRepository;
//         this.productSkuDocumentRepository = productSkuDocumentRepository;
//         this.productSizeDocumentRepository = productSizeDocumentRepository;
//         this.subcategoryDocumentRepository = subcategoryDocumentRepository;
//     }

//     // --- TaskExecutor 빈 추가: 병렬 처리를 위한 스레드 풀 정의 ---
//     @Bean
//     public TaskExecutor taskExecutor() {
//         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//         executor.setCorePoolSize(5); // 기본 스레드 수
//         executor.setMaxPoolSize(10); // 최대 스레드 수
//         executor.setQueueCapacity(25); // 큐 사이즈
//         executor.setThreadNamePrefix("batch-thread-");
//         executor.initialize();
//         return executor;
//     }

//     // --- Readers (수정된 부분) ---
//     @Bean
//     public JpaPagingItemReader<Product> productReader() {
//         String queryString = "SELECT p FROM Product p " +
//                 "LEFT JOIN FETCH p.category " +
//                 "LEFT JOIN FETCH p.subcategories " +
//                 "LEFT JOIN FETCH p.details " +
//                 "LEFT JOIN FETCH p.questions " +
//                 "LEFT JOIN FETCH p.skus s " + // `p.skus`와 그 하위 `p.skus.sizes` 컬렉션도 패치해야 합니다.
//                 "LEFT JOIN FETCH s.sizes " + // `ProductSku` 내의 `sizes` 컬렉션 패치 추가
//                 "LEFT JOIN FETCH s.color "; // `ProductSku` 내의 `color` 컬렉션 패치 추가
        
//         return new JpaPagingItemReaderBuilder<Product>()
//                 .name("productReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString(queryString)
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<Category> categoryReader() {
//         return new JpaPagingItemReaderBuilder<Category>()
//                 .name("categoryReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT c FROM Category c")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }
    
//     @Bean
//     public JpaPagingItemReader<ProductColor> productColorReader() {
//         return new JpaPagingItemReaderBuilder<ProductColor>()
//                 .name("productColorReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT pc FROM ProductColor pc")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<ProductDetails> productDetailsReader() {
//         return new JpaPagingItemReaderBuilder<ProductDetails>()
//                 .name("productDetailsReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT pd FROM ProductDetails pd")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<ProductQA> productQAReader() {
//         return new JpaPagingItemReaderBuilder<ProductQA>()
//                 .name("productQAReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT qa FROM ProductQA qa")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<ProductSku> productSkuReader() {
//         return new JpaPagingItemReaderBuilder<ProductSku>()
//                 .name("productSkuReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT ps FROM ProductSku ps")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<ProductSize> productSizeReader() {
//         return new JpaPagingItemReaderBuilder<ProductSize>()
//                 .name("productSizeReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT ps FROM ProductSize ps")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     @Bean
//     public JpaPagingItemReader<Subcategory> subcategoryReader() {
//         return new JpaPagingItemReaderBuilder<Subcategory>()
//                 .name("subcategoryReader")
//                 .entityManagerFactory(entityManagerFactory)
//                 .queryString("SELECT s FROM Subcategory s")
//                 .pageSize(CHUNK_SIZE)
//                 .saveState(false)
//                 .build();
//     }

//     // --- Processors (수정된 부분) ---
//     @Bean
//     public ItemProcessor<Product, ProductDocument> productProcessor() {
//         return product -> {
//             List<ProductDocument.ProductSubcategoryDocument> subcategoryDocuments = product.getSubcategories().stream()
//                     .map(sub -> ProductDocument.ProductSubcategoryDocument.builder()
//                             .id(sub.getSubcategory().getSubcategoryId().toString())
//                             .name(sub.getSubcategory().getSubcategoryName())
//                             .build())
//                     .collect(Collectors.toList());
//             List<ProductDocument.ProductSkuDocument> skuDocuments = product.getSkus().stream()
//                     .map(sku -> ProductDocument.ProductSkuDocument.builder()
//                             .sku(sku.getSku())
//                             .color(sku.getColor() != null ? sku.getColor().getColor() : null)
//                             .price(sku.getSizes() != null && !sku.getSizes().isEmpty() ? sku.getSizes().get(0).getPrice().floatValue() : 0.0f)
//                             .discount(sku.getDiscount())
//                             .build())
//                     .collect(Collectors.toList());
//             return ProductDocument.builder()
//                     .productId(product.getProductId().toString())
//                     .name(product.getName())
//                     .description(product.getDescription())
//                     .brand(product.getBrand())
//                     .category(product.getCategory().getCategoryName())
//                     .subcategories(subcategoryDocuments)
//                     .skus(skuDocuments)
//                     .rating(product.getRating())
//                     .shipping(product.getShipping().floatValue())
//                     .createdAt(product.getCreatedAt())
//                     .build();
//         };
//     }

//     @Bean
//     public ItemProcessor<Category, CategoryDocument> categoryProcessor() {
//         return category -> CategoryDocument.builder()
//                 .categoryId(category.getCategoryId().toString())
//                 .categoryName(category.getCategoryName())
//                 .slug(category.getSlug())
//                 .createdAt(category.getCreatedAt().toString())
//                 .build();
//     }

//     @Bean
//     public ItemProcessor<ProductColor, ProductColorDocument> productColorProcessor() {
//         return color -> ProductColorDocument.builder()
//                 .colorId(color.getColorId().toString())
//                 .color(color.getColor())
//                 .build();
//     }

//     @Bean
//     public ItemProcessor<ProductDetails, ProductDetailsDocument> productDetailsProcessor() {
//         return details -> ProductDetailsDocument.builder()
//                 .pdetailId(details.getPdetailId().toString())
//                 .productId(details.getProduct().getProductId().toString())
//                 .name(details.getName())
//                 .value(details.getValue())
//                 .build();
//     }

//     @Bean
//     public ItemProcessor<ProductQA, ProductQADocument> productQAProcessor() {
//         return qa -> ProductQADocument.builder()
//                 .qaId(qa.getQaId().toString())
//                 .productId(qa.getProduct().getProductId().toString())
//                 .question(qa.getQuestion())
//                 .answer(qa.getAnswer())
//                 .createdAt(qa.getCreatedAt().toString())
//                 .build();
//     }

//     @Bean
//     public ItemProcessor<ProductSku, ProductSkuDocument> productSkuProcessor() {
//         return sku -> ProductSkuDocument.builder()
//                 .skuproductId(sku.getSkuproductId().toString())
//                 .productId(sku.getProduct().getProductId().toString())
//                 .sku(sku.getSku())
//                 .discount(sku.getDiscount())
//                 .sold(sku.getSold())
//                 .build();
//     };

//     @Bean
//     public ItemProcessor<ProductSize, ProductSizeDocument> productSizeProcessor() {
//         return size -> ProductSizeDocument.builder()
//                 .sizeId(size.getSizeId().toString())
//                 .skuproductId(size.getSku().getSkuproductId().toString())
//                 .size(size.getSize())
//                 .quantity(size.getQuantity())
//                 .price(size.getPrice().floatValue())
//                 .build();
//     };

//     @Bean
//     public ItemProcessor<Subcategory, SubcategoryDocument> subcategoryProcessor() {
//         return subcategory -> SubcategoryDocument.builder()
//                 .subcategoryId(subcategory.getSubcategoryId().toString())
//                 .categoryId(subcategory.getCategory().getCategoryId().toString())
//                 .subcategoryName(subcategory.getSubcategoryName())
//                 .slug(subcategory.getSlug())
//                 .build();
//     }

//     // --- Writers (StepExecutionListener를 사용하여 JobParameters를 주입받도록 수정) ---
//     // ItemWriter는 StepExecutionListener를 구현해야 StepExecution에 접근할 수 있습니다.
//     public class ProductWriter implements ItemWriter<ProductDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductDocument> chunk) {
//             String indexName = this.newIndexName + "-products";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class CategoryWriter implements ItemWriter<CategoryDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends CategoryDocument> chunk) {
//             String indexName = this.newIndexName + "-categories";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class ProductColorWriter implements ItemWriter<ProductColorDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductColorDocument> chunk) {
//             String indexName = this.newIndexName + "-product-colors";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class ProductDetailsWriter implements ItemWriter<ProductDetailsDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductDetailsDocument> chunk) {
//             String indexName = this.newIndexName + "-product-details";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class ProductQAWriter implements ItemWriter<ProductQADocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductQADocument> chunk) {
//             String indexName = this.newIndexName + "-product-qa";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class ProductSkuWriter implements ItemWriter<ProductSkuDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductSkuDocument> chunk) {
//             String indexName = this.newIndexName + "-product-skus";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class ProductSizeWriter implements ItemWriter<ProductSizeDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends ProductSizeDocument> chunk) {
//             String indexName = this.newIndexName + "-product-sizes";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     public class SubcategoryWriter implements ItemWriter<SubcategoryDocument>, StepExecutionListener {
//         private String newIndexName;

//         @Override
//         public void beforeStep(StepExecution stepExecution) {
//             this.newIndexName = stepExecution.getJobParameters().getString("newIndexName", "default");
//         }
        
//         @Override
//         public void write(Chunk<? extends SubcategoryDocument> chunk) {
//             String indexName = this.newIndexName + "-subcategories";
//             elasticsearchOperations.save(chunk.getItems(), IndexCoordinates.of(indexName));
//         }
//     }

//     // --- ItemWriter 빈을 생성하여 반환하도록 수정
//     @Bean
//     public ItemWriter<ProductDocument> productWriter() {
//         return new ProductWriter();
//     }
    
//     @Bean
//     public ItemWriter<CategoryDocument> categoryWriter() {
//         return new CategoryWriter();
//     }
    
//     @Bean
//     public ItemWriter<ProductColorDocument> productColorWriter() {
//         return new ProductColorWriter();
//     }
    
//     @Bean
//     public ItemWriter<ProductDetailsDocument> productDetailsWriter() {
//         return new ProductDetailsWriter();
//     }
    
//     @Bean
//     public ItemWriter<ProductQADocument> productQAWriter() {
//         return new ProductQAWriter();
//     }
    
//     @Bean
//     public ItemWriter<ProductSkuDocument> productSkuWriter() {
//         return new ProductSkuWriter();
//     }
    
//     @Bean
//     public ItemWriter<ProductSizeDocument> productSizeWriter() {
//         return new ProductSizeWriter();
//     }
    
//     @Bean
//     public ItemWriter<SubcategoryDocument> subcategoryWriter() {
//         return new SubcategoryWriter();
//     }
    
//     // --- Roll-over Tasklet ---
//     @Bean
//     public Tasklet aliasRollOverTasklet() {
//         return (contribution, chunkContext) -> {

//             Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//             Object newIndexObj = jobParameters.get("newIndexName");
//             final String newIndex = (newIndexObj != null) ? newIndexObj.toString() : "default-new-index"; // 기본값 설정

//             List<String> existingIndices = elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).getAliases().keySet().stream()
//                 .filter(index -> !index.equals(newIndex))
//                 .collect(Collectors.toList());

//             AliasAction addAction = new AliasAction.Add(
//                 AliasActionParameters.builder()
//                     .withIndices(newIndex)
//                     .withAliases(productAliasName)
//                     .build()
//             );

//             if (!existingIndices.isEmpty()) {
//                 AliasAction removeAction = new AliasAction.Remove(
//                     AliasActionParameters.builder()
//                         .withIndices(existingIndices.get(0))
//                         .withAliases(productAliasName)
//                         .build()
//                 );
                
//                 AliasActions aliasActions = new AliasActions(addAction, removeAction);
//                 elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).alias(aliasActions);
                
//                 elasticsearchOperations.indexOps(IndexCoordinates.of(existingIndices.get(0))).delete();
//             } else {
//                 AliasActions aliasActions = new AliasActions(addAction);
//                 elasticsearchOperations.indexOps(IndexCoordinates.of(productAliasName)).alias(aliasActions);
//             }

//             return RepeatStatus.FINISHED;
//         };
//     }
    
//     // --- Steps ---
//     @Bean
//     public Step aliasRollOverStep() {
//         return new StepBuilder("aliasRollOverStep", jobRepository)
//                 .tasklet(aliasRollOverTasklet(), batchTransactionManager)
//                 .build();
//     }

//     @Bean
//     public Step categoryIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("categoryIndexingStep", jobRepository)
//                 .<Category, CategoryDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(categoryReader())
//                 .processor(categoryProcessor())
//                 .writer(categoryWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productColorIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productColorIndexingStep", jobRepository)
//                 .<ProductColor, ProductColorDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productColorReader())
//                 .processor(productColorProcessor())
//                 .writer(productColorWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productDetailsIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productDetailsIndexingStep", jobRepository)
//                 .<ProductDetails, ProductDetailsDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productDetailsReader())
//                 .processor(productDetailsProcessor())
//                 .writer(productDetailsWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productQAIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productQAIndexingStep", jobRepository)
//                 .<ProductQA, ProductQADocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productQAReader())
//                 .processor(productQAProcessor())
//                 .writer(productQAWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productIndexingStep", jobRepository)
//                 .<Product, ProductDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productReader())
//                 .processor(productProcessor())
//                 .writer(productWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productSkuIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productSkuIndexingStep", jobRepository)
//                 .<ProductSku, ProductSkuDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productSkuReader())
//                 .processor(productSkuProcessor())
//                 .writer(productSkuWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step productSizeIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("productSizeIndexingStep", jobRepository)
//                 .<ProductSize, ProductSizeDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(productSizeReader())
//                 .processor(productSizeProcessor())
//                 .writer(productSizeWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     @Bean
//     public Step subcategoryIndexingStep(TaskExecutor taskExecutor) {
//         return new StepBuilder("subcategoryIndexingStep", jobRepository)
//                 .<Subcategory, SubcategoryDocument>chunk(CHUNK_SIZE, batchTransactionManager)
//                 .reader(subcategoryReader())
//                 .processor(subcategoryProcessor())
//                 .writer(subcategoryWriter())
//                 .taskExecutor(taskExecutor) // taskExecutor 추가
//                 .build();
//     }

//     // --- Job ---
//     @Bean
//     public Job indexingJob() {
//         return new JobBuilder("indexingJob", jobRepository)
//                 .start(categoryIndexingStep(taskExecutor()))
//                 .next(productColorIndexingStep(taskExecutor()))
//                 .next(productDetailsIndexingStep(taskExecutor()))
//                 .next(productQAIndexingStep(taskExecutor()))
//                 .next(productIndexingStep(taskExecutor()))
//                 .next(productSkuIndexingStep(taskExecutor()))
//                 .next(productSizeIndexingStep(taskExecutor()))
//                 .next(subcategoryIndexingStep(taskExecutor()))
//                 .next(aliasRollOverStep())
//                 .build();
//     }
// }