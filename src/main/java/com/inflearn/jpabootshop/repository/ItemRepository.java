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

    public Long save(Item item){
        if(item.getId() == null){
            entityManager.persist(item);
        } else {
            entityManager.merge(item); // 강제로 Update
        }
        return item.getId();
    }

    public Item findOne(Long id){
        return entityManager.find(Item.class, id);
    }

    public List<Item> findAll(){
        return entityManager
                .createQuery("select i from Item i", Item.class)
                .getResultList()
        ;
    }


}
