package com.spscommerce.interview.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Warning {

    private EntityType entityType;
    private String id;
    private boolean duplicate;
    private boolean created;
    private String message;


}
