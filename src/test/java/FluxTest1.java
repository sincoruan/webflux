import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class FluxTest1 {
    @Test
    public void fluxConstruct() {
        // just
        Flux<Integer> flux = Flux.just(1,2,3,4,5);
        flux.subscribe(e->System.out.println(e));
        // from iterable
        Flux.fromIterable(Arrays.asList("A", "B", "C"));
        // from array
        Flux.fromArray(new String[]{"A", "B", "C"});
        // from stream 这种flux要避免订阅超过一次
        Flux streamFlux = Flux.fromStream(Stream.of(new String[]{"A", "B", "C"}));
        streamFlux.subscribe();
        streamFlux.subscribe();//不可订阅两次
    }

    @Test
    void fluxGenerate() {
        Flux<Long> flux = Flux.generate(
                // state 初始化
                AtomicLong::new,
                // 如何生成流 具体细节
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next(i);
                    if(i == 10) sink.complete();
                    return state;
                },
                // 定义flux结束后的操作(比如数据库链接关闭可以在此操作)
                state -> System.out.println("Done!")
        );
        flux.subscribe(System.out::println);
    }

    @Test
    void fluxCreate() {
        Flux<String> stock = Flux.create(sink -> {
            new MyDataListener(){
                public void onReceiveData(String str) {
                    sink.next(str);
                }
                public void onComplete() {
                    sink.complete();
                }
            };
        }, FluxSink.OverflowStrategy.DROP);
    }

    public class MyDataListener{
        public void onReceiveData(String str) {
            // TODO
        }
        public void onComplete(){
            // TODO
        }
    }
}


