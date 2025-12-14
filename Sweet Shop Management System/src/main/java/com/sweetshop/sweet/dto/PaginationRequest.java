package com.sweetshop.sweet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    
    public boolean hasPagination() {
        return page != null || size != null;
    }
}

