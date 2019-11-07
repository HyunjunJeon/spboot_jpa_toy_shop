package com.inflearn.jpabootshop.repository;

import com.inflearn.jpabootshop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager entityManager;

    public Long save(Item item) {
        if (item.getId() == null) {
            // 새로운 Object니까 persist 해서 DB에 등록
            entityManager.persist(item);
        } else {
            // 준영속 상태의 객체를 받아서 억지로 엔티티 필드 전체를 교체를 시켜버리고
            // 영속상태의 새로운 객체를 반환함
            entityManager.merge(item);
        }
        return item.getId();
    }

    public Item findOne(Long id) {
        return entityManager.find(Item.class, id);
    }

    public List<Item> findAll() {
        return entityManager
                .createQuery("select i from Item i", Item.class)
                .getResultList()
                ;
    }


}
