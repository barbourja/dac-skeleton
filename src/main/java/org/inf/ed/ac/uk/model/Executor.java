package org.inf.ed.ac.uk.model;

public abstract class Executor<I,O> {

    public Executor(){}

    /**
     * Should perform the base case computation on a given input
     * @param input input to perform computation with
     * @return result of computation
     */
    public abstract O execute(I input);
}
