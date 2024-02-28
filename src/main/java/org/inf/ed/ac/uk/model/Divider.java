package org.inf.ed.ac.uk.model;

import java.util.ArrayList;

public abstract class Divider<I> {

    public Divider() {}

    abstract boolean canDivide(I input);
    abstract Iterable<I> divisionProcedure(I input);

    /**
     * Returns an iterable collection of divided inputs
     * @return 1..N valid inputs
     */
    public Iterable<I> divide(I input) {
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
