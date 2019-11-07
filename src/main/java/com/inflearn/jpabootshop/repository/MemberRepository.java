package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {
    @PersistenceContext // 엔티티 매니저 주입
    private EntityManager entityManager;

    public void save(Member member) {
        entityManager.persist(member);
    }

    public Member findOne(Long id) {
        return entityManager.find(Member.class, id);
    }

    public List<Member> findAll() {
        return entityManager
                .createQuery("select m from Member m", Member.class) // JPQL은 Entity 대상으로 쿼리를 함
                .getResultList()
                ;
    }

    public List<Member> findByName(String name) {
        return entityManager
                .createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList()
                ;
    }

}
