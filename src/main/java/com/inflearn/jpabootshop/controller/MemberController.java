package com.inflearn.jpabootshop.controller;

import com.inflearn.jpabootshop.controller.dto.MemberForm;
import com.inflearn.jpabootshop.domain.Address;
import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/new")
    public String createMember(@Valid MemberForm memberForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // @Valid 에서 Error를 감지하면~
            return "members/createMemberForm"; // 에러메시지 태워서 보냄(MemberForm에 선언한 @NotEmpty 예제 참고)
        }
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping
    public String list(Model model) {
        /*
            @@ API 를 개발할때는 절대로 Entity를 외부로 보내면 안됌 @@
            Entity에 변경이 일어났다면, API 스펙도 변화가 생김.
            그렇다면 사용하는 쪽에서 굉장히 불완전한 API가 되기 때문에...

            Template Engine은 Server Side에서 돌기 때문에.. 뭐 괜찮긴 하지만
            그래도 모든 데이터는 DTO를 만들어서 보내고 받기를 권장함
         */
        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }
}
