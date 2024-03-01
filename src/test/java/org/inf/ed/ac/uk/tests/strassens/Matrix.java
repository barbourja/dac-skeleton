package org.inf.ed.ac.uk.tests.strassens;

public abstract class Matrix {

    protected abstract void addOp(Matrix other, Matrix target);

    public Matrix add(Matrix other, Matrix result) {
        addOp(other, result);
        return result;
    }

    public Matrix addInPlace(Matrix other) {
        addOp(other, this);
        return this;
    }

    protected abstract void subOp(Matrix other, Matrix target);

    public Matrix sub(Matrix other, Matrix result) {
        subOp(other, result);
        return result;
    }

    public Matrix subInPlace(Matrix other) {
        subOp(other, this);
        return this;
    }

    protected abstract void multOp(Matrix other, Matrix target);

    public Matrix mult(Matrix other, Matrix result) {
        multOp(other, result);
        return result;
    }

    public Matrix multInPlace(Matrix other) {
        multOp(other, this);
        return this;
    }

    abstract void updateMatrix(Matrix matrixToCopy);

    abstract void updateValue(int value, int rowIndex, int colIndex);

    abstract int getValue(int rowIndex, int colIndex);

    public boolean isSquare() {return getNumRows() == getNumCols();}

    public boolean dimEquals(Matrix other) {
        return getNumRows() == other.getNumRows() && getNumCols() == other.getNumCols();
    }

    public Matrix[] quadrantSplit() {
        if (!isSquare() || getNumRows() % 2 != 0 || getNumRows() <= 0) {
            throw new RuntimeException("Matrix must be square for quadrant split");
        }
        int split = getNumRows() / 2;
        Matrix[] matSplit = new Matrix[4];
        matSplit[0] = new MatrixView(this, 0, split - 1, 0, split - 1); // upper left
        matSplit[1] = new MatrixView(this, 0, split - 1 , split, getNumCols() - 1); // upper right
        matSplit[2] = new MatrixView(this, split, getNumRows() - 1, 0, split - 1); // lower left
        matSplit[3] = new MatrixView(this, split, getNumRows() - 1, split, getNumCols() - 1); // lower right
        return matSplit;
    }

    protected abstract Matrix getParent();

    protected abstract Matrix getUnderlying();

    abstract int getStartRow();

    abstract int getStartCol();

    abstract int getNumRows();

    abstract int getNumCols();
}
