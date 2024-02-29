package org.inf.ed.ac.uk.model;

public abstract class Conquerer<O> {

    /**
     * Combines multiple outputs into a single output
     * @param outputsToConquer iterable store of outputs to combine
     * @return single output result of combination
     */
    abstract O conquer(Iterable<O> outputsToConquer);
}
