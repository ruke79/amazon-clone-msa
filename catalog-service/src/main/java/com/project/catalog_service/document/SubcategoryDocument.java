package com.project.catalog_service.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "subcategories", createIndex = true)
@Setting(settingPath = "/es/es-settings.json")
public class SubcategoryDocument {

    @Id
    private String subcategoryId;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "custom_korean_analyzer"),
        otherFields = {
            @InnerField(suffix = "english", type = FieldType.Text, analyzer = "standard"),
            @InnerField(suffix = "japanese", type = FieldType.Text, analyzer = "custom_japanese_analyzer")
        }
    )
    private String subcategoryName;

    @Field(type = FieldType.Keyword)
    private String slug;
}