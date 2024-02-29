package org.inf.ed.ac.uk.model;

public class Skeleton<I,O> {
    private final Executor<I,O> executor;
    private final Divider<I> divider;
    private final Conquerer<O> conquerer;

    public Skeleton(Executor<I,O> executor, Divider<I> divider, Conquerer<O> conquerer) {
        this.executor = executor;
        this.divider = divider;
        this.conquerer = conquerer;
    }
}
