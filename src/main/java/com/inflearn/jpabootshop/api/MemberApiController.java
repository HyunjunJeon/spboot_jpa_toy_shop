package com.inflearn.jpabootshop.api;

import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /* 회원 등록 */
    @PostMapping("/api/v1/members") // REST API에서는 버전을 명시하는 것이 좋지 않지만 현재는 업그레이드하는 연습중이니까...
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // Entity를 직접 받는건 굉장히 위험한 Point -> DTO를 생성해서 처리해
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members") // 별도의 DTO를 사용해서, Entity가 변경된다 해도 API 스펙은 바뀌지 않음!! (무조건 DTO 쓰자!!)
    public CreateMemberResponse savememberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /* 회원 수정 */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updatememberV2(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        // 동작쿼리 와 Select Query를 분리해서 유지보수성을 높임(PK로 찾아오는 Tx는 별로.. Connection을 차지하지 않음)
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /* 회원 조회 */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        /*
            Collection을 직접 반환하면 스펙 변경(또는 확장)이 어려움
            {
                "data": []
            }
            위와 같은 식으로 해결하는 것이 좋음
         */
        return memberService.findMembers(); // 직접 Entity를 반환하면 안됌.....
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collectedList = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collectedList.size(), collectedList);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
