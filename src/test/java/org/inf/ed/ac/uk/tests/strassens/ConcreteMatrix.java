package org.inf.ed.ac.uk.tests.strassens;

import java.util.Arrays;
import java.util.Objects;

public class ConcreteMatrix extends Matrix {

    private final int[][] matrix;
    private final int ROWS;
    private final int COLS;

    public ConcreteMatrix(int[][] values) {
        this.matrix = values;
        this.ROWS = values.length;
        this.COLS = values[0].length;
    }

    @Override
    protected void addOp(Matrix other, Matrix target) {
        if (!this.dimEquals(other)) {
            System.out.println("Borked?!");
            System.out.println(this.getNumCols() + " " + this.getNumRows() + "|" + other.getNumCols() + " " + other.getNumRows()
            + "|" + target.getNumCols() + " " + target.getNumRows());
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                target.updateValue(matrix[row][col] + other.getValue(row, col), row, col);
            }
        }
    }

    @Override
    protected void subOp(Matrix other, Matrix target) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                target.updateValue(matrix[row][col] - other.getValue(row, col), row, col);
            }
        }
    }

    @Override
    protected void multOp(Matrix other, Matrix target) {
        if (COLS != other.getNumRows() || target.getNumRows() != ROWS || target.getNumCols() != other.getNumCols()) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < other.getNumCols(); col++) {
                int res = 0;
                for (int k = 0; k < other.getNumRows(); k++) {
                    res += matrix[row][k] * other.getValue(k, col);
                }
                target.updateValue(res, row, col);
            }
        }
    }

    @Override
    public void updateMatrix(Matrix matrixToCopy) {
        if (!this.dimEquals(matrixToCopy)) {
            throw new RuntimeException("Matrix to copy must have same dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                matrix[row][col] = matrixToCopy.getValue(row, col);
            }
        }
    }

    @Override
    public int getValue(int rowIndex, int colIndex) {
        return this.matrix[rowIndex][colIndex];
    }

    @Override
    public void updateValue(int value, int rowIndex, int colIndex) {
        if (rowIndex < 0 || colIndex < 0 || rowIndex >= ROWS || colIndex >= COLS) {
            throw new RuntimeException("Indices out of range");
        }
        this.matrix[rowIndex][colIndex] = value;
    }

    @Override
    protected Matrix getUnderlying() {
        return this;
    }

    @Override
    protected Matrix getParent(){return this;}

    @Override
    public int getStartRow() {
        return 0;
    }

    @Override
    public int getStartCol() {
        return 0;
    }

    @Override
    public int getNumRows() {return ROWS;}

    @Override
    public int getNumCols() {return COLS; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteMatrix that = (ConcreteMatrix) o;
        return ROWS == that.ROWS && COLS == that.COLS && Arrays.deepEquals(matrix, that.matrix);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ROWS, COLS);
        result = 31 * result + Arrays.deepHashCode(matrix);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder printStr = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                printStr.append(matrix[row][col]).append(" ");
            }
            printStr.append("\n");
        }
        return printStr.toString();
    }
}
