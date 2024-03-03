package org.inf.ed.ac.uk.skeleton;

import java.util.ConcurrentModificationException;

public interface IExecutor<I,O> {
    O execute(I input) throws ConcurrentModificationException;
}
