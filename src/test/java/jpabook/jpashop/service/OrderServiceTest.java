package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.addStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "마북로", "105-304"));
        em.persist(member);
        return member;
    }

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember("회원1");

        Book book = createBook("시골 JPA", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).isEqualTo(findOrder.getStatus()); //주문 시 주문 상태는 ORDER
        assertThat(findOrder.getOrderItems().size()).isEqualTo(1); //주문 상품 종류 수가 동일해야 한다.
        assertThat(findOrder.getTotalPrice()).isEqualTo(orderCount * 10000); //총 주문 가격 로직 검증
        assertThat(book.getStockQuantity()).isEqualTo(10 - orderCount); //주문 시 상품의 재고 수량이 줄어야 한다.

    }



    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember("회원1");
        Book book = createBook("시골 JPA", 10000, 10);



        //when
        int orderCount = 11;

        //then
        NotEnoughStockException e = Assertions.assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });

        assertThat(e.getMessage()).isEqualTo("need more stock");


    }

     @Test
     public void 주문취소() throws Exception{
         //given
         Book book = createBook("JPA", 10000, 10);
         Member member = createMember("KIM");

         int orderCount = 5;
         Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

         //when
         orderService.cancleOrder(orderId);

         //then
         Order getOrder = orderRepository.findOne(orderId);
         assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL); //주문 상태 확인
         assertThat(book.getStockQuantity()).isEqualTo(10); //주문 재고 원복 확인


     }




}