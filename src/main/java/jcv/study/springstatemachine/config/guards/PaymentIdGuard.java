package jcv.study.springstatemachine.config.guards;

import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import jcv.study.springstatemachine.service.PaymentServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {
    /**
     * @param stateContext
     * @return
     */
    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> stateContext) {
        return stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
    }
}
