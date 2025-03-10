package com.project.common.message.dto.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartEmptyRequest implements Serializable {

    Long userId;
    boolean emptyCart;
}
