package org.inf.ed.ac.uk.tests.strassens;

import java.util.Objects;

public class MatrixView extends Matrix {

    private Matrix baseMatrix;
    private final Matrix parent;
    private final int START_ROW;
    private final int START_COl;
    private final int ROWS;
    private final int COLS;

    public MatrixView(Matrix baseMatrix, int startRow, int endRow, int startCol, int endCol) {
        if (startRow < 0 || startCol < 0 || endRow >= baseMatrix.getNumRows() || endCol >= baseMatrix.getNumCols()) {
            throw new RuntimeException("Invalid matrix view provided!");
        }
        this.baseMatrix = baseMatrix;
        this.ROWS = endRow - startRow + 1;
        this.COLS = endCol - startCol + 1;
        this.parent = baseMatrix;
        while (!(this.baseMatrix instanceof ConcreteMatrix)) { // perform reduction to shorten call chain -> speedup operations
            startRow += this.baseMatrix.getStartRow();
            startCol += this.baseMatrix.getStartCol();
            this.baseMatrix = this.baseMatrix.getUnderlying();
        }
        this.START_ROW = startRow;
        this.START_COl = startCol;
    }

    @Override
    protected void addOp(Matrix other, Matrix target) {
        if (!this.dimEquals(other)) {
            throw new RuntimeException("Mismatched matrix dimensions");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                target.updateValue(baseMatrix.getValue(START_ROW + row, START_COl + col) + other.getValue(row, col), row, col);
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
                target.updateValue(baseMatrix.getValue(START_ROW + row, START_COl + col) - other.getValue(row, col), row, col);
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
                    res += baseMatrix.getValue(START_ROW + row, START_COl + k) * other.getValue(k, col);
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
                baseMatrix.updateValue(matrixToCopy.getValue(row, col), START_ROW + row, START_COl + col);
            }
        }
    }

    @Override
    public int getValue(int rowIndex, int colIndex) {
        return baseMatrix.getValue(START_ROW + rowIndex, START_COl + colIndex);
    }

    @Override
    public void updateValue(int value, int rowIndex, int colIndex) {
        baseMatrix.updateValue(value, START_ROW + rowIndex, START_COl + colIndex);
    }

    @Override
    public boolean dimEquals(Matrix other) {
        return ROWS == other.getNumRows() && COLS == other.getNumCols();
    }

    @Override
    public boolean isSquare() {
        return ROWS == COLS;
    }

    @Override
    public Matrix[] quadrantSplit() {
        if (!isSquare()) {
            throw new RuntimeException("Matrix must be square for quadrant split");
        }
        int split = ROWS / 2;
        Matrix[] matSplit = new Matrix[4];
        matSplit[0] = new MatrixView(this, 0, split - 1, 0, split - 1); // upper left
        matSplit[1] = new MatrixView(this, 0, split - 1 , split, COLS - 1); // upper right
        matSplit[2] = new MatrixView(this, split, ROWS - 1, 0, split - 1); // lower left
        matSplit[3] = new MatrixView(this, split, ROWS - 1, split, COLS - 1); // lower right
        return matSplit;
    }

    @Override
    protected Matrix getUnderlying() {
        return baseMatrix;
    }

    protected Matrix getParent(){return parent;}

    @Override
    public int getStartRow() {
        return this.START_ROW;
    }

    @Override
    public int getStartCol() {
        return this.START_COl;
    }

    @Override
    public int getNumRows() {
        return ROWS;
    }

    @Override
    public int getNumCols() {
        return COLS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatrixView that = (MatrixView) o;
        return START_ROW == that.START_ROW && START_COl == that.START_COl && ROWS == that.ROWS && COLS == that.COLS && baseMatrix.equals(that.baseMatrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseMatrix, START_ROW, START_COl, ROWS, COLS);
    }

    @Override
    public String toString() {
        StringBuilder printStr = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                printStr.append(baseMatrix.getValue(START_ROW + row, START_COl + col)+ " ");
            }
            printStr.append("\n");
        }
        return printStr.toString();
    }
}
