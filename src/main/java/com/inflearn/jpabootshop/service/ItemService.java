package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.item.Item;
import com.inflearn.jpabootshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { // 너무 단순히 위임만 하는 Service는 굳이 필요가 있을지 고민해보는게...
    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item){
        return itemRepository.save(item);
    }

    public List<Item> findAllItems(){
        return itemRepository.findAll();
    }

    public Item findOneItem(Long id){
        return itemRepository.findOne(id);
    }

}
