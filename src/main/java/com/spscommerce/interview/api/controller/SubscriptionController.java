package com.spscommerce.interview.api.controller;

import com.spscommerce.interview.api.dto.common.ApiResponse;
import com.spscommerce.interview.api.dto.subscription.SubscriptionDto;
import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.dto.validation.OnCreateSubscription;
import com.spscommerce.interview.api.dto.validation.OnUpdateSubscription;
import com.spscommerce.interview.api.mapper.CreateUpdateSubscriptionResultMapper;
import com.spscommerce.interview.api.mapper.SearchResultMapper;
import com.spscommerce.interview.api.mapper.SubscriptionImportResponseMapper;
import com.spscommerce.interview.api.mapper.SubscriptionMapper;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.service.SubscriptionImportService;
import com.spscommerce.interview.service.SubscriptionService;
import com.spscommerce.interview.util.ImportFileUtil;
import com.spscommerce.interview.util.RateLimiter;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.File;

@Validated
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController extends BaseController {

    private final RateLimiter rateLimiter;
    private final CatalogValidator validator;
    private final SubscriptionService subscriptionService;
    private final SubscriptionImportService subscriptionImportService;
    private final ImportFileUtil importFileUtil;
    private final SubscriptionMapper subscriptionMapper;
    private final CreateUpdateSubscriptionResultMapper createUpdateSubscriptionResultMapper;
    private final SubscriptionImportResponseMapper subscriptionImportResponseMapper;
    private final SearchResultMapper searchResultMapper;

    private static final String SEARCH_FIELDS = "\nSearch Fields:\nid\ndiscount\ntotalPrice\nproduct.id\nproduct.name\nproduct.description\nproduct.price\nproduct.sub_product.id\nproduct.sub_product.name\nproduct.sub_product.description\nproduct.sub_product.price";


    @Validated(OnCreateSubscription.class)
    @ApiOperation(value = "Create Subscription", notes = "Provides the ability to create a Subscription. \nOrganization ID is required. \nProducts associated with Subscription will be created if they don't exist.  \nExisting Products will not be updated but will be associated.")
    @PreAuthorize("hasAuthority('SCOPE_create:subscriptions')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createSubscription(@RequestBody @Valid SubscriptionDto subscriptionDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            this.validator.validateProducts(subscriptionDto.getProducts(), bindingResult);
            return createSuccessfulResponse(createUpdateSubscriptionResultMapper.mapToDto(subscriptionService.createSubscription(subscriptionMapper.mapToDomain(subscriptionDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @PreAuthorize("hasAuthority('SCOPE_read:subscriptions')")
    @ApiOperation(value = "Read Subscription", notes = "Returns a Subscription")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse readSubscription(@PathVariable("id") String subscriptionId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(subscriptionMapper.mapToDto(subscriptionService.readSubscription(subscriptionId)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @Validated(OnUpdateSubscription.class)
    @ApiOperation(value = "Update Subscription", notes = "Provides the ability to update an existing Subscription.  \nSubscriptions without IDs will be created.  \nProducts without IDs will be created.  \nExisting Products details will not be updated but associations will be updated.")
    @PreAuthorize("hasAuthority('SCOPE_update:subscriptions')")
    @PutMapping(path = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateSubscription(@PathVariable("id") String subscriptionId, @RequestBody @Valid SubscriptionDto subscriptionDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            this.validator.validateProducts(subscriptionDto.getProducts(), bindingResult);
            if (!subscriptionId.equals(subscriptionDto.getId())) {
                ErrorCodes.SUB_UPDATE_ID_MISMATCH.throwException();
            }
            return createSuccessfulResponse(createUpdateSubscriptionResultMapper.mapToDto(subscriptionService.updateSubscription(subscriptionMapper.mapToDomain(subscriptionDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Delete Subscription", notes = "Subscription will be deleted. Associated Products will be preserved.")
    @PreAuthorize("hasAuthority('SCOPE_delete:subscriptions')")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteSubscription(@PathVariable("id") String subscriptionId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            subscriptionService.deleteSubscription(subscriptionId);
            return createSuccessfulResponse(null, request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Search Subscription", notes = "Allows searching for Subscriptions using Apache lucene query syntax." + SEARCH_FIELDS)
    @PreAuthorize("hasAuthority('SCOPE_search:subscriptions')")
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse search(@RequestParam("query") String query, @RequestParam("sortColumn") String sortColumn,
                              @RequestParam("sortAscending") boolean sortAsc, @RequestParam("page") @Min(0) int page, @RequestParam("limit") @Min(1) @Max(25) int limit, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(this.searchResultMapper.mapToDto(this.subscriptionService.search(query, page, limit, sortColumn, sortAsc)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Import Subscription", notes ="Allows a csv/xlsx file to be uploaded.  Importing Subscriptions is not supported at this time.")
    @PreAuthorize("hasAuthority('SCOPE_import:subscriptions')")
    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse importSubscriptions(HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            File file = this.importFileUtil.downloadFile(request);
            return createSuccessfulResponse(subscriptionImportResponseMapper.mapToDto(subscriptionImportService.importSubscriptions(file)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

}
