package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.Address;
import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderStatus;
import com.inflearn.jpabootshop.domain.item.Book;
import com.inflearn.jpabootshop.domain.item.Item;
import com.inflearn.jpabootshop.exception.NotEnoughStockException;
import com.inflearn.jpabootshop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 주문() throws Exception{
        // Given
        Member member = createMember();
        Item item = createItem("JPA Book 1", 1000, 10);

        // When
        Long orderId = orderService.order(member.getId(), item.getId(), 3);

        // Then
        Order findOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, findOrder.getStatus());
        assertEquals("주문한 상품의 종류가 정확한가", 1, findOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량", 1000 * 3, findOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고 줄어듬", 7, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 주문_재고초과() throws Exception{
        // Given
        Member member = createMember();
        Item item = createItem("JPA Book 1", 1000, 10);

        int orderCount = 15;

        // When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // Then
        fail("재고 수량을 넘어서는 주문을 걸었으니까, 예외가 발생해야함");
    }

    @Test
    public void 주문취소() throws Exception{
        // Given
        Member member = createMember();
        Item item = createItem("JPA Book 1", 1000, 10);

        int orderCount = 5;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancleOrder(orderId);

        // Then
        Order findOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 재고가 원상 복구 되어야함", 10, item.getStockQuantity());
        assertEquals("주문 취소시 상태는 CANCEL로 변경되었는지", OrderStatus.CANCEL, findOrder.getStatus());
    }


    private Item createItem(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return item;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("tester1");
        member.setAddress(new Address("전남", "나주", "07567"));
        em.persist(member);
        return member;
    }
}
