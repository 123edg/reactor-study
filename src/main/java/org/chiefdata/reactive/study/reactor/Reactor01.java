package org.chiefdata.reactive.study.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Reactor01 {
    public static void main(String[] args) {
      scheduler();

    }

    public static void scheduler(){
        Flux.range(1,10000)
                .publishOn(Schedulers.newSingle("111"))
                .subscribe(s->{
                    System.out.println(s);
                });
    }

    public static void sink(){
        Flux<Object> generate = Flux.generate(() -> 0, (state, sink) -> {
            sink.next(state * 2);
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        });

        generate.subscribe(System.out::println);

    }



    class  SampleSubscriber<T> extends BaseSubscriber<T>{
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            System.out.println("订阅成功");
            subscription.request(1);
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            System.out.println("订阅失败" + throwable);
        }
    }
}
