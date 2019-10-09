package com.inflearn.jpabootshop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 저장할 때 구분할 수 있어야함
@Getter @Setter
public class Book extends Item{
    private String author;
    private String isbn;
}
