package com.inflearn.jpabootshop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders(); // query 1번
        orders.forEach(o -> {
            // ToMany 가져온 것들을 직접 루프돌면서 채워넣음
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // query N번 ... => N+1 문제에 걸림...
            o.setOrderItems(orderItems);
        });

        return orders;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        // ToMany 관계는 각각 별도로 처리한다
        return em.createQuery(
                " select new com.inflearn.jpabootshop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        // ToOne 관계를 먼저 조회하고(row수가 증가하지 않으니)
        return em.createQuery(
                "select new com.inflearn.jpabootshop.repository.order.query.OrderQueryDto(o.id, m.name,o.orderDate, o.status, d.address) " +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class).getResultList();
    }
}
