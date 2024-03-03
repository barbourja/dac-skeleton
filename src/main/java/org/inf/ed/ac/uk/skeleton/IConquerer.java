package org.inf.ed.ac.uk.skeleton;

import java.util.ConcurrentModificationException;

public interface IConquerer<O> {
    O conquer(Iterable<O> outputsToConquer) throws ConcurrentModificationException;
}
