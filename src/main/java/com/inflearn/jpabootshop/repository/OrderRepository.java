package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager entityManager;
    //private final JPAQueryFactory jpaQueryFactory;

    public void save(Order order){
        entityManager.persist(order);
    }

    public Order findOne(Long id){
        return entityManager.find(Order.class, id);
    }

    // 동적 쿼리 중 String은 SQL Injection의 위험도 있고 해서... 쓰지 않음(+ 너무 복잡함)
    public List<Order> findAll(OrderSearch orderSearch) {
        // 추후 QueryDSL을 활용하여 동적 쿼리로 만들어볼 것
        return entityManager.createQuery(
                "select o from Order o join o.member m" +
                        " where o.status = :status" +
                        " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                //.setFirstResult() // maxResult랑 사용해서 Paging도 가능~
                .setMaxResults(1000)
                .getResultList()
        ;
    }

    /**
     * JPA 표준 동적 쿼리 생성 스펙인 Criteria 를 사용한 동적 쿼리 생성
     *
     * @param orderSearch OrderSearch
     * @return List<Order>
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));

        TypedQuery<Order> query = entityManager.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }

    public List<Order> findAllByQueryDsl(OrderSearch orderSearch) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return order.status.eq(status);
    }

    private BooleanExpression nameLike(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return member.name.like(name);
    }
}
