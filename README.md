# RecursiveFunction

##### A way of creating non-tail recursive function not bound by the Java recursion depth.

Example code [here](https://github.com/khemritolya/RecursiveFunction/blob/master/src/org/khemritolya/functional/test/Example.java):
```java
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
```

[More Examples](https://github.com/khemritolya/RecursiveFunction/blob/master/src/org/khemritolya/functional/test/Test.java)

##### How?
Lazy evaluation, 2 stacks, lambda abuse, etc. See [RecursiveFunction](https://github.com/khemritolya/RecursiveFunction/blob/master/src/org/khemritolya/functional/RecursiveFunction.java) for implementation details. You'll be particularly interested in the Result class and the execute() method in Recursive Function. Result is a recursive data structure, which can be evaluated into an object of the return type or another Result. RecursiveFunction's execute() method handles these evaluations.

##### Author: 
Luis Hoderlein

##### Motivation:
A challenge