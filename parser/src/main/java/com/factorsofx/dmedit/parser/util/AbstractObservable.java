package com.factorsofx.dmedit.parser.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * An improved version of {@link java.util.Observable} that uses weak references and generics.
 * This class is meant to be extended.
 * @param <T> The type of notification
 */
public abstract class AbstractObservable<T> implements Observable<T>
{
    private List<WeakReference<Observer<T>>> observerRefs = new ArrayList<>();

    /**
     * Adds the given observer to the list of observers. Note that this does <em>NOT</em> maintain a strong
     * reference to the observer, instead a {@link WeakReference} is used.
     * @param observer The observer to add
     */
    @Override
    public void addObserver(Observer<T> observer)
    {
        observerRefs.add(new WeakReference<>(observer));
    }

    /**
     * Calls {@link Observer#notify} with <code>this</code> and the given argument.
     * @param arg Argument to send to the observers.
     */
    protected void notifyObservers(T arg)
    {
        observerRefs.forEach((observerRef) ->
        {
            Observer<T> reifiedObserver = observerRef.get();
            if(reifiedObserver != null)
            {
                reifiedObserver.notify(this, arg);
            }
            else
            {
                observerRefs.remove(observerRef);
            }
        });
    }
}
