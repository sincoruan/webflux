package webclient;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMonoZip {
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    /**
     * mono只有在block的时候才会执行真正执行，因此下面的两个block是串行执行的
     * 耗时： ~4s
     */
    @Test
    void testMonoBlock() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        Mono<String> result1 = callerAsync1();
        Mono<String> result2 = callerAsync2();
        print(result1.block(), result2.block());
        System.out.println("time cost:" + (System.currentTimeMillis() - start));//耗时 4s+
    }
    void print(String s1, String s2) {
        System.out.println(s1 + s2);
    }

    /**
     * 使用mono.zip 可以让两个mono并行执行起来
     * 耗时： ~2s
     */
    @Test
    void testMonoZip() {
        Long start = System.currentTimeMillis();
        Mono<String> result1 = callerAsync1();
        Mono<String> result2 = callerAsync2();
        Mono<Tuple2<String, String>> result = Mono.zip(result1, result2);
        Tuple2<String, String> tuple2 = result.block();
        System.out.println("time cost:" + (System.currentTimeMillis() - start)); //耗时 2s+
    }

    /**
     * 使用CompletableFuture将两个流并发执行起来，达到了类似Mono.zip的效果
     * 耗时： ~2s
     */
    @Test
    void testMonoWithCompletableFuture() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> callerAsync1().block());
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> callerAsync1().block());
        completableFuture1.get();
        completableFuture2.get();
        System.out.println("time cost:" + (System.currentTimeMillis() - start)); //耗时 2s+
    }


    Mono<String> callerAsync1() {
        //             sleep(2000);  //不能用sleep，如果使用sleep的话会导致主线程在睡，而不是mono中delay
        return Mono.just("response1").delayElement(Duration.ofSeconds(2));
    }

    Mono<String> callerAsync2() {
        //             sleep(2000);  //不能用sleep，如果使用sleep的话会导致主线程在睡，而不是mono中delay
        return Mono.just("response2").delayElement(Duration.ofSeconds(2));
    }

    void sleep(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
