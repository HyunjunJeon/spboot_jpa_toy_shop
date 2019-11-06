package com.inflearn.jpabootshop;

import com.inflearn.jpabootshop.domain.*;
import com.inflearn.jpabootshop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager entityManager;

        public void dbInit1() {
            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address("나주", "빛가람로", "111111"));
            entityManager.persist(member);

            Book book1 = new Book();
            book1.setName("JPA BOOK1");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            entityManager.persist(book1);

            Book book2 = new Book();
            book2.setName("JPA BOOK2");
            book2.setPrice(15000);
            book2.setStockQuantity(100);
            entityManager.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 75000, 5);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);

            entityManager.persist(order);
        }

        public void dbInit2() {
            Member member2 = new Member();
            member2.setName("userB");
            member2.setAddress(new Address("서울", "양천로", "222222"));
            entityManager.persist(member2);

            Book book2 = new Book();
            book2.setName("SPRING BOOK1");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            entityManager.persist(book2);

            Book book3 = new Book();
            book3.setName("SPRING BOOK2");
            book3.setPrice(25000);
            book3.setStockQuantity(100);
            entityManager.persist(book3);

            OrderItem orderItem1 = OrderItem.createOrderItem(book2, 60000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book3, 125000, 5);

            Delivery delivery = new Delivery();
            delivery.setAddress(member2.getAddress());

            Order order = Order.createOrder(member2, delivery, orderItem1, orderItem2);

            entityManager.persist(order);
        }


    }
}

