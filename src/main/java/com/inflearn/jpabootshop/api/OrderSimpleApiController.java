package com.inflearn.jpabootshop.api;

import com.inflearn.jpabootshop.domain.Address;
import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderSearch;
import com.inflearn.jpabootshop.domain.OrderStatus;
import com.inflearn.jpabootshop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    // x To One 관계
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        /*
        for(Order order: all){
            order.getMember().getName(); // Lazy 강제로 초기화함
            order.getDelivery().getAddress(); // Lazy 강제로 초기화함
        }
         */
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        /*
            검색된 ORDER는 2개
            지금은 1(order) + N(order -> member : 2) + N(order -> delivery : 2) 따라서 총 5개의 쿼리가 실행됌...
            ==> N+1 문제 발생!! -> 실제로는 1 + N 문제인거지 ㅋㅋ(1번 실행 후에 N번 만큼 추가실행되는...)
         */
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화되는 부분
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화되는 부분
        }
    }

    @GetMapping("/api/v3/orders")
    public List<SimpleOrderDto> ordersV3() {
        // Fetch Join을 사용한 해결 -> V1,V2에서 쿼리 5개 나가던 것이,,, 쿼리 1개 나감..
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }
}
