package com.project.catalog_service.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "product_qa", createIndex = true)
@Setting(settingPath = "/es/es-settings.json")
public class ProductQADocument {

    @Id
    private String qaId;

    @Field(type = FieldType.Keyword)
    private String productId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "custom_korean_analyzer"),
        otherFields = {
            @InnerField(suffix = "english", type = FieldType.Text, analyzer = "standard"),
            @InnerField(suffix = "japanese", type = FieldType.Text, analyzer = "custom_japanese_analyzer")
        }
    )
    private String question;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "custom_korean_analyzer"),
        otherFields = {
            @InnerField(suffix = "english", type = FieldType.Text, analyzer = "standard"),
            @InnerField(suffix = "japanese", type = FieldType.Text, analyzer = "custom_japanese_analyzer")
        }
    )
    private String answer;

    @Field(type = FieldType.Date)
    private String createdAt;
}