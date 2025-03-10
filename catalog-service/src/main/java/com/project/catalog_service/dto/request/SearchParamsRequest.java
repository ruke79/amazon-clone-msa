package com.project.catalog_service.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SearchParamsRequest implements Serializable{

    private String search;

    private String category;

    private String style;

    private String size;

    private String color;

    private String brand;

    private String material;

    private String gender;

    private BigDecimal lowPrice;

    private BigDecimal highPrice;

    private BigDecimal shipping;

    private float rating;

    private int page;

    private int pageSize;



}
