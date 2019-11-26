package org.khemritolya.functional;

import java.util.Stack;

/**
 * A class representing a non-tail recursive function not bound by the Java recursion depth.
 *
 * @author Luis Hoderlein
 *
 * @param <A> the type of the argument to the recursive function
 * @param <R> the return type of the recursive function
 *
 * @see Function
 * @see Result
 * @see TransformFunction
 * @see JoinFunction
 */
public class RecursiveFunction<A, R> {

    /**
     * An interface which specifies how to recursively transform an argument into the result of the computation
     *
     * @param <Arg> the type of the argument to the recursive function
     * @param <Res> the return type of the recursive function
     *
     * @see RecursiveFunction
     * @see Result
     */
    public interface Function<Arg, Res> {
        /**
         * A function which when executed returns a lazily evaluatable result of the execution of this function
         *
         * @param function a reference to the RecursiveFunction which is the parent of this Function
         *             allows for recursive calls using calls to func.call()
         *             allows for returning lazily evaluateable results using calls to func.result()
         * @param argument the argument of a particular recursive call
         * @return a result, which can be lazily evaluated to a type {@code Res}
         *
         * @see Result
         */
        Result<Arg, Res> apply(RecursiveFunction<Arg, Res> function, Arg argument);
    }

    /**
     * An interface which specifies how to transform the result of a lazy evaluation
     *
     * @param <Res> the return type of the recursive function
     *
     * @see Result
     */
    public interface TransformFunction<Res> {
        /**
         * Transforms an object of type result on lazy evaluation
         *
         * @param r an object of result type to transform on evaluation
         * @return the transformed object
         */
        Res transform(Res r);
    }

    /**
     * An interface which specifies how to join two results of lazy evaluations
     *
     * @param <Res> the return type of the recursive function
     *
     * @see Result
     */
    public interface JoinFunction<Res> {
        /**
         * Join two objects of the type result on lazy evaluation
         *
         * @param r1 the first object of result type
         * @param r2 the second object of result type
         * @return the resultant object of type result
         */
        Res join(Res r1, Res r2);
    }

    /**
     * An immutable recursive type, which represents the lazily evaluatable result of a recursive call.
     *
     * Not able to be instantiated outside this file using new.
     *
     * @param <Arg> the type of the argument of the recursive function
     * @param <Res> the type of the returned object of the recursive function
     *
     * The Return Invariant:
     * An object of type Return lazily evaluates to either
     * 1. An object of type Res
     * 2. Another object of type Return
     *
     * @see Function
     * @see TransformFunction
     * @see JoinFunction
     */
    public static class Result<Arg, Res> {
        private final Res res;

        private final Arg arg;
        private final Function<Arg, Res> recursiveFunction;
        private final RecursiveFunction<Arg, Res> parent;

        private final Result<Arg, Res> prev;
        private final TransformFunction<Res> transformFunction;

        private final Result<Arg, Res> other;
        private final JoinFunction<Res> joinFunction;

        private int visited = 0;

        private Result(Res res) {
            this.parent = null;
            this.prev = null;
            this.other = null;
            this.res = res;
            this.arg = null;
            this.recursiveFunction = null;
            this.transformFunction = null;
            this.joinFunction = null;
        }

        private Result(RecursiveFunction<Arg, Res> parent, Arg arg, Function<Arg, Res> recursiveFunction) {
            this.parent = parent;
            this.res = null;
            this.other = null;
            this.prev = null;
            this.arg = arg;
            this.recursiveFunction = recursiveFunction;
            this.transformFunction = null;
            this.joinFunction = null;
        }

        private Result(Result<Arg, Res> prev, TransformFunction<Res> transformFunction) {
            this.parent = null;
            this.res = null;
            this.other = null;
            this.arg = null;
            this.prev = prev;
            this.recursiveFunction = null;
            this.transformFunction = transformFunction;
            this.joinFunction = null;
        }

        private Result(Result<Arg, Res> prev, Result<Arg, Res> other, JoinFunction<Res> joinFunction) {
            this.parent = null;
            this.other = other;
            this.prev = prev;
            this.res = null;
            this.arg = null;
            this.recursiveFunction = null;
            this.transformFunction = null;
            this.joinFunction = joinFunction;
        }

        /**
         * Transforms this Result into another Result by a specified transformation
         *
         * @param function a function specifying how the current result should be transformed on evaluation
         * @return a result which contains the information to transform the result upon evaluation
         */
        public Result<Arg, Res> transform(TransformFunction<Res> function) {
            return new Result<>(this, function);
        }

        /**
         * Joins this Result to another Result by a specified function
         *
         * @param other another result to join to this one
         * @param function a function which specifies how the two results should be added upon evaluation
         * @return a result which represents the union of this result and other
         */
        public Result<Arg, Res> join(Result<Arg, Res> other, JoinFunction<Res> function) {
            return new Result<>(this, other, function);
        }
    }

    /**
     * A function to evaluate for this recursive function
     */
    private final Function<A, R> function;

    /**
     * Construct an immutable RecursiveFunction with the specified functional interface
     *
     * @param function a function to evaluate for this recursive function
     * @see Function
     */
    public RecursiveFunction(Function<A, R> function) {
        this.function = function;
    }

    /**
     * Provides a way to get the lazily evaluatable result of a recursive call
     *
     * @param args the arguments of the recursive call
     * @return a result which can be evaluated to the result of the recursive call
     */
    public Result<A, R> call(A args) {
        return new Result<>(this, args, function);
    }

    /**
     * Provides a way to build a lazily evaluatable result from an object of return type
     *
     * @param res an object of return type
     * @return a result which can be lazily evaluated to the specified object
     */
    public Result<A, R> result(R res) {
        return new Result<>(res);
    }

    /**
     * Executes the function with the provided arguments, and returns the result of the recursive call
     *
     * @param args an initial argument to pass to the recursive function
     * @return the result of evaluating the recursive function with the given arguments
     */
    public R execute(A args) {
        Stack<Result<A, R>> callstack = new Stack<>();
        callstack.push(call(args));

        Stack<R> intermediates = new Stack<>();
        R result = null;

        while (!callstack.empty()) {
            Result<A, R> current = callstack.pop();

            if (result == null) {
                if (current.res != null) {
                    result = current.res;
                } else {
                    callstack.push(current);
                    if (current.recursiveFunction != null) {
                        callstack.push(current.recursiveFunction.apply(current.parent, current.arg));
                    } else if (current.prev != null) {
                        callstack.push(current.prev);
                    }
                }
            } else {
                if (current.res != null) throw new RuntimeException("invalid state: res");

                if (current.transformFunction != null) {
                    result = current.transformFunction.transform(result);
                } else if (current.joinFunction != null && current.visited == 0) {
                    current.visited = 1;
                    intermediates.push(result);
                    callstack.push(current);
                    callstack.push(current.other);
                    result = null;
                } else if (current.joinFunction != null && current.visited == 1) {
                    current.visited = 2;
                    result = current.joinFunction.join(result, intermediates.pop());
                } else if (current.joinFunction != null && current.visited > 1)
                    throw new RuntimeException("invalid state: visited");
            }
        }

        return result;
    }
}
