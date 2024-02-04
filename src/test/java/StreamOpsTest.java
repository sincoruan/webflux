import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.function.Consumer;

@Slf4j
public class StreamOpsTest {
    @Test
    public void subscribeMethod() {
        Flux<String> stockReq = Flux.just("A", "B", "C");
        stockReq
                .log() //使用log可以方便debug流的处理过程
                .subscribe();//没有任何参数表明不对流做任何操作
    }

    @Test
    public void subscribeWithErrorConsumer() {
        Flux<Integer> stockReq = Flux.range(1, 5).map(x -> {
            if(x > 4) throw new RuntimeException("greater than 4!");
            return x;
        });
        stockReq.subscribe(
                System.out::println, //流处理
                error -> System.out.println("error:" + error.getMessage()), //遇到上游异常时handle
                () -> System.out.println("Done") //流结束时操作
        );
    }

    @Test
    public void subscribeWithCustomizedSubscriber() {
        Flux flux = Flux.range(1, 5);
        Consumer consumer1 = null;
        flux.log().subscribe(new CustimizedSubscriber());
    }

    /**
     * 什么时候需要继承BaseSubscriber实现自己的subscriber？
     * 一个最基本的case就是，如果你不想一次读取流的所有元素，而希望自行控制流的消费速度时
     * 比如在flink中，有时候你需要控制统计的批次或者窗口大小，而不希望一次读取流中所有元素
     */
    public class CustimizedSubscriber<T> extends BaseSubscriber<T> {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            log.info("subscribed");
            request(1);
        }

        @Override
        protected void hookOnNext(T value) {
            log.info(value.toString());
            request(1);
        }

        @Override
        protected void hookOnComplete() {
            log.info("compelted");
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            log.info("error happen");
            super.hookOnError(throwable);
        }

        @Override
        protected void hookOnCancel() {
            log.info("canceled ");
            super.hookOnCancel();
        }

        @Override
        protected void hookFinally(SignalType type) {
            log.info("always execute");
            super.hookFinally(type);
        }
    }
}
