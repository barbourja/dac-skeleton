package org.inf.ed.ac.uk.model;

public abstract class Executor<I,O> {
    abstract O execute(I input);
}
