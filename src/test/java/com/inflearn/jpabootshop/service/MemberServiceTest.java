package com.inflearn.jpabootshop.service;

import com.inflearn.jpabootshop.domain.Member;
import com.inflearn.jpabootshop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest // 지금은 service와 repository Layer를 넘나들며 테스트 해야하니까.. SpringBoot 를 Integration 해서 테스트 진행 (Autowired를 위해..)
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        // Given
        Member member = new Member();
        member.setName("Tester");

        // When
        Long saveId = memberService.join(member);

        // Then
        em.flush(); // 일부러 Insert 문을 넣어서 보기 위해서
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // Given
        Member member1 = new Member();
        member1.setName("t1");

        Member member2 = new Member();
        member2.setName("t1");

        // When
        memberService.join(member1);
        memberService.join(member2);

        // Then
        fail("IllegalStateException 이 발생해야 한다....");
    }


}
