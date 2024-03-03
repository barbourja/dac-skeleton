package org.inf.ed.ac.uk.skeleton;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class DaCSkeleton<I,O> implements IExecutor<I,O> {
    private final IExecutor<I,O> EXECUTOR;
    private final IDivider<I> DIVIDER;
    private final IConquerer<O> CONQUERER;
    private ForkJoinPool THREAD_POOL;

    public DaCSkeleton(Integer parallelism, IExecutor<I,O> executor, IDivider<I> divider, IConquerer<O> conquerer) {
        this.EXECUTOR = executor;
        this.DIVIDER = divider;
        this.CONQUERER = conquerer;
        this.THREAD_POOL = new ForkJoinPool(parallelism);
    }

    public O execute(I input) {
        GenericDaCTask<I,O> task = new GenericDaCTask<>(input, DIVIDER, CONQUERER, EXECUTOR);
        THREAD_POOL.execute(task);
        return task.join();
    }

    public Iterable<O> executeMultiple(Iterable<I> inputs) {
        ArrayList<O> results = new ArrayList<>();
        for (I input : inputs) {
            GenericDaCTask<I,O> task = new GenericDaCTask<>(input, DIVIDER, CONQUERER, EXECUTOR);
            THREAD_POOL.execute(task);
            results.add(task.join());
        }
        return results;
    }

    public IExecutor<I,O> getExecutor() {
        return EXECUTOR;
    }

    public IDivider<I> getDivider() {
        return DIVIDER;
    }

    public IConquerer<O> getConquerer() {
        return CONQUERER;
    }

    public synchronized boolean changeParallelism(Integer newParallelismValue) {
        if (newParallelismValue < 1 || newParallelismValue == THREAD_POOL.getParallelism()) {
            return false;
        }
        // no setParallelism in JDK 18 :(
        THREAD_POOL.shutdown(); // shut down current pool
        THREAD_POOL = new ForkJoinPool(newParallelismValue); // create new pool with updated parallelism
        return true;
    }

    public String toString() {
        String output = this.getClass().getSimpleName() + " \n" +
                "-".repeat(this.getClass().getSimpleName().length() + 1) + "\n" +
                "Parallelism: " + THREAD_POOL.getParallelism() + "\n" +
                "ConcreteExecutor: " + EXECUTOR.getClass().getSimpleName() + "\n" +
                "ConcreteDivider: " + DIVIDER.getClass().getSimpleName() + "\n" +
                "ConcreteConquerer: " + CONQUERER.getClass().getSimpleName();
        return output;
    }
}
