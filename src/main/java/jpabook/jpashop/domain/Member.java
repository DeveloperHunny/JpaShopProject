package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //연관관계 설정(주인x) 조회 권한만 있고 DB에 영향을 줄 수 없다.
    private List<Order> orders = new ArrayList<>(); //이후에 이 객체를 변경하면 안 됨. hibernates가 이 객체를 다른 클래스로 감싸고 있음. 이 객체를 바꾸면 hibernate가 관리 못 함.

}
