package com.spscommerce.interview.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultDto<T> extends DtoObject {

    private List<T> results;
    private int page;
    private int limit;
    private int totalResults;
    private int totalPages;

}
