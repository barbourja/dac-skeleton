package org.inf.ed.ac.uk.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.RecursiveTask;

public class GenericDaCTask<I,O> extends RecursiveTask<O> {
    private I input;
    private Divider<I> divider;
    private Conquerer<O> conquerer;

    public GenericDaCTask(I input, Divider<I> divider, Conquerer<O> conquerer) {
        this.input = input;
        this.divider = divider;
        this.conquerer = conquerer;
    }

    @Override
    protected O compute() {
        Iterable<I> dividedInputs = divider.divide(input); // Use division logic to produce new inputs
        Iterator<I> checkableIterator = dividedInputs.iterator();
        checkableIterator.next();
        if (!checkableIterator.hasNext()) {

        }
        Collection<GenericDaCTask<I,O>> subTasks = new ArrayList<>();
        for (I dividedInput : dividedInputs) { // Create new generic tasks for all new inputs
            subTasks.add(new GenericDaCTask<I, O>(dividedInput, divider, conquerer));
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
