package com.woowahan.recipe.domain.dto.sellerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerDeleteResponse {
    private String sellerName;
    private String message;
}
