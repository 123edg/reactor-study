package org.chiefdata.reactive.study.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

public class ReactiveApi {

    public static void creatReactive() throws InterruptedException {
        Flux<String> just = Flux.just("1", "1", "1");
        Flux<Integer> range = Flux.range(1, 100);
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        Flux<String> from = Flux.from(Flux.just("1", "2"));
        Flux<Object> error = Flux.error(new RuntimeException(""));
        Flux.fromArray(new Integer[]{1});
        Flux.fromStream(Collections.emptyList().stream());


        //同步，逐个产生值使用不可变类型基本数据类型及其包装类，String属于不可变类型
        Flux.generate(() -> 0, (init, sink) -> {
            sink.next(init + 11);
            if (init == 100){
                sink.complete();
            }
            return init + 1;
        });

        //可变类型
        Flux.generate(AtomicInteger::new,(init,sink)->{
            int andIncrement = init.getAndIncrement();
            sink.next(andIncrement);
           return init;
        },init-> System.out.println(init));

        Flux.create(fluxSink -> {
            fluxSink.next(11);
        });


        //handle过滤作用
        Flux.just(1,23,4)
                .handle((i,sink)->{
                    String s = String.valueOf(i);
                    if (s != null){
                        sink.next(s);
                    }
                });


    }

    public static void justBetweenCallable() throws InterruptedException {

        //just是饿汉式，runbale call supplier都是懒汉式;
        Mono<Date> justMono = Mono.just(new Date());
        //使用runnable包装
        Mono<Object> runnableMono = Mono.fromRunnable(Date::new);

        //使用callable
        Mono<Date> callableMono = Mono.fromCallable(Date::new);

        //使用supplier
        Mono<Date> supplierMono = Mono.fromSupplier(Date::new);

        Thread.sleep(5000);

        justMono.subscribe(System.out::println);
        runnableMono.subscribe(System.out::println);
    }


    public static void justAndNull(){

    }
    public static void scheduler() throws InterruptedException {

        //使用固定线程池为cpu核数
        Flux.range(1,10000)
                .publishOn(Schedulers.parallel())
                .subscribe(System.out::println);

        //默认使用parallel线程池模型
        Flux.interval(Duration.ofSeconds(100));

        Flux.interval(Duration.ofSeconds(1000),Schedulers.newSingle("nihao"));

        Flux.range(1,1000).publishOn(Schedulers.elastic());

        //可重用线程池
        Schedulers.single();

        //当前线程
        Schedulers.immediate();

        //专一线程池
        Schedulers.newSingle("sch");

        Thread.sleep(111111);

    }



    /**
     * 当发生错误时，返回替代值，并结束流
     *
     * @throws InterruptedException
     */
    public static void onErrorReturn() throws InterruptedException {
        Flux<String> error = Flux.range(1, 100)
                .map((s) -> {
//                    if (s == 10) {
//                        throw new RuntimeException("111");
//                    }
                    return String.valueOf(s);
                }).onErrorResume(e -> {
                    return Mono.just("error");
                });
        error.subscribe(System.out::println);


        //包装成另外一个错误抛出
        Flux.just("timeout1")
                .flatMap(k -> Flux.just("11"))
                .onErrorMap(original -> new RuntimeException("oops, SLA exceeded", original));

    }

    /**
     * 如果发生错误就重试一次
     * @throws InterruptedException
     */
    public static void retry() throws InterruptedException {
        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .elapsed()
                .retry(1)
                .subscribe(System.out::println, System.err::println);

        Thread.sleep(2100);

        //retryWhen 与retry
        Flux<String> flux = Flux
                .<String>error(new IllegalArgumentException())
                .doOnError(System.out::println)
                .retryWhen(companion -> companion.take(3));
        flux.subscribe(System.out::println,System.err::println);
    }



    //doFinally 再序列终止时候执行，并且判断是什么类型
    public static void doFinally(){
        LongAdder longAdder = new LongAdder();
        Flux<String> take = Flux.just("foo", "bar")
                .doFinally(signalType -> {
                    longAdder.increment();
                }).take(1);
    }



    public static void onError() throws InterruptedException {
        Flux<Tuple2<Long, String>> boom = Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    return "tick " + input;
//                    throw new RuntimeException("boom");
                })
                .elapsed()
                .retry(1);


        boom.subscribe(System.out::println, System.err::println);
        Thread.sleep(2000);
    }


    /**
     * 只取一个就停止流
     */
    public static void take(){
        Flux<Integer> take = Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.println(i);
                    return i;
                }).take(1);
        take.subscribe(System.out::println);



    }


    public static void main(String[] args) throws InterruptedException {

    }
}
