package com.inflearn.jpabootshop.domain;

import com.inflearn.jpabootshop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 실무에서는 사용 금지...
    // 왜냐면 JoinTable 자체가 FK,FK 만 가질 수 있도록 구성되니까..
    // 하다못해 날짜라도 넣으려고 하면.. 1:N ~ N:1 로 깨부셔야함
    @ManyToMany
    @JoinTable(name = "category_item",
        joinColumns = @JoinColumn(name = "category_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    // Self 양방향 연관관계 맺어줌..
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

}
