package com.inflearn.jpabootshop.domain.item;

import com.inflearn.jpabootshop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속관계를 구성할 때 한테이블에 다 때려박는 전략
@DiscriminatorColumn(name = "dtype") // 저장시에 상속된 것들을 구분할 수 있도록하는 컬럼
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany
    private List<Category> categories = new ArrayList<>();
}
