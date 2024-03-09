package org.inf.ed.ac.uk.tests.mergesort;

import java.lang.reflect.Array;

/**
 * Allows you to interact with a slice of a given array as
 * if it was its own independent array [startIndex, endIndex)
 */
public class ArrayView {
    private final Integer[] BASE_ARRAY;
    private final int START_INDEX;
    private final int END_INDEX;

    public ArrayView(Integer[] baseArray, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > baseArray.length || startIndex >= endIndex) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.BASE_ARRAY = baseArray;
        this.START_INDEX = startIndex;
        this.END_INDEX = endIndex;
    }

    public ArrayView(ArrayView view, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > view.size() || startIndex >= endIndex) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.BASE_ARRAY = view.getBaseArray();
        this.START_INDEX = view.getStartIndex() + startIndex;
        this.END_INDEX = view.getStartIndex() + endIndex;
    }

    public int get(int index) {
        if (START_INDEX + index >= END_INDEX) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return BASE_ARRAY[START_INDEX + index];
    }

    public void set(int index, Integer value) {
        if (START_INDEX + index >= END_INDEX) {
            throw new ArrayIndexOutOfBoundsException();
        }
        BASE_ARRAY[START_INDEX + index] = value;
    }

    public ArrayView concat(ArrayView other) {
        Integer[] concatenatedArray = new Integer[this.size() + other.size()];
        for (int i = 0; i < this.size(); i++) {
            concatenatedArray[i] = this.get(i);
        }
        for (int i = this.size(); i < this.size() + other.size(); i++) {
            concatenatedArray[i] = other.get(i - this.size());
        }
        return new ArrayView(concatenatedArray, 0, concatenatedArray.length);
    }

    public int size() {
        return END_INDEX - START_INDEX;
    }

    public Integer[] getBaseArray() {
        return BASE_ARRAY;
    }

    public int getStartIndex() {
        return START_INDEX;
    }

    public int getEndIndex() {
        return END_INDEX;
    }

}
