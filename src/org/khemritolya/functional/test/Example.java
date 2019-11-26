package org.khemritolya.functional.test;

import org.khemritolya.functional.RecursiveFunction;

public class Example {
    public static void main(String[] args) {
        RecursiveFunction<Integer, Integer> fibonacci = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1);
            var prev = func.call(n - 1);
            var before = func.call(n - 2);
            return prev.join(before, Integer::sum);
        });

        for (int i = 0; i < 35; i++) {
            System.out.println(fibonacci.execute(i));
        }

        RecursiveFunction<Long, Long> factorial = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1L);
            else return func.call(n - 1L).transform(v -> v * n);
        });

        for (long i = 0; i < 20; i++) {
            System.out.println(factorial.execute(i));
        }
    }
}