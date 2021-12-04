package com.instamic.authservice.model;

import lombok.Data;

@Data
public class Address {

    private String id;
    private String country;
    private String city;
    private String zipCode;
    private String streetName;
    private int buildingNumber;
}
