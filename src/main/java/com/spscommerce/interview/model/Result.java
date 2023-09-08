package com.spscommerce.interview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Result<ENTITY> {

    private ENTITY result;
    private List<Warning> warnings = new ArrayList<>();

}
