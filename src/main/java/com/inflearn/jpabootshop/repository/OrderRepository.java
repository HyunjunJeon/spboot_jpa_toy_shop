package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager entityManager;

    public void save(Order order){
        entityManager.persist(order);
    }

    public Order findOne(Long id){
        return entityManager.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){

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
