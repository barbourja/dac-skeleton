package org.inf.ed.ac.uk.tests.strassens;

import org.inf.ed.ac.uk.skeleton.GenericConquerer;
import org.inf.ed.ac.uk.skeleton.GenericDivider;
import org.inf.ed.ac.uk.skeleton.GenericExecutor;
import org.inf.ed.ac.uk.skeleton.DaCSkeleton;

import java.util.*;

import static java.lang.Math.*;

public class StrassensExample {

    public class StrassensDivider extends GenericDivider<StrassensInput> {
        private int DIMENSION_DIVISION_THRESHOLD;
        public StrassensDivider(int dimensionDivisionThreshold) {
            this.DIMENSION_DIVISION_THRESHOLD = dimensionDivisionThreshold;
        }
        @Override
        public boolean canDivide(StrassensInput inputMatrices) {
            return inputMatrices.getDim() > DIMENSION_DIVISION_THRESHOLD;
        }

        @Override
        protected Iterable<StrassensInput> divisionProcedure(StrassensInput inputMatrices) {
            Matrix[] mat1Split = inputMatrices.getMat1().quadrantSplit();
            Matrix[] mat2Split = inputMatrices.getMat2().quadrantSplit();
            Matrix[] resQuadrants = inputMatrices.getRes().quadrantSplit();

            Matrix[] workingQuadrants = new Matrix[11];
            for (int i = 0; i < workingQuadrants.length; i++) {
                workingQuadrants[i] = new ConcreteMatrix(new int[inputMatrices.getDim()/2][inputMatrices.getDim()/2]);
            }

            // Very similar to previous raw ForkJoin implementation
            // Constructing inputs to Strassen's subtasks based on Winograd's form (for greater memory efficiency at expense
            // of some readability/clarity)
            // n.b. essentially the idea is divide our input logically into 7 different inputs based on
            // mathematical formula, not required to understand exactly the motivation just trust that it works ;)
            StrassensInput task0Input = new StrassensInput(
                    mat1Split[2].sub(mat1Split[0], workingQuadrants[3]),
                    mat2Split[1].sub(mat2Split[3], workingQuadrants[4]),
                    workingQuadrants[0]
            );
            StrassensInput task1Input = new StrassensInput(
                    mat1Split[2].add(mat1Split[3], workingQuadrants[5]),
                    mat2Split[1].sub(mat2Split[0], workingQuadrants[6]),
                    workingQuadrants[1]
            );
            StrassensInput task2Input = new StrassensInput(
                    mat1Split[0],
                    mat2Split[0],
                    workingQuadrants[2]
            );
            StrassensInput task3Input = new StrassensInput(
                    workingQuadrants[5].sub(mat1Split[0], workingQuadrants[7]),
                    mat2Split[0].add(mat2Split[3], workingQuadrants[8])
                            .subInPlace(mat2Split[1]),
                    resQuadrants[3]
            );
            StrassensInput task4Input = new StrassensInput(
                    mat1Split[1],
                    mat2Split[2],
                    resQuadrants[0]
            );
            StrassensInput task5Input = new StrassensInput(
                    mat1Split[0].add(mat1Split[1], workingQuadrants[9])
                            .subInPlace(mat1Split[2])
                            .subInPlace(mat1Split[3]),
                    mat2Split[3],
                    resQuadrants[1]
            );
            StrassensInput task6Input = new StrassensInput(
                    mat1Split[3],
                    mat2Split[2].add(workingQuadrants[6], workingQuadrants[10])
                            .subInPlace(mat2Split[3]),
                    resQuadrants[2]
            );
            return List.of(task0Input, task1Input, task2Input, task3Input, task4Input, task5Input, task6Input);
        }
        public boolean setParallelismCutOff(int newCutOff) {
            if (newCutOff < 1) {
                return false;
            }
            DIMENSION_DIVISION_THRESHOLD = newCutOff;
            return true;
        }
    }

