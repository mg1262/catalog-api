package com.spscommerce.interview.api.controller;

import com.spscommerce.interview.api.dto.validation.CatalogValidator;
import com.spscommerce.interview.api.mapper.*;
import com.spscommerce.interview.service.OrganizationImportService;
import com.spscommerce.interview.service.OrganizationService;
import com.spscommerce.interview.service.SubscriptionImportService;
import com.spscommerce.interview.service.SubscriptionService;
import com.spscommerce.interview.util.ImportFileUtil;
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
@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private CatalogValidator validator;
    @MockBean
    private SubscriptionService subscriptionService;
    @MockBean
    private SubscriptionImportService subscriptionImportService;
    @MockBean
    private ImportFileUtil importFileUtil;
    @MockBean
    private SubscriptionMapper subscriptionMapper;
    @MockBean
    private CreateUpdateSubscriptionResultMapper createUpdateSubscriptionResultMapper;
    @MockBean
    private SubscriptionImportResponseMapper subscriptionImportResponseMapper;
    @MockBean
    private SearchResultMapper searchResultMapper;

    @Test
    void createSubscription() {
    }

    @Test
    void readSubscription() {
    }

    @Test
    void updateSubscription() {
    }

    @Test
    void deleteSubscription() {
    }

    @Test
    void search() {
    }

    @Test
    void importSubscriptions() {
    }
}