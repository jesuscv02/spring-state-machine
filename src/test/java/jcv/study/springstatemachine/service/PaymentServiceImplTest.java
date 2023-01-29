package jcv.study.springstatemachine.service;

import jcv.study.springstatemachine.domain.Payment;
import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import jcv.study.springstatemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository repository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("32.99")).build();
    }

    @Transactional
    @Test
    void preAuthPayment() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println("Saved payment " + savedPayment);
        paymentService.preAuthPayment(savedPayment.getId());
        Payment preAuthPayment = repository.findById(savedPayment.getId())
                .orElseThrow(() -> new RuntimeException("There was no state machine with id: " + savedPayment.getId()));

        System.out.println("Approved payment" + preAuthPayment);
    }

    @Transactional
    @Test
    void authPayment() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println("Saved payment " + savedPayment);
        StateMachine<PaymentState, PaymentEvent> preAuth = paymentService.preAuthPayment(savedPayment.getId());
        if (PaymentState.PRE_AUTH.equals(preAuth.getState().getId())) {
            System.out.println("Payment was approved");
            StateMachine<PaymentState, PaymentEvent> auth = paymentService.authPayment(savedPayment.getId());
            System.out.println("Result of Auth: " + auth.getState().getId());
        } else
            System.out.println("Payment failt to pre-authorize");
    }
}