    public class StrassensConquerer extends GenericConquerer<Matrix> {
        @Override
        public Matrix conquer(Iterable<Matrix> outputsToConquer) {
            if (outputsToConquer.spliterator().getExactSizeIfKnown() != 7) {
                throw new RuntimeException("Must receive exactly 7 matrices to allow recombination step!");
            }
            Matrix[] matrixResults = new Matrix[7];
            Iterator<Matrix> matrixOutputIterator = outputsToConquer.iterator();
            int i = 0;
            while (matrixOutputIterator.hasNext()) {
                matrixResults[i] = matrixOutputIterator.next();
                i++;
            }
            Matrix p1 = matrixResults[2];
            Matrix p2 = matrixResults[3];
            Matrix w = p1.add(p2, p2);

            Matrix p3 = matrixResults[4];
            Matrix res0 = p3.add(p1, p3);

            Matrix v = matrixResults[1];
            Matrix p4 = matrixResults[5];
            Matrix res1 = p4.add(w, p4).add(v, p4);

            Matrix u = matrixResults[0];
            Matrix p5 = matrixResults[6];
            Matrix res2 = p5.add(w, p5).add(u, p5);

            Matrix res3 = w.add(u, w).add(v, w);

            Matrix res = res0.getParent();
            return res;
        }
    }

    public class SequentialStrassensExecutor extends GenericExecutor<StrassensInput, Matrix> {

        private final int MIN_MATRIX_DIMENSION;
        public SequentialStrassensExecutor(int minMatrixDimension) {
            this.MIN_MATRIX_DIMENSION = minMatrixDimension;
        }

        @Override
        public Matrix execute(StrassensInput input) {
            if (input.getDim() <= MIN_MATRIX_DIMENSION) {
                return input.getMat1().mult(input.getMat2(), input.getRes());
            }
            else { // Perform DaC (sequentially)
                // We don't reuse the parallel divider as we can be more space efficient sequentially :)
                Matrix[] mat1Split = input.getMat1().quadrantSplit();
                Matrix[] mat2Split = input.getMat2().quadrantSplit();
                Matrix[] resQuadrants = input.getRes().quadrantSplit();

                Matrix[] workingQuadrants = new Matrix[4];
                for (int i = 0; i < workingQuadrants.length; i++) {
                    workingQuadrants[i] = new ConcreteMatrix(new int[input.getDim()/2][input.getDim()/2]);
                }

                Matrix u = execute(new StrassensInput(
                        mat1Split[2].sub(mat1Split[0], resQuadrants[0]),
                        mat2Split[1].sub(mat2Split[3], resQuadrants[1]),
                        workingQuadrants[0]
                ));
                Matrix v = execute(new StrassensInput(
                        mat1Split[2].add(mat1Split[3], resQuadrants[2]),
                        mat2Split[1].sub(mat2Split[0], workingQuadrants[3]),
                        workingQuadrants[1]
                ));
                Matrix p1 = execute(new StrassensInput(
                        mat1Split[0],
                        mat2Split[0],
                        workingQuadrants[2]
                ));
                Matrix p2 = execute(new StrassensInput(
                        resQuadrants[2].sub(mat1Split[0], resQuadrants[2]),
                        mat2Split[0].add(mat2Split[3], resQuadrants[1])
                                .subInPlace(mat2Split[1]),
                        resQuadrants[3]
                ));
                Matrix p3 = execute(new StrassensInput(
                        mat1Split[1],
                        mat2Split[2],
                        resQuadrants[0]
                ));
                Matrix p4 = execute(new StrassensInput(
                        mat1Split[0].add(mat1Split[1], resQuadrants[2])
                                .subInPlace(mat1Split[2])
                                .subInPlace(mat1Split[3]),
                        mat2Split[3],
                        resQuadrants[1]
                ));
                Matrix p5 = execute(new StrassensInput(
                        mat1Split[3],
                        mat2Split[2].add(workingQuadrants[3], workingQuadrants[3])
                                .subInPlace(mat2Split[3]),
                        resQuadrants[2]
                ));

                // Combine results
                Matrix w = p1.add(p2, p2);
                Matrix res0 = p3.add(p1, p3);
                Matrix res1 = p4.add(w, p4).add(v, p4);
                Matrix res2 = p5.add(w, p5).add(u, p5);
                Matrix res3 = w.add(u, w).add(v, w);

                Matrix res = res0.getParent();
                return res;
            }
        }
    }
    public void run() {
        final int INITIAL_PARALLELISM = 16;
        DaCSkeleton<StrassensInput, Matrix> myStrassensDaCSkeleton = new DaCSkeleton<>(
                INITIAL_PARALLELISM,
                new SequentialStrassensExecutor(32),
                new StrassensDivider(128),
                new StrassensConquerer()
        );

        // Generate two input matrices to multiply
        final int INPUT_DIMENSION = 1024;
        Random rand = new Random();
        int[][] input1 = new int[INPUT_DIMENSION][INPUT_DIMENSION];
        int[][] input2 = new int[INPUT_DIMENSION][INPUT_DIMENSION];
        for (int row = 0; row < INPUT_DIMENSION; row++) {
            for (int col = 0; col < INPUT_DIMENSION; col++) {
                input1[row][col] = rand.nextInt(3);
                input2[row][col] = rand.nextInt(3);
            }
        }
        Matrix skeletonRes = new ConcreteMatrix(new int[INPUT_DIMENSION][INPUT_DIMENSION]);
        Matrix directRes = new ConcreteMatrix(new int[INPUT_DIMENSION][INPUT_DIMENSION]);
        Matrix mat1 = new ConcreteMatrix(input1);
        Matrix mat2 = new ConcreteMatrix(input2);

        skeletonRes = myStrassensDaCSkeleton.execute(new StrassensInput(mat1, mat2, skeletonRes)); // Multiply using skeleton
        directRes = mat1.mult(mat2, directRes); // Naively multiply directly to check skeleton result against

        System.out.println(directRes.equals(skeletonRes)); // Check skeleton result matches naive direct multiplication

    }

