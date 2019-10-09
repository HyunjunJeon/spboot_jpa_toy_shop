package com.inflearn.jpabootshop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    // Value Type
    // Immutable 하게 생성하는게 중요함

    private String city;
    private String street;
    private String zipcode;

    protected Address() {} // Proxy나 Reflection으로는 접근 가능하도록

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
