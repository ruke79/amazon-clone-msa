package com.project.common.message.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;



@Getter
@Setter
@NoArgsConstructor
public class CartRollbackRequest implements Serializable {

    Long userId;
    int qty;
}
