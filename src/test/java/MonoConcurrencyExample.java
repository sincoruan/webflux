import reactor.core.publisher.Mono;

public class MonoConcurrencyExample {

    public static void main(String[] args) {
        Mono<String> mono1 = Mono.fromCallable(() -> {
            System.out.println("mono1" + Thread.currentThread().getName());
            sleep(2000);
            return "Result from Mono 1";
        });

        Mono<String> mono2 = Mono.fromCallable(() -> {
            System.out.println("mono2" + Thread.currentThread().getName());
            sleep(2000);
            return "Result from Mono 2";
        });

        mono1.flatMap(result1 ->
                mono2.map(result2 -> {
                    return result1 + " | " + result2;
                })
        ).subscribe(finalResult -> {
            System.out.println("Final Result: " + finalResult);
        });
    }

    static void sleep(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
