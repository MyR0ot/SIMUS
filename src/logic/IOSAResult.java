package logic;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.Arrays;


final class IOSAResult {
    private final double[][] pMatrix;
    private final int testCount;
    private final int successCount;
    private final Boolean isSuccess;

    private static final int MAX_COLUMN_SIZE = 15;


    public IOSAResult(Boolean isSuccess, int testCount, int successCount, double[][] pMatrix) {
        this.isSuccess = isSuccess;
        this.testCount = testCount;
        this.successCount = successCount;
        this.pMatrix = Arrays.copyOf(pMatrix, pMatrix.length);
    }

    public double[][] getPMatrix() {
        return pMatrix;
    }

    public int getTestCount() {
        return this.testCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    @Deprecated
    public void printPMatrix() {
        System.err.println("----------------- P MATRIX ----------------");
        for (int i = 0; i < pMatrix.length; i++) {
            for (int j = 0; j < pMatrix[i].length; j++)
                System.err.print(pMatrix[i][j] + " ");
            System.err.println();
        }
    }

    public void exportToEXCEL() {
        exportToEXCEL("SMAA_Report.xlsx");
    }


    public void exportToEXCEL(String fileName) {
        try {
            int sizeMatrix = pMatrix.length;
            XSSFWorkbook book = new XSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream(fileName);
            XSSFSheet sheet = book.createSheet("Sheet 1"); // создания страниц

            XSSFRow xssRow = sheet.createRow(0);
            for (int j = 1; j <= sizeMatrix; j++) {
                XSSFCell cell = xssRow.createCell(j);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("P-"+ j);
            }

            for (int row = 0; row < pMatrix.length; row++) {
                xssRow = sheet.createRow(row + 1);
                XSSFCell cell = xssRow.createCell(0);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("A-" + (row+1));

                for (int column = 0; column < pMatrix[row].length; column++) {
                    cell = xssRow.createCell(column + 1);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(pMatrix[row][column]);
                }
            }


            book.write(fileOut);
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
