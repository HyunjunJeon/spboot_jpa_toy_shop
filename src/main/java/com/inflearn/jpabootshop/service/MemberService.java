package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 각 메서드에 걸림
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional // 쓰기가 필요하므로 readOnly = false를 해줌
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 검증
    private void validateDuplicateMember(Member member) {
        // 실무에서는 name에 Unique 제약조건을 걸든 다른곳에 걸든 해서 최종적인 검증을 해야한다
        // 멀티 쓰레드 환경, 여러대의 WAS가 돌아가고 DB에 동시에 접근하는 경우 하단의 Validation이 통과될 가능성도 있다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = findOne(id);
        member.setName(name);
    }
}
