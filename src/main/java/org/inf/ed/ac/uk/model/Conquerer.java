package org.inf.ed.ac.uk.model;

public interface Conquerer<O> {

    /**
     * Combines multiple outputs into a single output
     * @param outputsToConquer iterable store of outputs to combine
     * @return single output result of combination
     */
    O conquer(Iterable<O> outputsToConquer);
}
