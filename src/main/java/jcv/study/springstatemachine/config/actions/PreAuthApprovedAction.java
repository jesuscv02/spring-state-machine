package jcv.study.springstatemachine.config.actions;

import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class PreAuthApprovedAction implements Action<PaymentState, PaymentEvent> {
    /**
     * @param stateContext
     */
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> stateContext) {
        System.out.println("Sending Notification of Pre Authorization Approved");
    }
}
