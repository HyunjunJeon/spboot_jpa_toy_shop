package com.inflearn.jpabootshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // Getter는 다 열어주되, Setter는 필요시에만 열어줘야함...
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    // @JsonIgnore // Jackson이 JSON화 할 때, 제외하고 만듦
    @OneToMany(mappedBy = "member") // Order class 의 member 라는 field 가 연관관계의 주인임을 가르킴
    private List<Order> orders = new ArrayList<>();
}
