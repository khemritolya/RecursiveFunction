# RecursiveFunction

##### A way of creating non-tail recursive function not bound by the Java recursion depth.

Made possible with lambda magic:

```java
public class Example {
    public static void main(String[] args) {
        RecursiveFunction<Integer, Integer> factorial = new RecursiveFunction<>((func, n) -> {
            if (n <= 1) return func.result(1);
            var prev = func.call(n - 1);
            var before = func.call(n - 2);
            return prev.join(before);
        });

        for (int i = 0; i < 40; i++) {
            System.out.println(factorial.execute(i));
        }  
    }
}
```