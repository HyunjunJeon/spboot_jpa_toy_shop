package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public void save(Order order){
        entityManager.persist(order);
    }

    public Order findOne(Long id){
        return entityManager.find(Order.class, id);
    }

    public List<Order> findAllByCreteria(OrderSearch orderSearch) {
        // 추후 QueryDSL을 활용하여 동적 쿼리로 만들어볼 것
        return entityManager.createQuery(
                "select o from Order o join o.member m" +
                " where o.status =:status" +
                " and m.name =:name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                //.setFirstResult() // maxResult랑 사용해서 Paging도 가능~
                .setMaxResults(1000)
                .getResultList()
        ;
    }

    

}
