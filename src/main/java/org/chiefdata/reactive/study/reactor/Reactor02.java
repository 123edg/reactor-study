package org.chiefdata.reactive.study.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Reactor02 {
    public static void fromSupplier(){
        Mono<Object> objectMono = Mono.fromSupplier(new Supplier<Object>() {

            @Override
            public Object get() {
                System.out.println(Thread.currentThread() + "1");
                try {
                    Thread.sleep(11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });


        Mono.fromRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
            }
        }).subscribe(System.out::println);

        objectMono.subscribe(System.out::println);
        System.out.println(Thread.currentThread() + "2");
    }

    public static void mapBetweenFlatMap() {
        /**output
         * FluxArray
         * FluxArray
         * FluxArray
         */
        Flux.just(2,2,4).map(s -> {
            return Flux.just(1,1,3);
        }).subscribe(System.out::println);

        /**
         * 1,1,3
         * 1,1,3
         * 1,1,3
         */
        Flux.just(2,2,4).flatMap(s -> {
            return Flux.just(1,1,3);
        }).subscribe(System.out::println);
    }



    public static void flatMapMany(){


        Mono<List<String>> listMono = Mono.fromSupplier(() -> {
            List<String> strings = Arrays.asList("1", "2");
            return strings;
        });

        /**
         * output [1, 2]
         */
        listMono.subscribe(System.out::println);


        /**
         * output
         * 1
         * 2
         */
        Flux<String> stringFlux = listMono.flatMapMany(strings -> Flux.fromIterable(strings));
        stringFlux.subscribe(System.out::println);


        stringFlux.collectList().doOnNext((list)->{
            list.forEach(System.out::println);
        }).subscribe(System.out::println);

    }



    public static void main(String[] args) {
        mapBetweenFlatMap();

    }
}
