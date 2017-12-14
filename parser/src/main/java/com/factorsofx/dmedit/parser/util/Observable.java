package com.factorsofx.dmedit.parser.util;

public interface Observable<T>
{
    void addObserver(Observer<T> observer);
}
