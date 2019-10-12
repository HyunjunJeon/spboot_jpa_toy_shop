package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.item.Book;
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

    @Transactional
    public Item updateItem(Long itemId, Book bookParam) {
        // Dirty Checking
        Item findItem = itemRepository.findOne(itemId); // 영속 상태의 객체(JPA를 통해 찾아왔으니..)
        // @@ Merge는 모든 속성을 변경함 @@
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());

        // 위의 코드가 Merge에서 일어나는 내부 과정임...

        /*
            변경 감지는 원하는 속성만 선택해서 Update가 가능하다
             > 물론 위 처럼 조금 불편하게 코드를 짜야하지만 위험성이 낮다

            Merge의 경우 전체 속성을 변경하고, 만약 속성값이 없다면 null을 집어넣어버림
             > 문제가 생길 가능성이 높아지는거지
             > 따라서 Merge를 사용하지 않는게 좋다!!
         */
        return findItem;
    }

    public List<Item> findAllItems(){
        return itemRepository.findAll();
    }

    public Item findOneItem(Long id){
        return itemRepository.findOne(id);
    }

}
