package logic;

import it.ssc.pl.milp.*;
import it.ssc.pl.milp.util.LPThreadsNumber;

import java.util.ArrayList;
import java.util.Arrays;

public final class SIMUS {

    private final double[][] idm;
    private final double[] rhs;
    private final ConsType[] rhsSigns;
    private final GoalType[] goalTypes;
    private final int criterionCount;
    private final int alternativeCount;
    private final double[][] erm; // normalized efficient result matrix
    private final double[] pf; // participation factor
    private final double[] sc; // sum of column
    private final Rank[] ranks; // result ranks for each alternative

    private String log = "";
    private String errorLog = "";
    private Boolean isCorrect = true;

    public double[][] getERM() {
        return this.erm;
    }


    public String getLog() {
        return this.log;
    }

    public boolean getIsSuccess() {
        return this.isCorrect;
    }

    public Rank[] getRanks() {
        return this.ranks;
    }

    /**
     * SIMUS method
     *
     * @param data
     */
    public SIMUS(InputData data) {
        this.idm = Arrays.copyOf(data.idm, data.idm.length);
        this.rhs = Arrays.copyOf(data.rhs, data.rhs.length);
        this.rhsSigns = Arrays.copyOf(data.rhsSigns, data.rhsSigns.length);
        this.goalTypes = Arrays.copyOf(data.actions, data.actions.length);

        criterionCount = data.idm.length;
        alternativeCount = data.idm[0].length;
        erm = new double[criterionCount][alternativeCount];

        pf = new double[alternativeCount];
        sc = new double[alternativeCount];
        ranks = new Rank[alternativeCount];

        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                erm[i][j] = 0;
            }
        }
    }

    public Boolean runLogic() {
        for (int i = 0; i < criterionCount; i++) {
            try {
                Solution solution = SIMPLEX(removeCriterionFromIDM(i), // A
                        idm[i], // object function
                        removeCriterionFromRHS(i), // RHS
                        removeCriterionFromRHSSigns(i), // signs of RHS
                        goalTypes[i]
                );

                if (solution == null) {
                    appendToLog("creterion#" + (i + 1) + ": SOLUTION IS NULL!\n", true);
                } else {
                    erm[i] = transformToERMString(solution);
                }
            } catch (LPException ex) {
                appendToLog("ERROR: LPException\n", false);
            } catch (SimplexException ex) {
                appendToLog("ERROR: SimplexException\n", false);
            } catch (Exception ex) {
                appendToLog("ERROR: Exception\n", false);
            }
        }

        normalize(erm);
        calculateSC();
        calculatePF();
        calculateRanks();


        appendToLog(this.toString(), false);
        return isCorrect;
    }

    private void calculateSC() {
        for (int i = 0; i < alternativeCount; i++) {
            sc[i] = 0;
            for (int j = 0; j < criterionCount; j++) {
                sc[i] += erm[j][i];
            }
        }
    }

    private void calculatePF() {
        for (int i = 0; i < alternativeCount; i++) {
            pf[i] = 0;
            for (int j = 0; j < criterionCount; j++) {
                if (erm[j][i] != 0)
                    pf[i]++;
            }
        }

        normalize(pf);
    }

    public void calculateRanks() {
        for (int i = 0; i < alternativeCount; i++) {
            int countEqual = 0;
            int countGreat = 0;
            for (int j = 0; j < alternativeCount; j++) {
                if (sc[i] * pf[i] < sc[j] * pf[j]) countGreat++;
                if (sc[i] * pf[i] == sc[j] * pf[j]) countEqual++;
            }
            ranks[i] = new Rank(countGreat, countGreat + countEqual - 1);
        }
    }

    private void appendToLog(String str, Boolean isErrorLog) {
        if (isErrorLog) {
            errorLog += str;
            isCorrect = false;
        } else log += str;

    }

    public static double[] transformToERMString(Solution solution) {
        Variable[] variables = solution.getVariables();
        double[] res = new double[variables.length];

        for (int i = 0; i < variables.length; i++) {
            res[i] = variables[i].getValue();
        }

        return res;
    }

    public double[][] removeCriterionFromIDM(int indexOfRemovedCriterion) {
        double[][] res = new double[criterionCount - 1][alternativeCount];

        for (int i = 0; i < indexOfRemovedCriterion; i++) {
            System.arraycopy(idm[i], 0, res[i], 0, alternativeCount);
        }

        for (int i = indexOfRemovedCriterion + 1; i < criterionCount; i++) {
            System.arraycopy(idm[i], 0, res[i - 1], 0, alternativeCount);
        }

        return res;
    }

    public double[] removeCriterionFromRHS(int indexOfRemovedCriterion) {
        double[] res = new double[criterionCount - 1];
        System.arraycopy(rhs, 0, res, 0, indexOfRemovedCriterion);
        for (int i = indexOfRemovedCriterion + 1; i < criterionCount; i++) {
            res[i - 1] = rhs[i];
        }

        return res;
    }

    public ConsType[] removeCriterionFromRHSSigns(int indexOfRemovedCriterion) {
        ConsType[] res = new ConsType[criterionCount - 1];
        System.arraycopy(rhsSigns, 0, res, 0, indexOfRemovedCriterion);
        for (int i = indexOfRemovedCriterion + 1; i < criterionCount; i++) {
            res[i - 1] = rhsSigns[i];
        }

        return res;
    }

    public static void normalize(double[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            double tmpSum = 0;
            for (int column = 0; column < matrix[row].length; column++)
                tmpSum += matrix[row][column];

            if (tmpSum == 0)
                continue;

            for (int column = 0; column < matrix[row].length; column++)
                matrix[row][column] /= tmpSum;
        }
    }

    public static void normalize(double[] array) {
        double tmpSum = 0;
        for (int i = 0; i < array.length; i++)
            tmpSum += array[i];

        if (tmpSum == 0) return;

        for (int i = 0; i < array.length; i++)
            array[i] /= tmpSum;
    }

    @Deprecated
    private void showErm() {
        System.out.println("ERM:");
        for (int i = 0; i < this.erm.length; i++) {
            for (int j = 0; j < this.erm[i].length; j++)
                System.out.print(this.erm[i][j] + " ");
            System.out.println();
        }
    }


    private static Solution SIMPLEX(double[][] A,
                                    double[] c,
                                    double[] b,
                                    ConsType[] rel,
                                    GoalType goalType
    ) throws SimplexException, LPException {

        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(c, goalType);
        ArrayList<Constraint> constraints = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
        try {
            LP lp = new LP(objectiveFunction, constraints); // вот здесь FAIL
            SolutionType solutionType = lp.resolve();
            lp.setThreadsNumber(LPThreadsNumber.N_4);
            return solutionType == SolutionType.OPTIMUM ? lp.getSolution() : null;
        } catch (Exception e) {
        }

        return null;
    }

    @Deprecated
    public static void printOF(LinearObjectiveFunction of) {
        for (double value : of.getC()) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    @Deprecated
    public static void printConstraints(ArrayList<Constraint> constraints) {
        for (int i = 0; i < constraints.size(); i++) {
            System.out.print(constraints.get(i).getRhs() + " ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String res = "";
        if (!errorLog.isEmpty()) {
            builder.append("ERRORS:\n");
            builder.append(errorLog);
        }
        builder.append("\nIDM:\n");
        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                builder.append(this.idm[i][j]).append("\t");
            }
            switch (this.rhsSigns[i]) {
                case LE:
                    builder.append("<= ");
                    break;
                case GE:
                    builder.append(">= ");
                    break;
                case LOWER:
                    builder.append("< ");
                    break;
                case UPPER:
                    builder.append("> ");
                    break;
                case EQ:
                    builder.append("= ");
                    break;
                default:
                    builder.append("? ");
                    break;
            }
            builder.append(this.rhs[i]);
            builder.append("\t");
            builder.append(this.goalTypes[i].toString());
            builder.append("\n");
        }
        builder.append("\nERM:\n");

        for (int i = 0; i < this.erm.length; i++) {
            for (int j = 0; j < this.erm[i].length; j++) {
                builder.append(round(this.erm[i][j], 4)).append("\t");
            }
            builder.append("\n");
        }
        builder.append("\n");

        return builder.toString();
    }

    private static double round(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        double tmp = number * pow;
        return (double) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
    }


    public static IOSAResult runIOSA(InputData inputData, IOSAConstraint iosaConstraint) {
        return runIOSA(inputData, iosaConstraint, 5000, 500);
    }

    public static IOSAResult runIOSA(InputData inputData, IOSAConstraint iosaConstraint, int testCount, int successCountMin) {
        int successCount = 0;

        double[][] pMatrix = new double[inputData.alternativeCount()][inputData.alternativeCount()];
        for (int i = 0; i < pMatrix.length; i++)
            for (int j = 0; j < pMatrix[0].length; j++)
                pMatrix[i][j] = 0;


        for (int i = 0; i < testCount; i++) {
            generateNextData(inputData, iosaConstraint);
            SIMUS simus = new SIMUS(inputData);
            if (simus.runLogic()) {
                successCount++;
                for(int r = 0; r < simus.ranks.length; i++){
                    double incValue = 1.0 / (simus.ranks[r].maxRank - simus.ranks[r].minRank + 1);
                    for(int curRank = simus.ranks[r].minRank; curRank <= simus.ranks[r].maxRank; curRank++)
                        pMatrix[r][curRank] += incValue;
                }
            }
        }

        normalize(pMatrix);
        if(successCount < successCountMin)
            return new IOSAResult(false, testCount, successCount, pMatrix);
        else
            return new IOSAResult(true, testCount, successCount, pMatrix);
    }

    private static void generateNextData(InputData inputData, IOSAConstraint iosaConstraint) {
        for (int i = 0; i < inputData.rhs.length; i++) {
            inputData.rhs[i] = iosaConstraint.minRhs[i] + Math.random() * (iosaConstraint.maxRhs[i] - iosaConstraint.minRhs[i]);
        }

        for (int i = 0; i < inputData.idm.length; i++)
            for (int j = 0; j < inputData.idm[0].length; j++)
                inputData.idm[i][j] = iosaConstraint.minIdm[i][j] + Math.random() * (iosaConstraint.maxIdm[i][j] - iosaConstraint.minIdm[i][j]);
    }
}
