package com.spscommerce.interview.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult<T> {

    private List<T> results;
    private int page;
    private int limit;
    private int totalResults;
    private int totalPages;

}
