package com.github.devil.client.akka;

import akka.actor.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eric.yao
 * @date 2021/2/9
 * AssociationErrorEvent
 **/
@Slf4j
public class MessageDeadActor extends UntypedAbstractActor {

    @Override
    public void preStart() throws Exception {
        super.preStart();
        context().system().eventStream().subscribe(self(), DeadLetter.class);
        context().system().eventStream().subscribe(self(), Dropped.class);
        context().system().eventStream().subscribe(self(), UnhandledMessage.class);
        context().system().eventStream().subscribe(self(), Terminated.class);
    }

    @Override
    public void onReceive(Object msg){
        if(msg instanceof AllDeadLetters || msg instanceof DeadLetterSuppression){
            log.error("receive an dead message,{}",msg);
        }

    }
}