    public void testParallelism() {
        final int INITIAL_PARALLELISM = 16;
        final int SEQ_MIN_SIZE = 32;
        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
        final int N = 11;
        DaCSkeleton<StrassensInput, Matrix> myStrassensDaCSkeleton = new DaCSkeleton<>(
                INITIAL_PARALLELISM,
                new SequentialStrassensExecutor(SEQ_MIN_SIZE),
                new StrassensDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
                new StrassensConquerer()
        );
        int inputSize = (int) pow(2, N);
        Integer[] parallelismValues = new Integer[]{1, 2, 4, 8, 16, 32, 64};

        StrassensSkeletonTest tester = new StrassensSkeletonTest(10, 2);
        tester.testVaryingParallelism(myStrassensDaCSkeleton, inputSize, parallelismValues, true);

    }

    public void testMinSize() {
        final int PARALLELISM = 128;
        final int SEQ_MIN_SIZE = 16;
        final int PARALLELISM_MIN_SIZE_PLACEHOLDER = 1;
        final int N = 11;
        DaCSkeleton<StrassensInput, Matrix> myStrassensDaCSkeleton = new DaCSkeleton<>(
                PARALLELISM,
                new SequentialStrassensExecutor(SEQ_MIN_SIZE),
                new StrassensDivider(PARALLELISM_MIN_SIZE_PLACEHOLDER),
                new StrassensConquerer()
        );
        int inputSize = (int) pow(2, N);
        int maxLevelReached = (int) ceil(log(PARALLELISM) / log(2));
        int minimumMinSize = (int) ceil(inputSize / pow(2, maxLevelReached));
        Integer[] minSizeValues = new Integer[7];
        int currMinSize = minimumMinSize;
        for (int i = 0; i < minSizeValues.length; i++) {
            minSizeValues[i] = currMinSize;
            currMinSize = currMinSize * 2;
        }

        StrassensSkeletonTest tester = new StrassensSkeletonTest(10, 2);
        tester.testVaryingMinSize(myStrassensDaCSkeleton, inputSize, PARALLELISM, minSizeValues, true);
    }
}
