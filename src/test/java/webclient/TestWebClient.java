package webclient;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.CompletableFuture;

class TestWebClient {
    WebClient webClient = WebClient.create("http://w7r9e.wiremockapi.cloud");
    Mono<String> result1 = webClient.get().uri("/test1").retrieve().bodyToMono(String.class);
    Mono<String> result2 = webClient.get().uri("/test2").retrieve().bodyToMono(String.class);

    // 如果直接使用block，那么这两个webclient会顺序执行，第一个block拿到结果，才会开始执行第二个
    @Test
    void testWebClientWithDirectBlock() {
        Long start = System.currentTimeMillis();
        print(result1.block(), result2.block());

        System.out.println("time cost:" + (System.currentTimeMillis() - start));
    }

    /**
     * 如果使用Mono.zip,会将两个流同时执行起来
     * 那么它是多线程吗？ Mono.zip 方法本身并不会创建新的线程。
     * 它是基于 Reactor 的异步执行模型实现的，因此它会利用 Reactor 提供的调度器（Schedulers）来执行操作，而不是直接创建线程。
     */
    @Test
    void testWebClientWithMonoZip() {
        Long start = System.currentTimeMillis();
        Mono<Tuple2<String, String>> result = Mono.zip(result1, result2);
        result.block();
        System.out.println("time cost:" + (System.currentTimeMillis() - start));
    }

    /**
     * 可以将testWebClientWithDirectBlock进行改造，实现两个直接block也能同时执行
     *
     */
    @Test
    void testWebClientWithCompletableFutureBlock() {
        Long start = System.currentTimeMillis();
        Mono<String> mono1 = Mono.fromFuture(CompletableFuture.supplyAsync(() -> result1.block()));
        Mono<String> mono2 = Mono.fromFuture(CompletableFuture.supplyAsync(() -> result1.block()));
        print(mono1.block(), mono2.block());
        System.out.println("time cost:" + (System.currentTimeMillis() - start));
    }

    void print(String s1, String s2) {
    }
}
