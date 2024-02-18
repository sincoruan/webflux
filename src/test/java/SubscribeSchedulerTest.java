import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class SubscribeSchedulerTest {
    /**
     * 直接执行使用的是主线程
     */
    @Test
    void noThreadDefined() {
        Mono<String> mono = Mono.just("foo");
        mono.map(str -> str + "with no thread defined")
                .subscribe(str -> System.out.println(str + " thread name:" + Thread.currentThread().getName()));

    }

    /**
     * 使用
     */
    @Test
    public void runInNewThread() throws InterruptedException {
        Thread t = new Thread(() ->{
            Mono<String> mono = Mono.just("foo");
            mono.map(str -> str + "with no thread defined")
                    .subscribe(str -> System.out.println(str + " thread name:" + Thread.currentThread().getName()));
        });
        t.start();
        t.join();
    }

    @Test
    void threadDefined() {
        Mono<String> mono = Mono.just("foo");
        mono.map(str -> str + "with no thread defined")
                .subscribe(str -> System.out.println(str + Thread.currentThread().getName()));
    }

    /**
     * mono只有在block的时候才会执行真正执行，因此下面的两个block是串行执行的
     */
    @Test
    void testMonoBlock() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        Mono<String> result1 = caller1();
        Mono<String> result2 = caller2();
        System.out.println(result1.block() +":" +  result2.block());
        System.out.println("time cost:" + (System.currentTimeMillis() - start));//耗时 4s+
    }

    /**
     * 使用mono.zip 可以让两个mono并行执行起来
     */
    @Test
    void testMonoZip() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        //
        Mono<String> result1 = caller1();
        Mono<String> result2 = caller2();

        Mono<Tuple2<String, String>> result = Mono.zip(result1, result2);
        Tuple2<String, String> tuple2 = result.block();
        System.out.println("time cost:" + (System.currentTimeMillis() - start)); //耗时 2s+
    }

    Mono<String> caller1 () throws ExecutionException, InterruptedException {
        CompletableFuture<Mono<String>> result = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            System.out.println( "caller1 is in : " + Thread.currentThread().getName());
            return Mono.just("response1");
        });
        return result.get();
    }
    Mono<String> caller2 () throws ExecutionException, InterruptedException {
        CompletableFuture<Mono<String>> result = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            System.out.println( "caller2 is in : " + Thread.currentThread().getName());
            return Mono.just("response1");
        });
        return result.get();
    }

    void sleep(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
