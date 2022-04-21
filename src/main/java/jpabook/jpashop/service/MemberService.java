package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {


    private final MemberRepository memberRepository;

    @Autowired //없어도 됨.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }




    /**
     * 회원 가입
     * @param member
     * @return id
     */
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }

    /**
     * 전체 회원 조회
     * @return all members
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 단일 회원 조회(id 이용)
     * @param memberId(Long)
     * @return find Member by id
     */
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }



}
