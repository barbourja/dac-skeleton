package org.inf.ed.ac.uk.tests.mergesort;

import org.inf.ed.ac.uk.skeleton.ConcreteConquerer;
import org.inf.ed.ac.uk.skeleton.ConcreteDivider;
import org.inf.ed.ac.uk.skeleton.ConcreteExecutor;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.*;


public class MergeSortExample {

    public static class MergeSortDivider extends ConcreteDivider<List<Integer>> {
        private int DIVISION_THRESHOLD;
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

        public boolean setParallelismCutOff(int newCutOff) {
            if (newCutOff < 1) {
                return false;
            }
            DIVISION_THRESHOLD = newCutOff;
            return true;
        }
    }

    public static class MergeSortConquerer extends ConcreteConquerer<List<Integer>> {

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

    public static class SequentialMergeSortExecutor extends ConcreteExecutor<List<Integer>, List<Integer>> {
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
            ConcreteDivider<List<Integer>> divider = new MergeSortDivider(BASE_CASE_SIZE); // DRY principle - using divider implementation for sequential
            Iterable<List<Integer>> dividedInputs = divider.divide(input);

            boolean baseReached = dividedInputs.spliterator().getExactSizeIfKnown() == 1; // If divide returns singleton we have reached the base case size
            Iterator<List<Integer>> dividedInputsIterator = dividedInputs.iterator();
            if (baseReached) {
                return insertionSort(dividedInputs.iterator().next());
            }
            else {
                ConcreteConquerer<List<Integer>> conquerer = new MergeSortConquerer(); // DRY principle - using conquerer implementation for merging lists
                List<Integer> list1 = dividedInputsIterator.next();
                List<Integer> list2 = dividedInputsIterator.next();
                return conquerer.conquer(List.of(execute(list1), execute(list2)));
            }
        }
    }

    public void run() {
        final int PARALLELISM = 16;
        DaCSkeleton<List<Integer>, List<Integer>> myMergeSortDaCSkeleton = new DaCSkeleton<>(
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
        List<Integer> skeletonResult = myMergeSortDaCSkeleton.execute(testList);
        Collections.sort(testList);
        System.out.println(testList.equals(skeletonResult));
    }

    public void testParallelism() {
        final int INITIAL_PARALLELISM = 16;
        final int SEQ_MIN_SIZE = 8;
        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
        final int N = 23;
        int inputSize = (int) Math.pow(2, N);

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        DaCSkeleton<List<Integer>, List<Integer>> myMergeSortDaCSkeleton = new DaCSkeleton<>(
                INITIAL_PARALLELISM,
                new SequentialMergeSortExecutor(SEQ_MIN_SIZE),
                new MergeSortDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
                new MergeSortConquerer()
        );

        MergeSortTest tester = new MergeSortTest(10, 2);
        tester.testVaryingParallelism(myMergeSortDaCSkeleton, inputSize, parallelismValues, true);

    }

//    public void testMinSize() {
//        final int PARALLELISM = 128;
//        final int SEQ_MIN_SIZE = 16;
//        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
//        final int N = 11;
//        DaCSkeleton<StrassensInput, Matrix> myStrassensDaCSkeleton = new DaCSkeleton<>(
//                PARALLELISM,
//                new StrassensExample.SequentialStrassensExecutor(SEQ_MIN_SIZE),
//                new StrassensExample.StrassensDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
//                new StrassensExample.StrassensConquerer()
//        );
//        int inputSize = (int) pow(2, N);
//        int maxLevelReached = (int) ceil(log(PARALLELISM) / log(2));
//        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));
//        Integer[] minSizeValues = new Integer[7];
//        int currMinSize = minimumMinSize;
//        for (int i = 0; i < minSizeValues.length; i++) {
//            minSizeValues[i] = currMinSize;
//            currMinSize = currMinSize * 2;
//        }
//
//        StrassensSkeletonTest tester = new StrassensSkeletonTest(10, 2);
//        tester.testVaryingMinSize(myStrassensDaCSkeleton, inputSize, PARALLELISM, minSizeValues, true);
//    }
}