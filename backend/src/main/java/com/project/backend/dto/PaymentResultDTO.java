
package com.project.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaymentResultDTO {

    private String status;
    private String email;
}
