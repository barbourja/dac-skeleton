package org.inf.ed.ac.uk.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;

public class GenericDaCTask<I,O> extends RecursiveTask<O> {
    private I input;
    private Divider<I> divider;
    private Conquerer<O> conquerer;
    private Executor<I,O> executor;

    public GenericDaCTask(I input, Divider<I> divider, Conquerer<O> conquerer, Executor<I,O> executor) {
        this.input = input;
        this.divider = divider;
        this.conquerer = conquerer;
        this.executor = executor;
    }

    @Override
    protected O compute() {
        if (!divider.canDivide(input)) { // Base case
            return executor.execute(input);
        }
        else { // Perform DaC
            Iterable<I> dividedInputs = divider.divide(input); // Use division logic to produce new inputs
            Collection<GenericDaCTask<I,O>> subTasks = new ArrayList<>();
            for (I dividedInput : dividedInputs) { // Create new generic tasks for all new inputs
                subTasks.add(new GenericDaCTask<>(dividedInput, divider, conquerer, executor));
            }
            invokeAll(subTasks); // Run all subtasks
            Collection<O> conquerableResults = new ArrayList<>();
            for (GenericDaCTask<I,O> joinableSubTask : subTasks) {
                O result = joinableSubTask.join();
                if (result != null) { // Treat null as RecursiveAction as opposed to RecursiveTask
                    conquerableResults.add(result);
                }
            }
            return conquerer.conquer(conquerableResults);
        }
    }
}
