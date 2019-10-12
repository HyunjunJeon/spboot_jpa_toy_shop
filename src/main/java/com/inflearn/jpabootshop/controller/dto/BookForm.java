package com.inflearn.jpabootshop.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
    // Item Field
    private Long id; // 수정 기능을 위해 id를 받아야함
    private String name;
    private int price;
    private int stockQuantity;
    // Book Field
    private String author;
    private String isbn;

}
