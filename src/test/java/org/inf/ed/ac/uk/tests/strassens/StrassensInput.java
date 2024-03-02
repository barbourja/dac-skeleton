package org.inf.ed.ac.uk.tests.strassens;

/**
 * Used to batch all Strassen algorithm inputs together for more readable (less index oriented!) code
 */
public class StrassensInput {
    private final Matrix MAT_1;
    private final Matrix MAT_2;
    private final Matrix RES;

    private final int DIM;
    public StrassensInput(Matrix mat1, Matrix mat2, Matrix res) {
        if (mat1 == null || mat2 == null || res == null) {
            throw new RuntimeException("Attempted to initialize invalid Strassen's input! Must provide 3 matrices!");
        }
        if (!mat1.dimEquals(mat2) || !mat2.dimEquals(res) || !mat1.dimEquals(res) || !mat1.isSquare()) {
            throw new RuntimeException("All input matrices must be of the same (square) dimension!");
        }
        this.MAT_1 = mat1;
        this.MAT_2 = mat2;
        this.RES = res;
        this.DIM = mat1.getNumCols();
    }

    public Matrix getMat1() {
        return MAT_1;
    }

    public Matrix getMat2() {
        return MAT_2;
    }

    public Matrix getRes() {
        return RES;
    }

    public int getDim() {
        return DIM;
    }
}
