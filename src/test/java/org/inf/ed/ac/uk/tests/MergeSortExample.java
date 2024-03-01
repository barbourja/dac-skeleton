package org.inf.ed.ac.uk.tests;

import org.inf.ed.ac.uk.model.Conquerer;
import org.inf.ed.ac.uk.model.Divider;
import org.inf.ed.ac.uk.model.Executor;
import skeleton.Skeleton;

import java.util.*;

public class MergeSortExample {

    private class MergeSortDivider extends Divider<List<Integer>> {
        private final int DIVISION_THRESHOLD;
        public MergeSortDivider(int divisionThreshold) {
            super();
            this.DIVISION_THRESHOLD = divisionThreshold;
        }

        @Override
        public boolean canDivide(List<Integer> input) {
            return input.size() > DIVISION_THRESHOLD;
        }

        @Override
        protected Iterable<List<Integer>> divisionProcedure(List<Integer> input) {
            int midPoint = input.size() / 2;
            return List.of(input.subList(0, midPoint), input.subList(midPoint, input.size()));
        }
    }

    private class MergeSortConquerer extends Conquerer<List<Integer>> {

        public MergeSortConquerer() {
            super();
        }

        @Override
        public List<Integer> conquer(Iterable<List<Integer>> outputsToConquer) {
            if (outputsToConquer.spliterator().getExactSizeIfKnown() != 2) {
                throw new RuntimeException("Number of sorted lists to combine should be 2!");
            }
            Iterator<List<Integer>> outputsIterator = outputsToConquer.iterator();
            List<Integer> list1 = outputsIterator.next();
            List<Integer> list2 = outputsIterator.next();

            List<Integer> mergedList = new ArrayList<>();
            int i = 0, j = 0;
            while (i < list1.size() || j < list2.size()) {
                if (i < list1.size() && j < list2.size()) {
                    if (list1.get(i) <= list2.get(j)) {
                        mergedList.add(list1.get(i));
                        i++;
                    }
                    else {
                        mergedList.add(list2.get(j));
                        j++;
                    }
                }
                else if (i < list1.size()) {
                    mergedList.add(list1.get(i));
                    i++;
                }
                else {
                    mergedList.add(list2.get(j));
                    j++;
                }
            }
            return mergedList;
        }
    }

    private class SequentialMergeSortExecutor extends Executor<List<Integer>, List<Integer>> {
        private final int BASE_CASE_SIZE;
        public SequentialMergeSortExecutor(int baseCaseSize) {
            this.BASE_CASE_SIZE = baseCaseSize;
        }
        private List<Integer> insertionSort(List<Integer> input) {
            if (input.size() < 2) {
                return input;
            }
            else {
                for (int currIndex = 1; currIndex < input.size(); currIndex++) {
                    while (currIndex > 0 && input.get(currIndex - 1) > input.get(currIndex)) {
                        Integer tmp = input.get(currIndex);
                        input.set(currIndex, input.get(currIndex - 1));
                        input.set(currIndex - 1, tmp);
                        currIndex--;
                    }
                }
            }
            return input;
        }
        public List<Integer> execute(List<Integer> input) { // Sequential MergeSort
            Divider<List<Integer>> divider = new MergeSortDivider(BASE_CASE_SIZE); // DRY principle - using divider implementation for sequential
            Iterable<List<Integer>> dividedInputs = divider.divide(input);

            boolean baseReached = dividedInputs.spliterator().getExactSizeIfKnown() == 1; // If divide returns singleton we have reached the base case size
            Iterator<List<Integer>> dividedInputsIterator = dividedInputs.iterator();
            if (baseReached) {
                return insertionSort(dividedInputs.iterator().next());
            }
            else {
                Conquerer<List<Integer>> conquerer = new MergeSortConquerer(); // DRY principle - using conquerer implementation for merging lists
                List<Integer> list1 = dividedInputsIterator.next();
                List<Integer> list2 = dividedInputsIterator.next();
                return conquerer.conquer(List.of(execute(list1), execute(list2)));
            }
        }
    }

    public void run() {
        final int PARALLELISM = 16;
        Skeleton<List<Integer>, List<Integer>> myMergeSortSkeleton = new Skeleton<>(
                PARALLELISM,
                new SequentialMergeSortExecutor(20),
                new MergeSortDivider(100),
                new MergeSortConquerer()
        );


        List<Integer> testList = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 100000; i++) {
            testList.add(rand.nextInt(1000000));
        }
        List<Integer> skeletonResult = myMergeSortSkeleton.execute(testList);
        Collections.sort(testList);
        System.out.println(testList.equals(skeletonResult));
    }
}