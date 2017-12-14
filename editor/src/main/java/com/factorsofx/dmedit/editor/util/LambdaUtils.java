package com.factorsofx.dmedit.editor.util;

import java.util.function.Consumer;

public class LambdaUtils
{
    private LambdaUtils() { throw new RuntimeException("Do not instantiate utils class"); }

    /**
     * Takes a consumer that can throw any checked exception, and returns one that throws it wrapped in a
     * runtime exception. Useful if you want to avoid {@code try} statements in your lambda chains.
     * @param consumer Consumer, can throw any exception.
     * @param <T> Type to consume
     * @return A {@link Consumer} that will throw a runtime exception wrapping any exception that is thrown during
     * execution.
     */
    public static <T> Consumer<T> uncheckedConsumer(ThrowsAnythingConsumer<? super T> consumer)
    {
        return (t) ->
        {
            try
            {
                consumer.consume(t);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Same as {@link Consumer}, except it's declared to throw {@link Exception}.
     * @param <T> Type of object to consume
     */
    @FunctionalInterface
    public interface ThrowsAnythingConsumer<T>
    {
        void consume(T t) throws Exception;
    }
}
