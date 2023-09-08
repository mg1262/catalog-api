package com.spscommerce.interview.api.dto.organization;

import com.spscommerce.interview.api.dto.validation.OnCreateOrganization;
import com.spscommerce.interview.api.dto.validation.OnUpdateOrganization;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Validated
public class LocationDto {

    @NotBlank(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Size(max = 100)
    String address;
    @NotBlank(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Size(max = 100)
    String city;
    @NotBlank(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Size(max = 2)
    String state;
    @NotBlank(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Size(min = 5, max = 10)
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$")
    String zipCode;

}
