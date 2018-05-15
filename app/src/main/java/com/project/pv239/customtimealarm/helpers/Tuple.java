package com.project.pv239.customtimealarm.helpers;

public class Tuple<T> {
    private final T x;
    private final T y;

    public Tuple(T x, T y){
        this.x = x;
        this.y = y;
    }

    public T getFirst() {
        return x;
    }

    public T getSecond() {
        return y;
    }

    @Override
    public String toString() {
        return x.toString()+"|"+y.toString();
    }
}
