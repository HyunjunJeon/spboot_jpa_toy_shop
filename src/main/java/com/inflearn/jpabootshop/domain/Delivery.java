package com.inflearn.jpabootshop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore // 양방향 걸리는 곳 중 한군데는 다 JsonIgnore로 끊어버려야함
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // 중간에 상태를 추가하거나 삭제하더라도.. 문제없음
    private DeliveryStatus status;
}
