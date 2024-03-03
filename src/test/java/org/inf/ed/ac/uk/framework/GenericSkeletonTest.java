package org.inf.ed.ac.uk.framework;

import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.ArrayList;

import static java.lang.Math.*;

public abstract class GenericSkeletonTest {

    protected final int NUM_RUNS_PER_INPUT;
    protected final int INPUT_DIVISION_FACTOR;

    public GenericSkeletonTest(int numRunsPerInput, int inputDivisionFactor) {
        this.NUM_RUNS_PER_INPUT = numRunsPerInput;
        this.INPUT_DIVISION_FACTOR = inputDivisionFactor;
    }

    public Long[] testVaryingParallelism(DaCSkeleton skeletonUnderTest, Integer inputSize,
                                         Integer[] valuesToTest, boolean fullPrinting) {
        System.out.println(skeletonUnderTest.toString() + "\n Input size: " + inputSize
        + " - Varying parallelism");

        ArrayList<Long> runtimes = new ArrayList<>();
        for (int parallelism : valuesToTest) {
            int minSize;
            int maxLevelReached = (int) ceil(log((INPUT_DIVISION_FACTOR - 1) * parallelism) / log(INPUT_DIVISION_FACTOR));
            minSize = (int) ceil(inputSize / pow(INPUT_DIVISION_FACTOR, maxLevelReached));
            long runtime = testInput(skeletonUnderTest, inputSize, minSize, parallelism, fullPrinting);
            runtimes.add(runtime);
        }

        // dump csv
        System.out.print("  ");
        for (int i = 0; i < valuesToTest.length - 1; i++) {
            System.out.print(valuesToTest[i] + ",");
        }
        System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");

        System.out.print("  ");
        for (int i = 0; i < runtimes.size() - 1; i++) {
            System.out.print(runtimes.get(i) + ",");
        }
        System.out.print(runtimes.get(runtimes.size() - 1) + "\n");

        return runtimes.toArray(new Long[0]);
    }

    public Long[] testVaryingMinSize(DaCSkeleton skeletonUnderTest, Integer inputSize,
                                     Integer parallelism, Integer[] valuesToTest, boolean fullPrinting) {
        skeletonUnderTest.changeParallelism(parallelism);
        System.out.println(skeletonUnderTest.toString() + "\n Input size: " + inputSize
                + " - Varying parallelism cut-off size");

        ArrayList<Long> runtimes = new ArrayList<>();
        for (int minSize : valuesToTest) {
            long runtime = testInput(skeletonUnderTest, inputSize, minSize, parallelism, fullPrinting);
            runtimes.add(runtime);
        }

        // dump csv
        System.out.print("  ");
        for (int i = 0; i < valuesToTest.length - 1; i++) {
            System.out.print(valuesToTest[i] + ",");
        }
        System.out.print(valuesToTest[valuesToTest.length - 1] + "\n");

        System.out.print("  ");
        for (int i = 0; i < runtimes.size() - 1; i++) {
            System.out.print(runtimes.get(i) + ",");
        }
        System.out.print(runtimes.get(runtimes.size() - 1) + "\n");

        return runtimes.toArray(new Long[0]);
    }

    public abstract long testInput(DaCSkeleton skeletonUnderTest, Integer inputSize, Integer minSize,
                                   Integer parallelism, boolean fullPrinting);
}
