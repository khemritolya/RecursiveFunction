package org.khemritolya.functional.test;

import org.khemritolya.functional.RecursiveFunction;

import java.util.List;

public class Example {
    public static void main(String[] args) {
        // Example: Fibonacci
        RecursiveFunction<Integer, Integer> fibonacci = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1);
            var prev = func.call(n - 1);
            var before = func.call(n - 2);
            return prev.join(before, Integer::sum);
        });

        System.out.println("Fibonacci:");
        for (int i = 0; i < 20; i++) {
            System.out.println(fibonacci.execute(i));
        }

        //Example: Factorial
        RecursiveFunction<Long, Long> factorial = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1L);
            else return func.call(n - 1L).transform(v -> v * n);
        });

        System.out.println("Factorial:");
        for (long i = 0; i < 20; i++) {
            System.out.println(factorial.execute(i));
        }

        //Example: List Increasing
        RecursiveFunction<List<Integer>, Boolean> isIncreasing = new RecursiveFunction<>((func, list) -> {
            if (list.size() <= 1)
                return func.result(true);
            else if (list.get(1) < list.get(0))
                return func.result(false);
            else
                return func.call(list.subList(1, list.size()));
        });

        System.out.println("isIncreasing:");
        System.out.println(isIncreasing.execute(List.of(1,2,3,4,5)));
        System.out.println(isIncreasing.execute(List.of(2,8,99,99)));
        System.out.println(isIncreasing.execute(List.of(5,5,5,5,5,5,5)));
        System.out.println(isIncreasing.execute(List.of(2)));
        System.out.println(isIncreasing.execute(List.of()));
        System.out.println(isIncreasing.execute(List.of(9,7,1,2,3)));
        System.out.println(isIncreasing.execute(List.of(2,3,5,4)));

        //Example: Mystery
        RecursiveFunction<Integer, Boolean> mystery = new RecursiveFunction<>((func, arg) -> {
            if (arg == 0)
                return func.result(false).transform(b -> !b);
            else if (arg < 0)
                return func.call(arg + 1).transform(b -> !b);
            else
                return func.call(arg - 2)
                        .transform(b -> !b)
                        .join(func.call(arg - 1), (a, b) -> a && b)
                        .transform(b -> !b);
        });

        System.out.println("Mystery:");
        System.out.println(mystery.execute(2));
        System.out.println(mystery.execute(0));
        System.out.println(mystery.execute(-100));
        System.out.println(mystery.execute(24));
        System.out.println(mystery.execute(1));
        System.out.println(mystery.execute(13));
        System.out.println(mystery.execute(-1));
    }
}