package logic;

import java.util.Arrays;

final class IOSAResult {
    private final double[][] pMatrix;
    private final int testCount;
    private final int successCount;
    private final Boolean isSuccess;


    public IOSAResult(Boolean isSuccess, int testCount, int successCount, double[][] pMatrix) {
        this.isSuccess = isSuccess;
        this.testCount = testCount;
        this.successCount = successCount;
        this.pMatrix = Arrays.copyOf(pMatrix, pMatrix.length);
    }

    public double[][] getPMatrix() {
        return pMatrix;
    }

    public int getTestCount(){
        return this.testCount;
    }

    public int getSuccessCount(){
        return successCount;
    }

    public Boolean getIsSuccess(){
        return isSuccess;
    }
}
