package org.inf.ed.ac.uk.skeleton;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Responsible for the 'divide' step of a parallel divide-and-conquer algorithm.
 * Divides an input into multiple smaller inputs that will be used as parameters
 * when creating/calling corresponding subtasks.
 *
 * Note: the methods of the class *will* be called in parallel so the client is responsible for
 * synchronization of shared global state!
 * @param <I> input type of divide-and-conquer algorithm
 */
public abstract class GenericDivider<I> implements IDivider<I>{

    public GenericDivider(){}
    public abstract boolean canDivide(I input);
    protected abstract Iterable<I> divisionProcedure(I input);

    /**
     * Returns an iterable collection of divided inputs. If the output cannot be further
     * divided it will return a singleton iterable.
     * @return 1..N valid inputs
     */
    public Iterable<I> divide(I input) throws ConcurrentModificationException {
        if (canDivide(input)) {
            return divisionProcedure(input); // Return the divided input
        }
        else {
            ArrayList<I> singleton = new ArrayList<>();
            singleton.add(input);
            return singleton; // Return original (undivided) input
        }
    }
}
