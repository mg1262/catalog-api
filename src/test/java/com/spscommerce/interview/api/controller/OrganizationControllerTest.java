package com.spscommerce.interview.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.spscommerce.interview.api.dto.organization.CreateUpdateOrganizationResultDto;
import com.spscommerce.interview.api.dto.organization.LocationDto;
import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.api.dto.subscription.SubscriptionDto;
import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.mapper.CreateUpdateOrganizationResultMapper;
import com.spscommerce.interview.api.mapper.OrganizationMapper;
import com.spscommerce.interview.api.mapper.SearchResultMapper;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.model.organization.CreateUpdateOrganizationResult;
import com.spscommerce.interview.service.OrganizationImportService;
import com.spscommerce.interview.service.OrganizationService;
import com.spscommerce.interview.util.RateLimiter;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private CatalogValidator validator;
    @MockBean
    private OrganizationService organizationService;
    @MockBean
    private OrganizationMapper organizationMapper;
    @MockBean
    private CreateUpdateOrganizationResultMapper createUpdateOrganizationResultMapper;
    @MockBean
    private SearchResultMapper searchResultMapper;

    @Test
    void createOrganization() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test");
        organizationDto.setLocation(new LocationDto());
        organizationDto.getLocation().setAddress("123 Main St");
        organizationDto.getLocation().setCity("Austin");
        organizationDto.getLocation().setState("TX");
        organizationDto.getLocation().setZipCode("78665");
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setDiscount(0.0);
        subscriptionDto.setTotalPrice(100.0);
        ProductDto productDto = new ProductDto();
        productDto.setName("product");
        productDto.setDescription("product description");
        productDto.setPrice(20.99);
        ProductDto subProduct = new ProductDto();
        subProduct.setName("sub-product");
        subProduct.setDescription("sub-product description");
        subProduct.setPrice(5.99);
        productDto.setSubProducts(new ArrayList<>());
        productDto.getSubProducts().add(subProduct);
        subscriptionDto.setProducts(new ArrayList<>());
        subscriptionDto.getProducts().add(productDto);
        organizationDto.setSubscriptions(new ArrayList<>());
        organizationDto.getSubscriptions().add(subscriptionDto);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(organizationDto );
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiter.getBucket()).thenReturn(bucket);
        Mockito.when(bucket.tryConsume(1)).thenReturn(true);
        Mockito.when(organizationMapper.mapToDomain(any(OrganizationDto.class))).thenReturn(new OrganizationEntity());
        Mockito.when(organizationService.createOrganization(any(OrganizationEntity.class))).thenReturn(new CreateUpdateOrganizationResult());
        Mockito.when(createUpdateOrganizationResultMapper.mapToDto(any(CreateUpdateOrganizationResult.class))).thenReturn(new CreateUpdateOrganizationResultDto());
        mvc.perform(post("/organizations").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(requestJson).with(jwt().jwt(builder -> builder.claim("create", "organizations"))))
                .andExpect(status().isOk());
    }

    @Test
    void createOrganizationRateLimitExceeded() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test");
        organizationDto.setLocation(new LocationDto());
        organizationDto.getLocation().setAddress("123 Main St");
        organizationDto.getLocation().setCity("Austin");
        organizationDto.getLocation().setState("TX");
        organizationDto.getLocation().setZipCode("78665");
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setDiscount(0.0);
        subscriptionDto.setTotalPrice(100.0);
        ProductDto productDto = new ProductDto();
        productDto.setName("product");
        productDto.setDescription("product description");
        productDto.setPrice(20.99);
        ProductDto subProduct = new ProductDto();
        subProduct.setName("sub-product");
        subProduct.setDescription("sub-product description");
        subProduct.setPrice(5.99);
        productDto.setSubProducts(new ArrayList<>());
        productDto.getSubProducts().add(subProduct);
        subscriptionDto.setProducts(new ArrayList<>());
        subscriptionDto.getProducts().add(productDto);
        organizationDto.setSubscriptions(new ArrayList<>());
        organizationDto.getSubscriptions().add(subscriptionDto);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(organizationDto );
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiter.getBucket()).thenReturn(bucket);
        Mockito.when(bucket.tryConsume(1)).thenReturn(false);
        mvc.perform(post("/organizations").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(requestJson).with(jwt().jwt(builder -> builder.claim("create", "organizations"))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void createOrganizationMissingPermission() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("test");
        organizationDto.setLocation(new LocationDto());
        organizationDto.getLocation().setAddress("123 Main St");
        organizationDto.getLocation().setCity("Austin");
        organizationDto.getLocation().setState("TX");
        organizationDto.getLocation().setZipCode("78665");
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setDiscount(0.0);
        subscriptionDto.setTotalPrice(100.0);
        ProductDto productDto = new ProductDto();
        productDto.setName("product");
        productDto.setDescription("product description");
        productDto.setPrice(20.99);
        ProductDto subProduct = new ProductDto();
        subProduct.setName("sub-product");
        subProduct.setDescription("sub-product description");
        subProduct.setPrice(5.99);
        productDto.setSubProducts(new ArrayList<>());
        productDto.getSubProducts().add(subProduct);
        subscriptionDto.setProducts(new ArrayList<>());
        subscriptionDto.getProducts().add(productDto);
        organizationDto.setSubscriptions(new ArrayList<>());
        organizationDto.getSubscriptions().add(subscriptionDto);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(organizationDto );
        mvc.perform(post("/organizations").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void readOrganization() throws Exception {
//        mvc.perform(get("/organizations/testId").contentType(MediaType.APPLICATION_JSON_UTF8)
//                        .content(requestJson))
//                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrganization() {
    }

    @Test
    void deleteOrganization() {
    }

    @Test
    void search() {
    }
}