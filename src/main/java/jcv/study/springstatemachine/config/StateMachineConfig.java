package jcv.study.springstatemachine.config;

import jcv.study.springstatemachine.config.actions.*;
import jcv.study.springstatemachine.config.guards.PaymentIdGuard;
import jcv.study.springstatemachine.domain.PaymentEvent;
import jcv.study.springstatemachine.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    private final PaymentIdGuard paymentIdGuard;
    private final AuthAction authAction;
    private final PreAuthAction preAuthAction;
    private final PreAuthApprovedAction preAuthApprovedAction;
    private final PreAuthDeclinedAction preAuthDeclinedAction;
    private final AuthApprovedAction authApprovedAction;
    private final AuthDeclineAction authDeclineAction;

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).target(PaymentState.NEW)
                .action(preAuthAction)
                .guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.NEW).event(PaymentEvent.PRE_AUTH_APPROVED).target(PaymentState.PRE_AUTH)
                .action(preAuthApprovedAction)
                .and()
                .withExternal().source(PaymentState.NEW).event(PaymentEvent.PRE_AUTH_DECLINED).target(PaymentState.PRE_AUTH_ERROR)
                .action(preAuthDeclinedAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE).target(PaymentState.PRE_AUTH)
                .action(authAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).event(PaymentEvent.AUTH_APPROVED).target(PaymentState.AUTH)
                .action(authApprovedAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).event(PaymentEvent.AUTH_DECLINED).target(PaymentState.AUTH_ERROR)
                .action(authDeclineAction);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("stateChanged(from: %s, to %s)", from, to);
            }
        };
        config.withConfiguration().listener(adapter);
    }
}
