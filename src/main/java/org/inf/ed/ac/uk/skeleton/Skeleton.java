package org.inf.ed.ac.uk.skeleton;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Skeleton<I,O> {
    private final Executor<I,O> executor;
    private final Divider<I> divider;
    private final Conquerer<O> conquerer;
    private final ForkJoinPool threadPool;

    public Skeleton(Integer parallelism, Executor<I,O> executor, Divider<I> divider, Conquerer<O> conquerer) {
        this.executor = executor;
        this.divider = divider;
        this.conquerer = conquerer;
        this.threadPool = new ForkJoinPool(parallelism);
    }

    public O execute(I input) {
        GenericDaCTask<I,O> task = new GenericDaCTask<>(input, divider, conquerer, executor);
        threadPool.execute(task);
        return task.join();
    }

    public Iterable<O> executeMultiple(Iterable<I> inputs) {
        ArrayList<O> results = new ArrayList<>();
        for (I input : inputs) {
            GenericDaCTask<I,O> task = new GenericDaCTask<>(input, divider, conquerer, executor);
            threadPool.execute(task);
            results.add(task.join());
        }
        return results;
    }
}
