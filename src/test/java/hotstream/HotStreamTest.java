package hotstream;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class HotStreamTest {
    @Test
    public void sinmpleHotStreamCreation() {
        Sinks.Many<Integer> hotSource = Sinks.unsafe().many().multicast().directBestEffort();
        Flux<Integer> hotFlux = hotSource.asFlux();
        hotFlux.subscribe(d -> System.out.println("subscriber 1 get" + d));
        hotSource.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        hotSource.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        hotFlux.subscribe(d -> System.out.println("subscriber 2 get" + d));
        hotSource.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
        hotSource.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);
        hotSource.tryEmitComplete();
    }

    /**
     * 先发起订阅，再触发真正的数据  使用ConnectableFlux
     */
    @Test
    public void connectableFlux () throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 4);
        ConnectableFlux<Integer> connectableFlux = source.publish();
        connectableFlux.subscribe(d -> System.out.println("subscriber 1 get" + d));
        connectableFlux.subscribe(d -> System.out.println("subscriber 1 get" + d));
        System.out.println("finish subscription");

        Thread.sleep(5000);
        System.out.println("start to connect");
        connectableFlux.connect();

    }

    public void test() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });

    }
}
