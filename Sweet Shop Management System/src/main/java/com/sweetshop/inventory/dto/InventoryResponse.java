package com.sweetshop.inventory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryResponse {
    private Long sweetId;
    private String sweetName;
    private Integer quantity;
}


