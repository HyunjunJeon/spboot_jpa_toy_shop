package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderSearch;
import com.inflearn.jpabootshop.repository.order.simplequery.SimpleOrderQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    private final JPAQueryFactory jpaQueryFactory;

    public void save(Order order) {
        entityManager.persist(order);
    }

    public Order findOne(Long id) {
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

    // Ver.3
    public List<Order> findAllWithMemberDelivery() {
        // Fetch Join을 이용한 해결
        return entityManager.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class).getResultList();
    }

    // Ver.4
    public List<SimpleOrderQueryDto> findOrderDtos() {
        // Entity 나 Embedded 타입만 JPA에서 반환 가능
        /*
            JPQL을 직접 짜놨기 때문에... 재사용성 없음(딱 Fit하게 맞춰놨기도 하고, 코드도 지저분하고.. 그대신 Network을 덜 사용하긴 함)
            -> 근데.. 요즘 network이 너무 좋아서 엄청나게 데이터양이 큰 컬럼이 선택되지 않는 한 크게 성능 최적화가 있지는 않음
            new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환

            그리고 API 스펙이 여기에 들어와 있는 것(Repository 계층에..) 자체도 맞지 않음!!
            따로 계층을 더 빼서 사용하는게 관리 포인트적인 측면으로 좋음
            예를 들면,,, OrderQueryRepository 뭐 이렇게 만들어서 조회 전용 DTO 포인트는 따로 주는게 좋음
            OrderRepository는 Order 엔티티를 조회하는 레포지토리임..!
         */
        return entityManager.createQuery(
                "select new com.inflearn.jpabootshop.repository.SimpleOrderQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
        /*
            JPQL distinct 키워드
            1) DB에 SQL 날릴 때, distinct 를 포함해서 날림
            2) 가져온 데이터에 중복이 있을 경우, Collection에 담을때 제거하고 담아줌
         */

        // Collection fetch join을 사용하기 때문에 paging이 불가능함!
        // 여러개의 Collection에 fetch join을 사용해서도 안된다.
        return entityManager.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery2(int offset, int limit) {
        // yaml 파일에 default_batch_fetch_size 옵션을 적용했음(Global)
        /*
            @페이징 한계돌파
            1) xToOne 관계를 모두 Fetch Join으로 처리한다(xToOne 관계는 row의 수를 증가시키지 않으므로 페이징 쿼리에 영향 X)
            2) 컬렉션은 지연로딩으로 조회(-> 처음 Entity 구성할 때 했던 그대로..)
            3) 지연 로딩의 '성능 최적화' 를 위해 Batch 로 쿼리가 나갈 수 있게끔 Global(YAML), Local(@BatchSize) 한 전략을 적용
               -> SQL IN절을 사용함
         */
        return entityManager.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /*
    public List<Order> findAllByQueryDsl(OrderSearch orderSearch) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus(), order), nameLike(orderSearch.getMemberName(), member))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus status, QOrder order) {
        if (status == null) {
            return null;
        }
        return order.status.eq(status);
    }

    private BooleanExpression nameLike(String name, QMember member) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return member.name.like(name);
    }
     */
}
