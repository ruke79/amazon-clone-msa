package com.project.common.message.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductUpdateRequest implements Serializable  {

    List<OrderProductRequest> orderProducts;
}
