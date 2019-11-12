package com.inflearn.jpabootshop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<OrderQueryDto> findAllByDtoOptimizing() {
        List<OrderQueryDto> orders = findOrders(); // ToOne 관계 먼저 조회하고

        List<Long> orderIds = orders.stream()
                .map(o -> o.getOrderId()) // 먼저 조회한 것에서 얻은 식별자로
                .collect(Collectors.toList());

        // ToMany 관계를 한방에 조회하고~
        List<OrderItemQueryDto> orderItems = em.createQuery(
                " select new com.inflearn.jpabootshop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 루프를 돌려서 매칭하는게 아니라, 그냥 Map으로 바꿔서 메모리에 올려두고
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

        // 바로 setOrderItems 에 매칭시켜버리기
        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orders;
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        // 쿼리가 1번만 나가게끔 만듬!
        // 그리고 Order를 기준으로 페이징은 불가능해...
        List<OrderFlatDto> resultList = em.createQuery(
                "select new com.inflearn.jpabootshop.repository.order.query.OrderFlatDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count" +
                        ")" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();

        return resultList;
    }
}
