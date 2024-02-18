package webclient;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMonoZip {
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    /**
     * mono只有在block的时候才会执行真正执行，因此下面的两个block是串行执行的
     */
    @Test
    void testMonoBlock() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        Mono<String> result1 = caller1();
        Mono<String> result2 = caller2();
        print(result1.block(), result2.block());
        System.out.println("time cost:" + (System.currentTimeMillis() - start));//耗时 4s+
    }
    void print(String s1, String s2) {
        System.out.println(s1 + s2);
    }

    /**
     * 使用mono.zip 应该可以让两个mono并行执行起来, 实际上没有并行起来，不知道为啥？ 后续研究...
     */
    @Test
    void testMonoZip() {
        Long start = System.currentTimeMillis();
        //
        Mono<String> result1 = caller1();
        Mono<String> result2 = caller2();

        // 为啥下面的code会阻塞导致串行？ 是因为callerSync1/callerSync2是在主线程睡的
//        Mono<String> result1 = callerSync1();
//        Mono<String> result2 = callerSync2();
        Mono<Tuple2<String, String>> result = Mono.zip(result1, result2);
        Tuple2<String, String> tuple2 = result.block();
        System.out.println("time cost:" + (System.currentTimeMillis() - start)); //耗时 2s+
    }
    Mono<String> caller1() {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            System.out.println("start call1");
            sleep(2000);
            System.out.println("caller1 is in : " + Thread.currentThread().getName());
            return "response1";
        }, executorService));
    }

    Mono<String> caller2() {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            System.out.println("start call2");
            sleep(2000);
            System.out.println("caller2 is in : " + Thread.currentThread().getName());
            return "response2";
        }, executorService));
    }

    Mono<String> callerSync1() {
        System.out.println("start call1");
        sleep(2000);
        System.out.println("caller1 is in : " + Thread.currentThread().getName());
        return Mono.just("response2");
    }

    Mono<String> callerSync2() {
            System.out.println("start call2");
            sleep(2000);
            System.out.println("caller2 is in : " + Thread.currentThread().getName());
            return Mono.just("response2");
    }

    void sleep(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
