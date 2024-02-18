import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

}
