package com.spscommerce.interview.dao.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Location {

    private String address;
    private String city;
    private String state;
    private String zipCode;
}
