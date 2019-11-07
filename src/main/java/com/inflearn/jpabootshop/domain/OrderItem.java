package com.inflearn.jpabootshop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inflearn.jpabootshop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 protected로 생성해서 사용을 막음(생성 메서드 사용 강제)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격

    private int count;

    // @@ 생성 메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 재고를 줄여줌
        item.removeStock(count);

        return orderItem;
    }

    // @@ 비지니스 메서드
    public void cancel() {
        // 재고 원상 복귀
        getItem().addStock(count);
    }

    // @@ 조회 메서드

    /**
     * 주문 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
