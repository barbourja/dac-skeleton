package org.inf.ed.ac.uk.skeleton;

/**
 * Responsible for the 'base case' computation of a sufficiently
 * small input as part of a parallel divide-and-conquer algorithm.
 *
 * @param <I> input type of divide-and-conquer algorithm
 * @param <O> output type of divide-and-conquer algorithm
 */
public abstract class Executor<I,O> {

    public Executor(){}

    /**
     * Should perform the base case computation on a given input
     * @param input input to perform computation on
     * @return result of computation
     */
    public abstract O execute(I input);
}
