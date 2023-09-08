package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.subscription.CreateUpdateSubscriptionResultDto;
import com.spscommerce.interview.model.subscription.CreateUpdateSubscriptionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUpdateSubscriptionResultMapper implements EntityMapper<CreateUpdateSubscriptionResultDto, CreateUpdateSubscriptionResult> {

    private final SubscriptionMapper subscriptionMapper;

    @Override
    public CreateUpdateSubscriptionResult mapToDomain(CreateUpdateSubscriptionResultDto createUpdateProductResultDto) {
        return null;
    }

    @Override
    public CreateUpdateSubscriptionResultDto mapToDto(CreateUpdateSubscriptionResult createUpdateSubscriptionResult) {
        CreateUpdateSubscriptionResultDto createUpdateSubscriptionResultDto = new CreateUpdateSubscriptionResultDto();
        createUpdateSubscriptionResultDto.setSubscription(subscriptionMapper.mapToDto(createUpdateSubscriptionResult.getResult()));
        createUpdateSubscriptionResultDto.setWarnings(createUpdateSubscriptionResult.getWarnings());
        return createUpdateSubscriptionResultDto;
    }
}
