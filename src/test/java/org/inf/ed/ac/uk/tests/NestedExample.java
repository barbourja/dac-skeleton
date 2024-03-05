package org.inf.ed.ac.uk.tests;

import org.inf.ed.ac.uk.skeleton.ConcreteConquerer;
import org.inf.ed.ac.uk.skeleton.ConcreteDivider;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple example of a nested skeleton, it will partition an array and sort the partitions via
 * the pre-existing MergeSort parallel skeleton.
 */
public class NestedExample {

    private class PartitionDivider extends ConcreteDivider<List<Integer>> {
        private final int MAX_PARTITION_SIZE;
        public PartitionDivider(int maxPartitionSize) {
            this.MAX_PARTITION_SIZE = maxPartitionSize;
        }
        @Override
        public boolean canDivide(List<Integer> input) {
            return input.size() > MAX_PARTITION_SIZE;
        }

        @Override
        protected Iterable<List<Integer>> divisionProcedure(List<Integer> input) {
            return List.of(input.subList(0, input.size() / 2), input.subList(input.size() / 2, input.size()));
        }
    }

    private class PartitionConquerer extends ConcreteConquerer<List<Integer>> {

        public PartitionConquerer() {
            super();
        }
        @Override
        public List<Integer> conquer(Iterable<List<Integer>> outputsToConquer) throws ConcurrentModificationException { // Concatenates the two lists to return a single list
            if (outputsToConquer.spliterator().getExactSizeIfKnown() != 2) {
                throw new RuntimeException("Expected 2 lists to conquer!");
            }
            Iterator<List<Integer>> listIterator = outputsToConquer.iterator();
            List<Integer> list1 = listIterator.next();
            List<Integer> list2 = listIterator.next();
            return Stream.of(list1, list2).flatMap(List::stream).collect(Collectors.toList());
        }
    }

    public void run() {
        final int PARENT_PARALLELISM = 16;
        final int CHILD_PARALLELISM = 4;
        final int LIST_INPUT_SIZE = 200000;
        final int MAX_PARTITION_SIZE = LIST_INPUT_SIZE / 2;
        DaCSkeleton<List<Integer>, List<Integer>> myMergeSortSkeleton = new DaCSkeleton<>(
                CHILD_PARALLELISM,
                new MergeSortExample.SequentialMergeSortExecutor(30),
                new MergeSortExample.MergeSortDivider(MAX_PARTITION_SIZE / 100),
                new MergeSortExample.MergeSortConquerer()
        );
        DaCSkeleton<List<Integer>, List<Integer>> myNestedSkeleton = new DaCSkeleton<>(
                PARENT_PARALLELISM,
                myMergeSortSkeleton,
                new PartitionDivider(MAX_PARTITION_SIZE),
                new PartitionConquerer()
        );

        List<Integer> testInput = new ArrayList<>();
        List<Integer> verificationInput = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 200000; i++) {
            int randInt = rand.nextInt();
            testInput.add(randInt);
            verificationInput.add(randInt);
        }
        List<Integer> output = myNestedSkeleton.execute(testInput);
        Collections.sort(verificationInput.subList(0, verificationInput.size()/2));
        Collections.sort(verificationInput.subList(verificationInput.size()/2,verificationInput.size()));
        System.out.println(output.equals(verificationInput));
    }
}
