package com.inflearn.jpabootshop.controller;

import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.domain.Order;
import com.inflearn.jpabootshop.domain.OrderSearch;
import com.inflearn.jpabootshop.domain.item.Item;
import com.inflearn.jpabootshop.service.ItemService;
import com.inflearn.jpabootshop.service.MemberService;
import com.inflearn.jpabootshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findAllItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/new")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        // Service Layer에는 식별자만 넘기고 Transaction이 있는 Service Layer 가 실제 기능을 담당하는게 깔-끔
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    @GetMapping
    public String list(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrderSearch(orderSearch);

        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancleOrder(orderId);
        return "redirect:/orders";
    }

}
