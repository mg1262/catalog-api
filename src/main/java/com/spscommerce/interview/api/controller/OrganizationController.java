package com.spscommerce.interview.api.controller;


import com.spscommerce.interview.api.dto.common.ApiResponse;
import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.dto.validation.OnCreateOrganization;
import com.spscommerce.interview.api.dto.validation.OnUpdateOrganization;
import com.spscommerce.interview.api.mapper.CreateUpdateOrganizationResultMapper;
import com.spscommerce.interview.api.mapper.OrganizationMapper;
import com.spscommerce.interview.api.mapper.SearchResultMapper;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.service.OrganizationImportService;
import com.spscommerce.interview.service.OrganizationService;
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

@Validated
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController extends BaseController {

    private final RateLimiter rateLimiter;
    private final CatalogValidator validator;
    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;
    private final CreateUpdateOrganizationResultMapper createUpdateOrganizationResultMapper;
    private final SearchResultMapper searchResultMapper;


    private static final String SEARCH_FIELDS = "\nSearch Fields:\nid\nname\nlocation.address\nlocation.city\nlocation.state\nlocation.zipCode\nsubscription.id\nsubscription.discount\nsubscription.totalPrice\nsubscription.product.id\nsubscription.product.name\nsubscription.product.description\nsubscription.product.price\nsubscription.product.sub_product.id\nsubscription.product.sub_product.name\nsubscription.product.sub_product.description\nsubscription.product.sub_product.price";

    @Validated(OnCreateOrganization.class)
    @ApiOperation(value = "Create Organization", notes = "Provides the ability to create an Organization. \nSubscriptions provided will be created as well. \nExisting Subscriptions are not allowed. \nProducts associated with Subscriptions will be created if they don't exist.  \nExisting Products will not be updated but will be associated.")
    @PreAuthorize("hasAuthority('SCOPE_create:organizations')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createOrganization(@RequestBody @Valid OrganizationDto organizationDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            this.validator.validateSubscriptions(organizationDto.getSubscriptions(), bindingResult);
            return createSuccessfulResponse(createUpdateOrganizationResultMapper.mapToDto(organizationService.createOrganization(organizationMapper.mapToDomain(organizationDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Read Organization", notes = "Returns an Organization")
    @PreAuthorize("hasAuthority('SCOPE_read:organizations')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse readOrganization(@PathVariable("id") String organizationId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(organizationMapper.mapToDto(organizationService.readOrganization(organizationId)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @Validated(OnUpdateOrganization.class)
    @ApiOperation(value = "Update Organization", notes = "Provides the ability to update an existing Organization.  \nSubscriptions without IDs will be created.  \nExisting Subscriptions must belong to this Organization already. \nExisting Subscriptions details will not be updated but associations will be updated.  \nProducts without IDs will be created.  \nExisting Products details will not be updated but associations will be updated.")
    @PreAuthorize("hasAuthority('SCOPE_update:organizations')")
    @PutMapping(path = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateOrganization(@PathVariable("id") String organizationId, @RequestBody OrganizationDto organizationDto, HttpServletRequest request, BindingResult bindingResult) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            if (!organizationId.equals(organizationDto.getId())) {
                ErrorCodes.ORG_UPDATE_ID_MISMATCH.throwException();
            }
            this.validator.validateSubscriptions(organizationDto.getSubscriptions(), bindingResult);
            return createSuccessfulResponse(createUpdateOrganizationResultMapper.mapToDto(organizationService.updateOrganization(organizationMapper.mapToDomain(organizationDto))), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Delete Organization", notes = "Organization and associated Subscriptions will be deleted. Associated Products will be preserved.")
    @PreAuthorize("hasAuthority('SCOPE_delete:organizations')")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteOrganization(@PathVariable("id") String organizationId, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            organizationService.deleteOrganization(organizationId);
            return createSuccessfulResponse(null, request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }

    @ApiOperation(value = "Search Organization", notes = "Allows searching for Organizations using Apache lucene query syntax." + SEARCH_FIELDS)
    @PreAuthorize("hasAuthority('SCOPE_search:organizations')")
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse search(@RequestParam("query") String query, @RequestParam("sortColumn") String sortColumn,
                              @RequestParam("sortAscending") boolean sortAsc, @RequestParam("page") @Min(0) int page, @RequestParam("limit") @Min(1) @Max(25) int limit, HttpServletRequest request) {
        if (rateLimiter.getBucket().tryConsume(1)) {
            return createSuccessfulResponse(this.searchResultMapper.mapToDto(this.organizationService.search(query, page, limit, sortColumn, sortAsc)), request);
        }
        ErrorCodes.TOO_MANY_REQUESTS.throwException();
        //Code should never reach here
        return null;
    }



}
