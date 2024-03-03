package org.inf.ed.ac.uk.tests.strassens;

import org.inf.ed.ac.uk.framework.GenericSkeletonTest;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.ArrayList;
import java.util.Random;

public class StrassensSkeletonTest extends GenericSkeletonTest {

    private final long SEED = 1994847393;
    private final Random rand = new Random();

    public StrassensSkeletonTest(int numRunsPerInput, int inputDivisionFactor) {
        super(numRunsPerInput, inputDivisionFactor);
    }

    @Override
    public long testInput(DaCSkeleton skeletonUnderTest, Integer inputSize, Integer minSize, Integer parallelism, boolean fullPrinting) {
        ArrayList<Long> runtimes = new ArrayList<>();
        rand.setSeed(SEED);
        for (int i = 0; i < NUM_RUNS_PER_INPUT; i++) {
            int[][] input1 = new int[inputSize][inputSize];
            int[][] input2 = new int[inputSize][inputSize];
            for (int row = 0; row < inputSize; row++) {
                for (int col = 0; col < inputSize; col++) {
                    input1[row][col] = rand.nextInt();
                    input2[row][col] = rand.nextInt();
                }
            }
            StrassensInput inputToTest = new StrassensInput(
                    new ConcreteMatrix(input1),
                    new ConcreteMatrix(input2),
                    new ConcreteMatrix(new int[inputSize][inputSize])
            );
            runtimes.add(runSimpleTest(skeletonUnderTest, inputToTest, minSize, parallelism));
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

    private long runSimpleTest(DaCSkeleton<StrassensInput, Matrix> skeletonUnderTest, StrassensInput input, Integer minSize, Integer parallelism) {
        if (minSize != null) {
            StrassensExample.StrassensDivider divider = (StrassensExample.StrassensDivider) skeletonUnderTest.getDivider();
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
