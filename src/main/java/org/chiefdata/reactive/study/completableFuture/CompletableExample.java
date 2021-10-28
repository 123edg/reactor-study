package org.chiefdata.reactive.study.completableFuture;

import java.util.concurrent.*;

public class CompletableExample {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void create() throws ExecutionException, InterruptedException {
        //第一种方式
        CompletableFuture<String> cf = new CompletableFuture<>();

        new Thread(() -> {
            System.out.println("一些耗时任务");
            cf.complete("ss");
        }).start();

        //阻塞状态
        String s = cf.get();

        //利用已经知道的结果直接返回
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.completedFuture("11");

        //第二种方法，有返回值
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("耗时任务");
            return "完成任务";
        });

        //利用的线程池，默认使用jvm的ForkJoinPool.commonPool线程
        CompletableFuture.supplyAsync(() -> {
            System.out.println("111");
            return "11";
        }, executorService);

        //第三种方法 没有返回值
        CompletableFuture.runAsync(() -> {
            System.out.println("111");
        });

        CompletableFuture.runAsync(() -> {
            System.out.println("11");
        }, executorService);
    }


    public static void thenRunStage() throws ExecutionException, InterruptedException {
        //1. 指定一个任务完成后，再完成另外一个任务;
        //1.1 thenRun thenRunSync // 指定哪个线程完成这个任务，如果上一个任务的耗时很长则会使用处理上个任务中的线程，如果很快，则使用主线程
        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> "1", executorService)
                .thenRun(() -> {
                    System.out.println("thenRun");
                    System.out.println("thenRun-" + Thread.currentThread().getName());
                }).thenRunAsync(() -> {
                    System.out.println("thenRunAsync");
                    System.out.println("thenRunAsync-" + Thread.currentThread().getName());
                });


        System.out.println("main-" + Thread.currentThread().getName());
    }

    public static void thenApplyStage() {
        //apply 是有返回值，accept没有返回值
        CompletableFuture.completedFuture("11")
                .thenApply(s -> {
                    System.out.println(s);
                    return "d";
                }).thenAccept(System.out::println);
    }


    public static void whenComplete() throws InterruptedException {
        //当任务计算完成获取抛出异常的时候执行

        CompletableFuture.runAsync(() -> {
            System.out.println("run");
            int i = 1 / 0;
            //当一个任务完成执行，没有返回值
        }).whenComplete((r, e) -> {
            System.out.println("com" + r);
            System.out.println("com" + e);
            //exceptionally 有异常执行
        }).exceptionally((e) -> {
            System.out.println("finally" + e.getMessage());
            return null;
            //当任务有没有异常都会执行
        }).handle((r,e)->{
            System.out.println("handle " + r);
            System.out.println("handle " + e.getMessage());
            return  "1";
        });

        TimeUnit.SECONDS.sleep(1);
    }


    public static void thenCompose() throws ExecutionException, InterruptedException {
        //编排两个任务，任务2依赖任务2
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("111");
        CompletableFuture<String> cf2 = cf1.thenCompose(f -> CompletableFuture.supplyAsync(() -> f + "1"));
        cf2.get();
    }

    public static void thenCombine(){
        //将两个任务的结果组合起来
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("22");
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture("33");
        //返回结果
        CompletableFuture<String> cf3 = cf1.thenCombine(cf2, (r1, r2) -> r1 + r2);

        //不放回结果
        cf1.thenAcceptBoth(cf2,(r1,r2)-> System.out.println(r1 + r2));

        cf1.runAfterBoth(cf2,()-> System.out.println("11"));

    }

    public static void applyToEither(){
        //谁执行快，悬着那个结果
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("22");
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture("33");

        CompletableFuture<String> cf3 = cf1.applyToEither(cf2, r -> "r");

        cf1.acceptEither(cf2,System.out::println);

        cf1.runAfterEither(cf2,()->{
            System.out.println("11");
        });

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        whenComplete();
    }
}
