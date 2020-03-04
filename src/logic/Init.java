package logic;


import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LPException;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.SimplexException;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import static logic.SIMUS.SIMPLEX;




public class Init {
    
    public double[][] idm;
    public double[] rhs;
    public ConsType[] rhsSigns; 
    
    public static final double POSITIVE_INF = Double.MAX_VALUE/2.718281828459045;
    public static final double NEGATIVE_INF = Double.MIN_VALUE/2.718281828459045;
    
    public static void main(String args[]){
       System.out.println("Start test");
        
       // startGUI();
       // test1();
       // test2();
       
       test3();
       
       // printMatrix(simus.getERM());
    }  
    
    private static void test0(){ // стр 264
       
        double[][] idm  = {
            { 0.95, 0.68},
            {0.74, 0.75},
            {0.78, 0.98},
            {0.92, 0.65},
            {0.99, 0.60}
        };
       
       double[] rhs = {
        0.68,
        1.00,
        0.84,
        0.94,
        0.80
        };
       
        
        ConsType[] rhsSigns= {
          ConsType.GE,
          ConsType.LE,
          ConsType.GE,
          ConsType.LE,
          ConsType.GE
      };
        
      GoalType[] goalTypes = {
          GoalType.MIN,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MAX,
          GoalType.MIN
      };
        
      SIMUS simus = new SIMUS(idm, rhs, rhsSigns,goalTypes);
    }
    
    private static void test1(){ // стр 264
       
        double[][] idm  = {
            { 5.7, 5.9, 6, 6.1},
            { 6200, 6050, 4800, 3800},
            { 4.2, 4.2, 2.8, 3.1},
            { 5.7, 5.9, 6, 6.01},
        };
       
       double[] rhs = {
        NEGATIVE_INF,
        NEGATIVE_INF,
        POSITIVE_INF,
        NEGATIVE_INF,
        };
       
        
        ConsType[] rhsSigns= {
          ConsType.GE,
          ConsType.GE,
          ConsType.LE,
          ConsType.GE,
      };
        
      GoalType[] goalTypes = {
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MAX,
      };
        
      SIMUS simus = new SIMUS(idm, rhs, rhsSigns,goalTypes);
    }
    
     private static void test2(){ // стр 152
        
      double[][] idm  = {
            { 2198, 3542, 2037, 0, 75, 2E+06, 358000, 0, 0},
            { 89321, 85937, 89157, 0, 0, 101237, 89752, 0, 0},
            { 0.063, 0.058, 0.089, 0, 0, 0.059, 0.061, 0, 0},
          
            { 0, 3401, 1952, 2023, 70, 2E+06, 123000, 2100000, 140},
            { 0, 85478, 88958, 91542, 0, 98630, 98631, 100157, 0},
            { 0, 0.061, 0.077, 0.082, 0, 0.098, 0.085, 0.102, 0},
            { 0, 13697, 11230, 25842, 0, 567000, 423000, 800000, 0},
            { 0, 4, 4, 3, 0, 0, 0, 5, 0},
            
            { 1956, 3821, 0, 1025, 50, 789500, 289100, 0, 0 },
            { 88127, 86904, 0, 91304, 0, 106932, 91236, 0, 0 },
            { 0.067, 0.049, 0, 0.093, 0, 0.091, 0.085, 0, 0 },
            { 15631, 12852, 0, 25630, 0, 567000, 423000, 0, 0 },
            { 0.058, 0.061, 0, 0.062, 0, 0.08,0.07, 0, 0 },
            { 0.01, 0.012, 0, 0.03, 0, 0, 0, 0, 0 },
            
           
            { 2301, 0, 3327, 0, 75, 9E+06, 287000, 1457000, 97 },
            { 85962, 0, 89627, 0, 0, 102457, 89631, 1023789, 0 },
            { 0.065, 0, 0.071, 0, 0, 0.091, 0.085, 0.09, 0, },
            { 15631, 0, 12785, 0, 0, 567000, 423000, 800000, 0 },
            { 4, 0, 5, 0, 0, 4, 5, 5, 0 },
            { 0.07, 0, 0.03, 0, 0, 0.07, 0.08, 0.09, 0 },
            { 0.01, 0, 0.011, 0, 0, 0.015, 0.021, 0.08, 0 },
        };
        
      
      ConsType[] rhsSigns= {
          ConsType.GE,
          ConsType.GE,
          ConsType.GE,
         
          ConsType.GE,
          ConsType.GE,
          ConsType.GE,
          ConsType.LE,
          ConsType.GE,

          ConsType.GE,
          ConsType.GE,
          ConsType.GE,
          ConsType.LE,
          ConsType.LE,
          ConsType.LE,
        
          ConsType.GE,
          ConsType.GE,
          ConsType.GE,
          ConsType.LE,
          ConsType.GE,
          ConsType.LE,
          ConsType.LE
      };
      
      GoalType[] goalTypes= {
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MAX,
          
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MAX,

          GoalType.MAX,
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MIN,
          GoalType.MIN,

          GoalType.MAX,
          GoalType.MAX,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MIN,
      };
      
      double[] rhs = {
          NEGATIVE_INF,
          NEGATIVE_INF,
          NEGATIVE_INF,
         
          NEGATIVE_INF,
          NEGATIVE_INF,
          NEGATIVE_INF,
          POSITIVE_INF,
          NEGATIVE_INF,

          NEGATIVE_INF,
          NEGATIVE_INF,
          NEGATIVE_INF,
          POSITIVE_INF,
          POSITIVE_INF,
          POSITIVE_INF,
         
          NEGATIVE_INF,
          NEGATIVE_INF,
          NEGATIVE_INF,
          POSITIVE_INF,
          NEGATIVE_INF,
          POSITIVE_INF,
          POSITIVE_INF,
        };
      
      SIMUS simus = new SIMUS(idm, rhs, rhsSigns, goalTypes);
    }
    
     private static void test3(){
         double[][] idm  = {
            { 0.85, 0.75},
            {0.78, 0.98},
            {0.92, 0.65},
            {0.99, 0.60},
        };
       
       double[] rhs = {
        1.00,
        0.84,
        0.94,
        0.80
        };
       
        
        ConsType[] rhsSigns= {
          ConsType.LE,
          ConsType.GE,
          ConsType.LE,
          ConsType.GE,
      };
        
      GoalType[] goalTypes = {
          GoalType.MAX,
          GoalType.MIN,
          GoalType.MAX,
          GoalType.MIN,
      };
      
      double[] of = {0.72, 0.68};
      
        try {
            Solution solution = SIMPLEX(idm, // A
                    of, // object function
                    rhs, // RHS
                    rhsSigns, // signs of RHS
                    GoalType.MIN
            );
            
            System.err.println(solution.getTypeSolution());
            System.err.println(solution.getOptimumValue());
            System.err.println(solution.getVariables());
            
            writeToFile(solution.getTypeSolution().toString(), "solution.txt");
            writeToFile(Double.toString(solution.getOptimumValue()), "getOptimumValue.txt");
            
            String strVars  = "";
            Variable[] vars = solution.getVariables();
            for(int i = 0; i<vars.length; i++){
                strVars += Double.toString(vars[i].getValue()) + "\n";
            }
            writeToFile(strVars, "getVariables.txt");
            
            
        } catch (SimplexException ex) {
            Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LPException ex) {
            Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      SIMUS simus = new SIMUS(idm, rhs, rhsSigns,goalTypes);
     }
     
     public static void writeToFile(String str, String fileName) {
         try{
             BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
         } catch(IOException e){
             
         }
        
     }
}
