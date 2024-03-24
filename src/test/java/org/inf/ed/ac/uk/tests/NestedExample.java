package org.inf.ed.ac.uk.tests;

import org.inf.ed.ac.uk.skeleton.GenericConquerer;
import org.inf.ed.ac.uk.skeleton.GenericDivider;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;
import org.inf.ed.ac.uk.tests.mergesort.ArrayView;
import org.inf.ed.ac.uk.tests.mergesort.MergeSortExample;

import java.util.*;

/**
 * Simple example of a nested skeleton, it will partition an array and sort the partitions via
 * the pre-existing MergeSort parallel skeleton.
 */
public class NestedExample {

    private class PartitionDivider extends GenericDivider<ArrayView> {
        private final int MAX_PARTITION_SIZE;
        public PartitionDivider(int maxPartitionSize) {
            this.MAX_PARTITION_SIZE = maxPartitionSize;
        }
        @Override
        public boolean canDivide(ArrayView input) {
            return input.size() > MAX_PARTITION_SIZE;
        }

        @Override
        protected Iterable<ArrayView> divisionProcedure(ArrayView input) {
            int midPoint = input.size() / 2;
            return List.of(new ArrayView(input, 0, midPoint), new ArrayView(input, midPoint, input.size()));
        }
    }

    private class PartitionConquerer extends GenericConquerer<ArrayView> {

        public PartitionConquerer() {
            super();
        }
        @Override
        public ArrayView conquer(Iterable<ArrayView> outputsToConquer) throws ConcurrentModificationException { // Concatenates the two lists to return a single list
            if (outputsToConquer.spliterator().getExactSizeIfKnown() != 2) {
                throw new RuntimeException("Expected 2 lists to conquer!");
            }
            Iterator<ArrayView> listIterator = outputsToConquer.iterator();
            ArrayView list1 = listIterator.next();
            ArrayView list2 = listIterator.next();
            return list1.concat(list2);
        }
    }

    public void run() {
        final int PARENT_PARALLELISM = 16;
        final int CHILD_PARALLELISM = 4;
        final int LIST_INPUT_SIZE = 200000;
        final int MAX_PARTITION_SIZE = LIST_INPUT_SIZE / 2;
        DaCSkeleton<ArrayView, ArrayView> myMergeSortSkeleton = new DaCSkeleton<>(
                CHILD_PARALLELISM,
                new MergeSortExample.SequentialMergeSortExecutor(30),
                new MergeSortExample.MergeSortDivider(MAX_PARTITION_SIZE / 100),
                new MergeSortExample.MergeSortConquerer()
        );
        DaCSkeleton<ArrayView, ArrayView> myNestedSkeleton = new DaCSkeleton<>(
                PARENT_PARALLELISM,
                myMergeSortSkeleton,
                new PartitionDivider(MAX_PARTITION_SIZE),
                new PartitionConquerer()
        );

        // Generate test input and input to verify skeleton result against
        Integer[] testInput = new Integer[LIST_INPUT_SIZE];
        Integer[] verificationInput = new Integer[LIST_INPUT_SIZE];
        Random rand = new Random();
        for (int i = 0; i < LIST_INPUT_SIZE; i++) {
            int randInt = rand.nextInt();
            testInput[i] = randInt;
            verificationInput[i] = randInt;
        }
        Integer[] result = myNestedSkeleton.execute(new ArrayView(testInput, 0, testInput.length)).getBaseArray();
        // Partition and sort verification input using Java sort
        Arrays.sort(verificationInput, 0, verificationInput.length / 2);
        Arrays.sort(verificationInput, verificationInput.length / 2, verificationInput.length);
        System.out.println(Arrays.equals(result, verificationInput)); // Check skeleton result matches the partitioned/sorted verification input
    }
}
