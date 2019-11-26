package org.khemritolya.functional.test;

import org.khemritolya.functional.RecursiveFunction;

import java.util.List;

public class Test {
    private static void assertEquals(Object a, Object b) {
        if (a == null && b == null) return;
        else if (a != null && b != null) {
            if (a.equals(b)) return;
        }

        throw new RuntimeException("ERR: " + a + " not equals " + b);
    }

    public static void main(String[] args) {
        RecursiveFunction<Integer, Integer> transformOrderTest1 = new RecursiveFunction<>(((func, a) -> {
            if (a <= 0) return func.result(0);
            else return func.call(a - 1)
                    .transform(v -> v + 1)
                    .transform(v -> v / 2)
                    .transform(v -> v * 2);
        }));

        RecursiveFunction<Integer, Integer> transformOrderTest2 = new RecursiveFunction<>(((func, a) -> {
            if (a <= 0) return func.result(0);
            else return func.call(a - 1)
                    .transform(v -> v + 1)
                    .transform(v -> v * 2)
                    .transform(v -> v / 2);
        }));

        for (int i = 0; i < 100; i++) {
            assertEquals(0, transformOrderTest1.execute(i));
        }

        for (int i = 1; i < 100; i++) {
            assertEquals(i, transformOrderTest2.execute(i));
        }

        System.out.println("PASSED: transformOrder");

        RecursiveFunction<Integer, Integer> fib = new RecursiveFunction<>(((func, n) -> {
            if (n <= 1) return func.result(1);
            var oneback = func.call(n - 1);
            var twoback = func.call(n - 2);
            return oneback.join(twoback, Integer::sum);
        }));

        for (int i = 0; i < 20; i++) {
            assertEquals(fib.execute(i), fib(i));
        }

        System.out.println("PASSED: fib");

        RecursiveFunction<Integer, Integer> trip = new RecursiveFunction<>(((func, n) -> {
            if (n <= 1) return func.result(1);
            var oneback = func.call(n - 1);
            var twoback = func.call(n - 2);
            var threeback = func.call(n - 3);
            twoback = twoback.join(threeback, Integer::sum);
            return oneback.join(twoback, Integer::sum);
        }));

        for (int i = 0; i < 20; i++) {
            assertEquals(trip.execute(i), trip(i));
        }

        System.out.println("PASSED: trip");

        RecursiveFunction<Integer, Integer> mTrip = new RecursiveFunction<>(((func, n) -> {
            if (n <= 1) return func.result(1);
            //System.out.println(n);
            var oneback = func.call(n - 1).transform(v -> v * 2);
            var twoback = func.call(n - 2).transform(v -> v / 2);
            var threeback = func.call(n - 3);
            return oneback.join(twoback.join(threeback, joinFunction), joinFunction).transform(v -> v - 3);
        }));

        RecursiveFunction<Integer, Integer> mTrip2 = new RecursiveFunction<>(((func, n) -> {
            if (n <= 1) return func.result(1);
            var oneback = func.call(n - 1).transform(v -> v * 2);
            var twoback = func.call(n - 2).transform(v -> v / 2);
            var threeback = func.call(n - 3).transform(v -> v - 1);
            return oneback.join(twoback.join(threeback, joinFunction), joinFunction).transform(v -> v - 2);
        }));

        RecursiveFunction<Integer, Integer> mTrip3 = new RecursiveFunction<>(((func, n) -> {
            if (n <= 1) return func.result(1);
            //System.out.println(n);
            var oneback = func.call(n - 1).transform(v -> v * 2);
            var twoback = func.call(n - 2).transform(v -> v / 2);
            var threeback = func.call(n - 3).transform(v -> v - 3);
            return oneback.join(twoback.join(threeback, joinFunction), joinFunction);
        }));

        for (int i = 0; i < 25; i++) {
            int k = mTrip(i);
            assertEquals(mTrip.execute(i), k);
            assertEquals(mTrip2.execute(i), k);
            assertEquals(mTrip3.execute(i), k);
        }

        System.out.println("PASSED: mTrip");

        RecursiveFunction<List<Integer>, Boolean> isListPalindrome = new RecursiveFunction<>((func, list) -> {
            if (list.size() <= 1)
                return func.result(false).transform(b -> !b);

            if (!list.get(0).equals(list.get(list.size() - 1)))
                return func.result(true).transform(b -> !b);

            return func.call(list.subList(1, list.size() - 1));
        });

        assertEquals(true, isListPalindrome.execute(List.of(1,2,3,3,2,1)));
        assertEquals(true, isListPalindrome.execute(List.of(11)));
        assertEquals(true, isListPalindrome.execute(List.of(1,2,3,2,1)));
        assertEquals(true, isListPalindrome.execute(List.of()));

        assertEquals(false, isListPalindrome.execute(List.of(10,2,3,3,2,1)));
        assertEquals(false, isListPalindrome.execute(List.of(10,2,1)));

        RecursiveFunction<Integer, Boolean> isIntegerPalindrome = new RecursiveFunction<>((func, n) -> {
            String s = String.valueOf(n);
            if (s.length() <= 1)
                return func.result(true);
            else if (s.charAt(0) != s.charAt(s.length() - 1))
                return func.result(false);
            else if (s.length() == 2 && s.charAt(0) == s.charAt(1))
                return func.result(true);
            else
                return func.call(Integer.parseInt(s.substring(1, s.length() - 1)));
        });

        assertEquals(true, isIntegerPalindrome.execute(1));
        assertEquals(true, isIntegerPalindrome.execute(11));
        assertEquals(true, isIntegerPalindrome.execute(121));
        assertEquals(true, isIntegerPalindrome.execute(1221));
        assertEquals(true, isIntegerPalindrome.execute(19091));

        assertEquals(false, isIntegerPalindrome.execute(12));
        assertEquals(false, isIntegerPalindrome.execute(123));
        assertEquals(false, isIntegerPalindrome.execute(1234));
        assertEquals(false, isIntegerPalindrome.execute(12437421));
        assertEquals(false, isIntegerPalindrome.execute(12992));

        System.out.println("PASSED: palindrome");

        RecursiveFunction<Integer, Integer> counter = new RecursiveFunction<>((func, n) -> {
            if (n <= 0) return func.result(0);
            return func.call(n - 1).transform(v -> v + 1);
        });

        for (int i = 1; i < 8; i++) {
            int lim = (int)Math.pow(7, i);
            assertEquals(lim, counter.execute(lim));
        }

        RecursiveFunction<Integer, Integer> strangeCounter = new RecursiveFunction<>((func, n) -> {
            if (n <= 0) return func.result(0);
            else return func.result(1)
                    .join(func.call(n-1), Integer::sum)
                    .transform(v -> v + 1)
                    .join(func.call(n-1), Integer::sum)
                    .transform(v -> v / 2);
        });

        for (int i = 1; i < 5; i++) {
            int lim = (int)Math.pow(2, i);
            assertEquals(lim, strangeCounter.execute(lim));
        }

        System.out.println("PASSED: counter");

        RecursiveFunction<Integer, Integer> sumOfN = new RecursiveFunction<>((func, n) -> {
            if (n <= 0) return func.result(0);
            else return func.call(n - 1).transform(v -> v + n);
        });

        for (int i = 1; i < 100; i++) {
            assertEquals(i * (i + 1) / 2, sumOfN.execute(i));
        }

        System.out.println("PASSED: sumOfN");

        RecursiveFunction<Integer, Integer> factorial = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1);
            else return func.call(n - 1).transform(v -> v * n);
        });

        for (int i = 0; i < 100; i++) {
            assertEquals(fact(i), factorial.execute(i));
        }

        System.out.println("PASSED: factorial");

        RecursiveFunction<Integer, Integer> nonRecursive = new RecursiveFunction<>((func, n) ->
                func.result(n).transform(v -> v + 1).transform(v -> v * v));

        for (int i = 0; i < 100; i++) {
            assertEquals((i+1) * (i+1), nonRecursive.execute(i));

        }

        RecursiveFunction<Integer, Integer> echo = new RecursiveFunction<>(RecursiveFunction::result);

        for (int i = 0; i < 100; i++) {
            assertEquals(i, echo.execute(i));
        }

        System.out.println("PASSED: non-recursive");

        RecursiveFunction<Integer, Integer> pow2 = new RecursiveFunction<>((func, n) -> {
            if (n < 0) return func.result(0);
            if (n == 0) return func.result(1);
            return func.call(n - 1).transform(v -> v * 2);
        });

        for (int i = 0; i < 30; i++) {
            assertEquals((int)Math.pow(2, i), pow2.execute(i));
        }

        System.out.println("PASSED: powers of 2");
    }

    private static RecursiveFunction.JoinFunction<Integer> joinFunction = Integer::sum;

    private static int fib(int n) {
        if (n <= 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    private static int trip(int n) {
        if (n <= 1) return 1;
        return trip(n - 1) + trip(n - 2) + trip(n - 3);
    }

    private static int mTrip(int n) {
        if (n <= 1) return 1;
        int oneback = mTrip(n - 1) * 2;
        int twoback = mTrip(n - 2) / 2;
        int threeback = mTrip(n - 3);
        return oneback + twoback + threeback - 3;
    }

    private static int fact(int n) {
        if (n <= 1) return 1;
        else return fact(n - 1) * n;
    }
}
