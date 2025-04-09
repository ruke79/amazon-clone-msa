package com.project.common.dto;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSkuDto {

    private String id;

    private String sku;

    private List<String> images;
    
    //private List<String> descriptionImages;

    private int discount;

    private int sold;

    private Set<ProductSizeDto> sizes;
    private ProductColorDto color;

    public boolean hasSize(String size) {
        for (Iterator<ProductSizeDto> it = sizes.iterator(); it.hasNext(); ) {
            ProductSizeDto f = it.next();
            if (f.getSize() == size) {
                return true;
            }            
        }
        return false;
    }

    public ProductSizeDto getSize(String size) {
        for (Iterator<ProductSizeDto> it = sizes.iterator(); it.hasNext(); ) {
            ProductSizeDto f = it.next();
            if (f.getSize() == size) {
                return f;
            }            
        }
        return null;
    }

    public boolean hasColor(String colorId) {
        return color.getId() == colorId;
    }

}
