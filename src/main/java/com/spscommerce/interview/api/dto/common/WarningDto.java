package com.spscommerce.interview.api.dto.common;

import com.spscommerce.interview.model.EntityType;
import lombok.Data;

@Data
public class WarningDto {

    private EntityType entityType;
    private String id;
    private boolean duplicate;
    private boolean created;
    private String message;

}
