package logic;

import it.ssc.pl.milp.*;
import it.ssc.pl.milp.util.*;
import it.ssc.log.SscLogger;
import java.util.ArrayList;
import java.util.Arrays;

public final class SIMUS {

    private double[][] idm;
    private double[] rhs;
    private ConsType[] rhsSigns;
    private GoalType[] goalTypes;
    private int criterionCount;
    private int alternativeCount;
    private double[][] erm; // normalized efficient result matrix

    public double[][] getERM() {
        return this.erm;
    }

    /**
     * SIMUS method
     *
     * @param idm
     * @param rhs
     * @param rhsSigns
     */
    public SIMUS(double[][] idm, double[] rhs, ConsType[] rhsSigns, GoalType[] goalTypes) {
        this.idm = Arrays.copyOf(idm, idm.length);
        this.rhs = Arrays.copyOf(rhs, rhs.length);
        this.rhsSigns = Arrays.copyOf(rhsSigns, rhsSigns.length);
        this.goalTypes = Arrays.copyOf(goalTypes, goalTypes.length);

        criterionCount = idm.length;
        alternativeCount = idm[0].length;
        erm = new double[criterionCount][alternativeCount];

        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                erm[i][j] = 0;
            }
        }

        normalizeInputData();
        showInitData();

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
                    System.err.println("ERROR: SOLUTION IS NULL!");
                } else{
                    erm[i] = transformToERMString(solution);
                }

                

            } catch (LPException ex) {
                System.err.println("ОШИБКА!!! LPException");
            } catch (SimplexException ex) {
                System.err.println("ОШИБКА!!! SimplexException");
            } catch (Exception ex) {
                System.err.println("ОШИБКА!!! Exception");
            }
        }

        System.out.println(this.toString());
        normalize(erm);
        System.out.println(this.toString());

    }

    public void showInitData() {
        for (int i = 0; i < this.criterionCount; i++) {
            for (int j = 0; j < this.alternativeCount; j++) {
                System.out.print(this.idm[i][j] + " ");
            }
            switch (this.rhsSigns[i]) {
                case LE:
                    System.out.print("<= ");
                    break;
                case GE:
                    System.out.print(">= ");
                    break;
                case LOWER:
                    System.out.print("< ");
                    break;
                case UPPER:
                    System.out.print("> ");
                    break;
                case EQ: {
                    System.out.print("= ");
                    break;
                }
                default: {
                    System.out.print("? ");
                    break;
                }
            }

            System.out.print(this.rhs[i] + "\n");
        }
    }
    
    public void normalizeInputData(){
        
        for (int i = 0; i < criterionCount; i++) {
            double tmpSum = 0.0;
            for (int j = 0; j < alternativeCount; j++) {
                tmpSum += this.idm[i][j];
            }
            if(tmpSum == 0)
                continue;
            
            for (int j = 0; j < alternativeCount; j++) {
                this.idm[i][j] /= tmpSum;
                this.idm[i][j] *= 100; // ?
            }
            
            if(this.rhs[i] != Init.POSITIVE_INF && this.rhs[i] != Init.NEGATIVE_INF ){
                this.rhs[i] /= tmpSum;
            }
        }
        
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
            for (int column = 0; column < matrix[row].length; column++) {
                tmpSum += matrix[row][column];
            }

            if (tmpSum == 0) {
                continue;
            }

            for (int column = 0; column < matrix[row].length; column++) {
                matrix[row][column] /= tmpSum;
            }

        }
    }

    @Deprecated
    public static void test() throws LPException, SimplexException, Exception {

        double[] c = {3, 1, 0, 0, 0};
        double[] b = {1, 2, 6};
        double[][] A = {
            {13584, 14908, 15214},
            {89321, 85967, 99157},
            {417, 362, 189},
            {1, 1, 1},
            {1, 1, 1}
        };

        ConsType[] rel = {
            ConsType.GE,
            ConsType.GE,
            ConsType.LE,
            ConsType.LE,
            ConsType.LE,
            ConsType.GE
        };

        LinearObjectiveFunction fo = new LinearObjectiveFunction(c, GoalType.MAX);

        ArrayList< Constraint> constraints = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }

        LP lp = new LP(fo, constraints);
        lp.setThreadsNumber(LPThreadsNumber.N_4);
        SolutionType solution_type = lp.resolve();

        if (solution_type == SolutionType.OPTIMUM) {
            Solution solution = lp.getSolution();
            for (Variable var : solution.getVariables()) {
                SscLogger.log("Variable name :" + var.getName() + " value:" + var.getValue());
            }
            SscLogger.log("o.f. value:" + solution.getOptimumValue());
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
        // Thread.sleep(2000);
        System.out.println("//////////////////////////////////////////////////////////////////////////////");
        try {
            LP lp = new LP(objectiveFunction, constraints); // вот здесь FAIL
            SolutionType solutionType = lp.resolve();
            System.out.println("solutionType: " + solutionType.getValue());
            // lp.setThreadsNumber(LPThreadsNumber.N_4);
            return solutionType == SolutionType.OPTIMUM ? lp.getSolution() : null;
        } catch (Exception e) {
            System.out.print("isEmpty?");
            printOF(objectiveFunction);
            printConstraints(constraints);
        }
        
        return null;
    }

    public static void printSolution(Solution solution) {
        /*for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable name :" + var.getName() + " value:" + var.getValue());
            }
            SscLogger.log("o.f. value:" + solution.getOptimumValue());*/
        System.err.println("!");
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

        String res = "IDM:\n";
        for (int i = 0; i < criterionCount; i++) {
            for (int j = 0; j < alternativeCount; j++) {
                res += (this.idm[i][j] + " ");
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
            res += "\n";
        }
        res += "\nERM:\n";

        for (int i = 0; i < this.erm.length; i++) {
            for (int j = 0; j < this.erm[i].length; j++) {
                res += round(this.erm[i][j], 4) + " ";
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
