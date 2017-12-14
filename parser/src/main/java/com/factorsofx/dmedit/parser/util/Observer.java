package com.factorsofx.dmedit.parser.util;

public interface Observer<T>
{
    void notify(Observable<T> observable, T arg);
}
