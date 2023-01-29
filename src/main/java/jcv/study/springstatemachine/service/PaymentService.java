package jcv.study.springstatemachine.service;

import jcv.study.springstatemachine.domain.Payment;
import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;


public interface PaymentService {
    Payment newPayment(Payment payment);
    StateMachine<PaymentState, PaymentEvent> preAuthPayment(Long paymentId);
    StateMachine<PaymentState,PaymentEvent> authPayment(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> declineAuthPayment(Long paymentId);
}
