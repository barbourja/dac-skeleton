package org.inf.ed.ac.uk.tests.mergesort;

import org.inf.ed.ac.uk.skeleton.ConcreteConquerer;
import org.inf.ed.ac.uk.skeleton.ConcreteDivider;
import org.inf.ed.ac.uk.skeleton.ConcreteExecutor;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.*;

import static java.lang.Math.*;


public class MergeSortExample {

    public static class MergeSortDivider extends ConcreteDivider<ArrayView> {
        private int DIVISION_THRESHOLD;
        public MergeSortDivider(int divisionThreshold) {
            super();
            this.DIVISION_THRESHOLD = divisionThreshold;
        }

        @Override
        public boolean canDivide(ArrayView input) {
            return input.size() > DIVISION_THRESHOLD;
        }

        @Override
        protected Iterable<ArrayView> divisionProcedure(ArrayView input) {
            int midPoint = input.size() / 2;
            return List.of(new ArrayView(input, 0, midPoint), new ArrayView(input, midPoint, input.size()));
        }

        public boolean setParallelismCutOff(int newCutOff) {
            if (newCutOff < 1) {
                return false;
            }
            DIVISION_THRESHOLD = newCutOff;
            return true;
        }
    }

    public static class MergeSortConquerer extends ConcreteConquerer<ArrayView> {

        public MergeSortConquerer() {
            super();
        }

        @Override
        public ArrayView conquer(Iterable<ArrayView> outputsToConquer) {
            if (outputsToConquer.spliterator().getExactSizeIfKnown() != 2) {
                throw new RuntimeException("Number of sorted lists to combine should be 2!");
            }
            Iterator<ArrayView> outputsIterator = outputsToConquer.iterator();
            ArrayView list1 = outputsIterator.next();
            ArrayView list2 = outputsIterator.next();

            Integer[] mergedList = new Integer[list1.size() + list2.size()];
            int i = 0, j = 0, k = 0;
            while (i < list1.size() || j < list2.size()) {
                if (i < list1.size() && j < list2.size()) {
                    if (list1.get(i) <= list2.get(j)) {
                        mergedList[k] = list1.get(i);
                        i++;
                    }
                    else {
                        mergedList[k] = list2.get(j);
                        j++;
                    }
                }
                else if (i < list1.size()) {
                    mergedList[k] = list1.get(i);
                    i++;
                }
                else {
                    mergedList[k] = list2.get(j);
                    j++;
                }
                k++;
            }
            return new ArrayView(mergedList, 0, mergedList.length);
        }
    }

    public static class SequentialMergeSortExecutor extends ConcreteExecutor<ArrayView, ArrayView> {
        private final int BASE_CASE_SIZE;
        public SequentialMergeSortExecutor(int baseCaseSize) {
            this.BASE_CASE_SIZE = baseCaseSize;
        }
        private ArrayView insertionSort(ArrayView input) {
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
        public ArrayView execute(ArrayView input) { // Sequential MergeSort
            ConcreteDivider<ArrayView> divider = new MergeSortDivider(BASE_CASE_SIZE); // DRY principle - using divider implementation for sequential
            Iterable<ArrayView> dividedInputs = divider.divide(input);

            boolean baseReached = dividedInputs.spliterator().getExactSizeIfKnown() == 1; // If divide returns singleton we have reached the base case size
            Iterator<ArrayView> dividedInputsIterator = dividedInputs.iterator();
            if (baseReached) {
                return insertionSort(dividedInputs.iterator().next());
            }
            else {
                ConcreteConquerer<ArrayView> conquerer = new MergeSortConquerer(); // DRY principle - using conquerer implementation for merging lists
                ArrayView list1 = dividedInputsIterator.next();
                ArrayView list2 = dividedInputsIterator.next();
                return conquerer.conquer(List.of(execute(list1), execute(list2)));
            }
        }
    }

    public void run() {
        final int PARALLELISM = 16;
        DaCSkeleton<ArrayView, ArrayView> myMergeSortDaCSkeleton = new DaCSkeleton<>(
                PARALLELISM,
                new SequentialMergeSortExecutor(20),
                new MergeSortDivider(100),
                new MergeSortConquerer()
        );

        // Generate test input and input to verify skeleton result against
        int inputSize = 100000;
        Integer[] testList = new Integer[inputSize];
        Integer[] verifyList = new Integer[inputSize];
        Random rand = new Random();
        for (int i = 0; i < inputSize; i++) {
            int randInt = rand.nextInt();
            testList[i] = randInt;
            verifyList[i] = randInt;
        }
        ArrayView skeletonResult = myMergeSortDaCSkeleton.execute(new ArrayView(testList, 0, testList.length));
        Integer[] result = skeletonResult.getBaseArray();
        Arrays.sort(verifyList); // Sort the verification list using Java sort
        System.out.println(Arrays.equals(result, verifyList)); // Check skeleton output matches sorted verification list
    }

    public void testParallelism() {
        final int INITIAL_PARALLELISM = 16;
        final int SEQ_MIN_SIZE = 8;
        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
        final int N = 23;
        int inputSize = (int) pow(2, N);

        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        DaCSkeleton<ArrayView, ArrayView> myMergeSortDaCSkeleton = new DaCSkeleton<>(
                INITIAL_PARALLELISM,
                new SequentialMergeSortExecutor(SEQ_MIN_SIZE),
                new MergeSortDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
                new MergeSortConquerer()
        );

        MergeSortTest tester = new MergeSortTest(10, 2);
        tester.testVaryingParallelism(myMergeSortDaCSkeleton, inputSize, parallelismValues, true);

    }

    public void testMinSize() {
        final int PARALLELISM = 2048;
        final int SEQ_MIN_SIZE = 8;
        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
        final int N = 23;
        int inputSize = (int) pow(2, N);
        DaCSkeleton<ArrayView, ArrayView> myMergeSortDaCSkeleton = new DaCSkeleton<>(
                PARALLELISM,
                new SequentialMergeSortExecutor(SEQ_MIN_SIZE),
                new MergeSortDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
                new MergeSortConquerer()
        );

        int maxLevelReached = (int) ceil(log(PARALLELISM) / log(2));
        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));
        Integer[] minSizeValues = new Integer[7];
        int currMinSize = minimumMinSize;
        for (int i = 0; i < minSizeValues.length; i++) {
            minSizeValues[i] = currMinSize;
            currMinSize = currMinSize * 2;
        }
        MergeSortTest tester = new MergeSortTest(10, 2);
        tester.testVaryingMinSize(myMergeSortDaCSkeleton, inputSize, PARALLELISM, minSizeValues, true);
    }
}