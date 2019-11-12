package com.inflearn.jpabootshop.api;

import com.inflearn.jpabootshop.domain.*;
import com.inflearn.jpabootshop.repository.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    // 주문조회 ver.1 : 엔티티 직접 노출
    @GetMapping("/api2/v1/orders")
    public List<Order> api2OrdersV1(){
        List<Order> allOrders = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : allOrders) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 컬렉션 조회~ 양방향이니까 JsonIgnore 잘 걸어줘야 출력가능...
            List<OrderItem> orderItems = order.getOrderItems();// 이번 메서드의 핵심..!!
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return allOrders;
    }

    @GetMapping("/api2/v2/orders")
    public List<OrderDto2> api2OrdersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto2> collect = orders.stream()
                    .map(o -> new OrderDto2(o))
                    .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api2/v3/orders")
    public List<OrderDto2> api2OrdersV3() {
        /*
            쿼리가 1개 나가긴 헀는데
            orderId가 2개라서(orderId가 많으면 많을수록 더 뻥튀기가 되겟지...)
            결과물이 2개씩 나왔음...!(객체주소까지 같네... 같은 엔티티가 조회된 것)
            -> JPQL 에 distinct 를 넣어봤음
            => 일단 orderID 쿼리에 distinct 나가서.. 뭔가 줄어드는 것처럼 보이긴 했는데..
            => 페이징 처리를 할 때 굉장히 문제가됌!!!!!! (메모리에서 페이징을 처리해버림,, 엄청난 과부하 발생)
         */
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto2::new)
                .collect(Collectors.toList());
    }



    @Getter
    static class OrderDto2{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;
        /*
            Entity에 대한 의존을 완전히 끊어버려야함
            아니면 OrderItem은 Entity가 그대로 나가게 됌...
            결국 같은 문제 발생!
         */

        public OrderDto2(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(
                    a -> a.getItem().getName()
            );
            this.orderItems = order.getOrderItems().stream()
                                    .map(orderItem -> new OrderItemDto(orderItem))
                                    .collect(Collectors.toList());
        }

        // 내부 컬렉션들까지도 Entity를 절대로 노출시키면 안돼!! 무조건 DTO로 변환해서 사용자와 통신하고
        // 내부 JPA 사용시에만 Entity 사용하는 습관들이도록!

        @Getter
        static class OrderItemDto{
            private String itemName;
            private int orderPrice;
            private int count;

            public OrderItemDto(OrderItem orderItem) {
                this.itemName = orderItem.getItem().getName();
                this.orderPrice = orderItem.getOrderPrice();
                this.count = orderItem.getCount();
            }
        }


    }
}
