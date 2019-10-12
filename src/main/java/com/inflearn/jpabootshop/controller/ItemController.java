package com.inflearn.jpabootshop.controller;

import com.inflearn.jpabootshop.controller.dto.BookForm;
import com.inflearn.jpabootshop.domain.item.Book;
import com.inflearn.jpabootshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String create(BookForm form) {
        // TODO BookForm 은 DTO니까.. 해당 데이터로 Book 객체를 바로 만들어 줄 수 있으면 편하겠다
        /*
            Book은 Builder를 지원해주고, BookForm은 createBook? 같은 메서드를 이용해서
            내부적으로 Book Builder를 사용해서 Book을 뱉어주는거지
            ** 그러고 Setter 를 Entity에서 다 지워버림 **
         */
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", itemService.findAllItems());
        return "items/itemList";
    }

    @GetMapping("/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model) {
        // Casting이 굳이 필요한 것으로 기획하진 않았지만 예제의 단순화를 위해서 지금은 Book 으로 캐스팅해서 씀
        Book book = (Book) itemService.findOneItem(itemId);

        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setName(book.getName());
        form.setPrice(book.getPrice());
        form.setStockQuantity(book.getStockQuantity());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());

        model.addAttribute("itemForm", form);

        return "items/updateItemForm";
    }

    @PostMapping("/{itemId}/edit")
    public String updateItem(@ModelAttribute("itemForm") BookForm form) {
        /*
            Controller에서 어설프게... Entity 생성하지마!
            DTO or parameter 자체를 Service Layer로 넘겨서 로직은 그쪽에서 처리토록 하자!

            @@ 트랜잭션이 있는 Service Layer에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하는 방식이 좋음!

            Controller에서는 DTO, Entity의 Validation 등 비지니스 로직과 무관한 일들을 하는게 좋다

         */
        Book book = new Book();
        // ID가 직접 넘어오면 변조의 가능성이 있기 때문에 검증을 더 해줘야함(ex> 해당 유저가 이 상품ID에 접근 권한이 있는지 등..)
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";
    }
}
