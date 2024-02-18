package com.practice.reactor;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.stream.Stream;

public class FluxDemo {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1,2,3,4,5);
        flux.subscribe(e->System.out.println(e));
        Flux.fromIterable(Arrays.asList("A", "B", "C"));
        Flux.fromArray(new String[]{"A", "B", "C"});
        Flux.fromStream(Stream.of(new String[]{"A", "B", "C"}));

    }
}
