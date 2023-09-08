package com.spscommerce.interview.api.controller;

import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.mapper.*;
import com.spscommerce.interview.service.OrganizationImportService;
import com.spscommerce.interview.service.OrganizationService;
import com.spscommerce.interview.service.ProductService;
import com.spscommerce.interview.util.RateLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private CatalogValidator validator;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductMapper productMapper;
    @MockBean
    private CreateUpdateProductResultMapper createUpdateProductResultMapper;
    @MockBean
    private SearchResultMapper searchResultMapper;

    @Test
    void createProduct() {
    }

    @Test
    void readProduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void search() {
    }
}