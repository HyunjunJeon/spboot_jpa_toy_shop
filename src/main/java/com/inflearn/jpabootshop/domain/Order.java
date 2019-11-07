package com.inflearn.jpabootshop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 protected로 생성해서 사용을 막음(생성 메서드 사용 강제)
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 1:N 관계(양방향)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // FK
    private Member member;

    // Cascade 옵션 때문에 Order 엔티티에 객체 할당되면 강제로 persist 다 날려줌
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 그냥 Date를 쓴다면 @DateTimeFormat 을 사용해야함...
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCLE]


    // ## 연관관계 메서드 - 양방향 연관관계일때, 원자적으로 결합시키기 위해서 ##
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // ## 생성 메서드 : 생성하는데 필요한 비지니스 로직도 포함하니까 잘못 생성하는걸 방지해줌 + 편리성
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();

        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    // @@ 비지니스 로직

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송이 완료된 상품은 주문 취소가 불가능 합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);

        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel(); // 재고 원상 복귀를 위해
        }

    }

    // @@ 조회 로직

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice).sum()
                ;
    }


}
