package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.item.Book;
import com.inflearn.jpabootshop.domain.item.Item;
import com.inflearn.jpabootshop.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    ItemService service;

    @Autowired
    ItemRepository repository;

    @Autowired
    EntityManager em;


    @Test
    @Rollback(false) // 다음 테스트를 위해서 False 로 놔둠
    public void Item_Save() throws Exception{
        // Given
        Item item = new Book();
        item.setName("Algorithm");
        item.setPrice(1000);

        // When
        service.saveItem(item);

        // Then
        em.flush();
        assertEquals(repository.findOne(item.getId()), item);
    }

    // 단순히 조회하는 부분이라 생략
    @Test
    public void Item_findOne() throws Exception{}

    // 단순히 조회하는 부분이라 생략
    @Test
    public void Item_findAll() throws Exception{}


}
