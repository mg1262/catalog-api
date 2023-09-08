package com.spscommerce.interview.api.controller;


import com.spscommerce.interview.api.dto.common.ApiResponse;
import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.dto.validation.OnCreateProduct;
import com.spscommerce.interview.api.dto.validation.OnUpdateProduct;
import com.spscommerce.interview.api.mapper.CreateUpdateProductResultMapper;
import com.spscommerce.interview.api.mapper.ProductMapper;
import com.spscommerce.interview.api.mapper.SearchResultMapper;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.service.ProductService;
import com.spscommerce.interview.util.RateLimiter;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final RateLimiter rateLimiter;
    private final CatalogValidator validator;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CreateUpdateProductResultMapper createUpdateProductResultMapper;
    private final SearchResultMapper searchResultMapper;

    private static final String SEARCH_FIELDS = "\nSearch Fields:\nid\nname\ndescription\nprice\nsub_product.id\nsub_product.name\nsub_product.description\nsub_product.price";


    @Validated(OnCreateProduct.class)
    @ApiOperation(value = "Create Product", notes = "Provides the ability to create a Product.  \nSub Products that do not exist will be created. \nExisting Sub Products details will not be updated but will be associated.")
    @PreAuthorize("hasAuthority('SCOPE_create:products')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createProduct(@RequestBody ProductDto productDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            this.validator.validateProducts(productDto.getSubProducts(), bindingResult);
            return createSuccessfulResponse(this.createUpdateProductResultMapper.mapToDto(this.productService.createProduct(productMapper.mapToDomain(productDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Read Product", notes = "Returns a Product")
    @PreAuthorize("hasAuthority('SCOPE_read:products')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse readProduct(@PathVariable("id") String productId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(productMapper.mapToDto(productService.readProduct(productId)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @Validated(OnUpdateProduct.class)
    @ApiOperation(value = "Update Product", notes = "Provides the ability to update an existing Product.  \nSub Products that do not exist will be created. \nExisting Sub Products details will not be updated but will be associated.")
    @PreAuthorize("hasAuthority('SCOPE_update:products')")
    @PutMapping(path = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateProduct(@PathVariable("id") String productId, @RequestBody ProductDto productDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            if (!productId.equals(productDto.getId())) {
                ErrorCodes.PRD_UPDATE_ID_MISMATCH.throwException();
            }
            this.validator.validateProducts(productDto.getSubProducts(), bindingResult);
            return createSuccessfulResponse(this.createUpdateProductResultMapper.mapToDto(productService.updateProduct(productMapper.mapToDomain(productDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Delete Product", notes = "Product will be deleted. Associated Products will be preserved.")
    @PreAuthorize("hasAuthority('SCOPE_delete:products')")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteProduct(@PathVariable("id") String productId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            productService.deleteProduct(productId);
            return createSuccessfulResponse(null, request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Search Product", notes = "Allows searching for Products using Apache lucene query syntax." + SEARCH_FIELDS)
    @PreAuthorize("hasAuthority('SCOPE_search:products')")
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse search(@RequestParam("query") String query, @RequestParam("sortColumn") String sortColumn,
                              @RequestParam("sortAscending") boolean sortAsc, @RequestParam("page") @Min(0) int page, @RequestParam("limit") @Min(1) @Max(25) int limit, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(this.searchResultMapper.mapToDto(this.productService.search(query, page, limit, sortColumn, sortAsc)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

}
