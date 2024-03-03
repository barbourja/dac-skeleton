package org.inf.ed.ac.uk.skeleton;

import java.util.ConcurrentModificationException;

public interface IDivider<I> {
    boolean canDivide(I input);
    Iterable<I> divide(I input) throws ConcurrentModificationException;
}
