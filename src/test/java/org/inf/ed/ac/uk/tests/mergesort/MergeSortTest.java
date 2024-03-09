package org.inf.ed.ac.uk.tests.mergesort;

import org.inf.ed.ac.uk.framework.GenericSkeletonTest;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.ArrayList;
import java.util.Random;

public class MergeSortTest extends GenericSkeletonTest {
    private final long SEED = 1405214844;
    private final Random rand = new Random();
    public MergeSortTest(int numberRunsPerInput, int inputDivisionFactor) {
        super(numberRunsPerInput, inputDivisionFactor);
    }

    @Override
    public long testInput(DaCSkeleton skeletonUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting) {
        ArrayList<Long> runtimes = new ArrayList<>();
        rand.setSeed(SEED);
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            Integer[] input = new Integer[inputSize];
            for (int j = 0; j < inputSize; j++) {
                input[j] = rand.nextInt();
            }
            runtimes.add(runSimpleTest(skeletonUnderTest, new ArrayView(input, 0, input.length), minSize, parallelism));
        }
        long avgRuntime = Math.round(runtimes.stream().mapToDouble(Double::valueOf).average().getAsDouble());
        if (fullPrinting) {
            System.out.print("  ");
            System.out.print("Min. Size=" + minSize + " | Parallelism=" + parallelism + " | Runtimes: ");
            for (int i = 0; i < runtimes.size() - 1; i++) {
                System.out.print(runtimes.get(i) + ",");
            }
            System.out.print(runtimes.get(runtimes.size() - 1) + " | Avg.: " + avgRuntime + "\n");
        }
        return avgRuntime;
    }

    private long runSimpleTest(DaCSkeleton<ArrayView, ArrayView> skeletonUnderTest, ArrayView input, Integer minSize, Integer parallelism) {
        if (minSize != null) {
            MergeSortExample.MergeSortDivider divider = (MergeSortExample.MergeSortDivider) skeletonUnderTest.getDivider();
            divider.setParallelismCutOff(minSize);
        }
        if (parallelism != null) {
            skeletonUnderTest.changeParallelism(parallelism);
        }
        long startTime, elapsedTime;
        startTime = System.nanoTime(); // ns
        skeletonUnderTest.execute(input);
        elapsedTime = (System.nanoTime() - startTime) / 1000000; // ms
        return elapsedTime;
    }
}
