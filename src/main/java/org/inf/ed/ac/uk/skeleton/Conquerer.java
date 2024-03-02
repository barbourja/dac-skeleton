package org.inf.ed.ac.uk.skeleton;

/**
 * Responsible for the recombination of subtask outputs as part of the 'conquer' step of a
 * parallel divide-and-conquer algorithm.
 *
 * Note: the methods of the class *will* be called in parallel so the client is responsible for
 * synchronization of shared global state!
 * @param <O> output type of divide-and-conquer algorithm
 */
public abstract class Conquerer<O> {

    public Conquerer(){}

    /**
     * Combines multiple outputs into a single output
     * @param outputsToConquer iterable collection of outputs to combine
     * @return single output result of combination
     */
    public abstract O conquer(Iterable<O> outputsToConquer);
}
