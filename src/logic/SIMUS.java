package logic;

import it.ssc.pl.milp.*;
import it.ssc.pl.milp.util.*;
import it.ssc.log.SscLogger;
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
    private String log = "";
    private String errorLog = "";

    public double[][] getERM() {
        return this.erm;
    }
    
    public String getLog(){
        return this.log;
    }

    /**
     * SIMUS method
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

        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                erm[i][j] = 0;
            }
        }
    }
    
    public void runLogic(){
        for (int i = 0; i < criterionCount; i++) {
            try {
                Solution solution = SIMPLEX(removeCriterionFromIDM(i), // A
                        idm[i], // object function
                        removeCriterionFromRHS(i), // RHS
                        removeCriterionFromRHSSigns(i), // signs of RHS
                        goalTypes[i]
                );

                // printSolution(solution); // TODO: kill this     
                if (solution == null) {
                    appendToLog("creterion#" + (i+1) + ": SOLUTION IS NULL!\n", true);
                } else{
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

        // normalize(erm); TODO: Добавить нормальизацию
        
        appendToLog(this.toString(), false);
    }
    
    private void appendToLog(String str, Boolean isErrorLog){
        if(isErrorLog) errorLog += str;
        else log += str;
        
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

    public static Solution SIMPLEX(double[][] A,
            double[] c,
            double[] b,
            ConsType[] rel,
            GoalType goalType
    ) throws SimplexException, LPException {

        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(c, goalType);
        ArrayList< Constraint> constraints = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
        try {
            LP lp = new LP(objectiveFunction, constraints); // вот здесь FAIL
            SolutionType solutionType = lp.resolve();
            // lp.setThreadsNumber(LPThreadsNumber.N_4);
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
    public static void printConstraints(ArrayList< Constraint> constraints) {
        for(int i = 0; i< constraints.size(); i++){
            System.out.print(constraints.get(i).getRhs() + " ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        String res = "";
        if(!errorLog.isEmpty()){
            res += "ERRORS:\n";
            res += errorLog;
        }
        res += "\nIDM:\n";
        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                res += (this.idm[i][j] + "\t");
            }
            switch (this.rhsSigns[i]) {
                case LE:
                    res += "<= ";
                    break;
                case GE:
                    res += ">= ";
                    break;
                case LOWER:
                    res += "< ";
                    break;
                case UPPER:
                    res += "> ";
                    break;
                case EQ:
                    res += "= ";
                    break;
                default:
                    res += "? ";
                    break;
            }
            res += (this.rhs[i]);
            res += "\t";
            res += (this.goalTypes[i].toString());
            res += "\n";
        }
        res += "\nERM:\n";

        for (int i = 0; i < this.erm.length; i++) {
            for (int j = 0; j < this.erm[i].length; j++) {
                res += round(this.erm[i][j], 4) + "\t";
            }
            res += "\n";
        }
        res += "\n";

        return res;
    }

    private static double round(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        double tmp = number * pow;
        return (double) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
    }
}
