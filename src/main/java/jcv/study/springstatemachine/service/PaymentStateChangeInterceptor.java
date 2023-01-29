package jcv.study.springstatemachine.service;

import jcv.study.springstatemachine.domain.Payment;
import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import jcv.study.springstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository repository;

    /**
     * @param state
     * @param message
     * @param transition
     * @param stateMachine
     * @param rootStateMachine
     */
    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState,
            PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
        Optional.ofNullable(message).flatMap(msg ->
                        Optional.ofNullable((Long) msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
                .ifPresent(paymentId -> {
                    Payment payment = repository.findById(paymentId).orElseThrow(() -> new RuntimeException("There was no state machine with id: " + paymentId));
                    payment.setState(state.getId());
                    repository.save(payment);
                });
    }
}
