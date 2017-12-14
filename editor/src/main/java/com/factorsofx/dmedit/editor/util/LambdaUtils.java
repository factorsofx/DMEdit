package com.factorsofx.dmedit.editor.util;

import java.util.function.Consumer;

public class LambdaUtils
{
    private LambdaUtils() { throw new RuntimeException("Do not instantiate utils class"); }

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

    @FunctionalInterface
    public interface ThrowsAnythingConsumer<T>
    {
        void consume(T t) throws Exception;
    }
}
