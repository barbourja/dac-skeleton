package org.inf.ed.ac.uk.skeleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;

/**
 * Builds upon the standard RecursiveTask provided by the ForkJoin framework.
 * Takes on the responsibility of constructing smaller subtasks and sending
 * them for execution by a ForkJoin pool. However, in order to do this,
 * the client must provide valid implementations of the abstract base classes:
 * 'GenericDivider', 'GenericConquerer', and 'GenericExecutor'.
 *
 * @param <I> input type of divide-and-conquer algorithm
 * @param <O> ouput type of divide-and-conquer algorithm
 */
class GenericDaCTask<I,O> extends RecursiveTask<O> {
    private I input;
    private final IDivider<I> DIVIDER;
    private final IConquerer<O> CONQUERER;
    private final IExecutor<I,O> EXECUTOR;

    /**
     * @param input the input to evaluate and perform computation on
     * @param divider splits up input (if required) into smaller inputs for corresponding subtasks
     * @param conquerer recombines results of subtasks into single result
     * @param executor executes 'base case' computation logic if the divider can't further divide the task
     */
    public GenericDaCTask(I input, IDivider<I> divider, IConquerer<O> conquerer, IExecutor<I,O> executor) {
        this.input = input;
        this.DIVIDER = divider;
        this.CONQUERER = conquerer;
        this.EXECUTOR = executor;
    }

    @Override
    protected O compute() {
        if (!DIVIDER.canDivide(input)) { // Base case
            O result;
            if (EXECUTOR instanceof DaCSkeleton<I,O>) { // If supplied executor is skeleton then execute its logic BUT use current skeleton's thread pool
                DaCSkeleton<I,O> nestedSkeleton = (DaCSkeleton<I, O>) EXECUTOR;
                GenericDaCTask<I,O> delegateTask = new GenericDaCTask<>(input, nestedSkeleton.getDivider(), nestedSkeleton.getConquerer(), nestedSkeleton.getExecutor());
                result = delegateTask.fork().join();
            }
            else {
                result = EXECUTOR.execute(input);
            }
            return result;
        }
        else { // Perform DaC
            Iterable<I> dividedInputs = DIVIDER.divide(input); // Use division logic to produce new inputs
            Collection<GenericDaCTask<I, O>> subTasks = new ArrayList<>();
            for (I dividedInput : dividedInputs) { // Create new generic tasks for all new inputs
                subTasks.add(new GenericDaCTask<>(dividedInput, DIVIDER, CONQUERER, EXECUTOR));
            }
            invokeAll(subTasks); // Run all subtasks
            Collection<O> conquerableResults = new ArrayList<>();
            for (GenericDaCTask<I, O> joinableSubTask : subTasks) {
                O result = joinableSubTask.join();
                if (result != null) { // Treat null as RecursiveAction as opposed to RecursiveTask
                    conquerableResults.add(result);
                }
            }
            return CONQUERER.conquer(conquerableResults);
        }
    }
}
