package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;



public class Init {
    
    public static final double POSITIVE_INF = Double.MAX_VALUE/2.718281828459045;
    public static final double NEGATIVE_INF = Double.MIN_VALUE/2.718281828459045;
    public static int successCount = 0;
    
    public static void main(String args[]) throws IOException{

        MainForm w = new MainForm();

        w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        w.setVisible(true);
        return;
//    	InputData data = generateRndData(new ConstraintData(4, 5, 0, 100, 5000, 70000), 2);
//        SIMUS simus = new SIMUS(data);
//        boolean res = simus.runLogic();
//        System.out.println(res);
//        Rank[]  ranks = simus.getRanks();
//
//        for(int i  = 0; i < ranks.length; i++) {
//        	System.out.println(ranks[i].minRank + "-" + ranks[i].maxRank);
//        }
        
    }  
     
     public static void writeToFile(String str, String fileName) {
         try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
         } catch(IOException e){
             
         }
     }
     
     public static InputData parseTest(String fileName){
        InputData res = new InputData();
        Scanner scan;
        
        
        File file = new File(fileName);
        
        int n, m;        
        
        try {
            scan = new Scanner(file);

            n = scan.nextInt();
            m = scan.nextInt();
        
            res.idm = new double[n][m];
            res.rhs = new double[n];
            res.rhsSigns = new ConsType[n];
            res.actions = new GoalType[n];
            
            for(int i = 0; i< n; i++)
                for(int j = 0; j<m; j++)
                    res.idm[i][j] = scan.nextDouble();
        
            for(int i = 0; i< n; i++){
                if(scan.nextInt() == 1){
                    res.actions[i] = GoalType.MAX;
                } else {
                    res.actions[i] = GoalType.MIN;
                }
            }
            
            for(int i = 0; i < n; i++){
                switch(scan.nextInt())
                {
                    case 1: res.rhsSigns[i] = ConsType.GE; break;
                    case 2: res.rhsSigns[i] = ConsType.LE; break;
                    case 3: res.rhsSigns[i] = ConsType.UPPER; break;
                    case 4: res.rhsSigns[i] = ConsType.LOWER; break;
                    case 5: res.rhsSigns[i] = ConsType.EQ; break;
                }
            }
            
            for(int i = 0; i<n; i++)
                res.rhs[i] = scan.nextDouble();
        } 
        catch (FileNotFoundException e1) {
            return null;
        }
        return res;
     }
     
     @Deprecated
     public static void showData(InputData data) {
        for (int i = 0; i < data.idm.length; i++) {
            for (int j = 0; j < data.idm[0].length; j++) {
                System.out.print(data.idm[i][j] + " ");
            }
            switch (data.rhsSigns[i]) {
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

            System.out.print(data.rhs[i] + " " + data.actions[i].toString() + "\n");
        }
    }

     public static InputData generateRndData(ConstraintData constraint, int precision){
         InputData res = new InputData();
         res.idm = new double[constraint.getCriterionCount()][constraint.getAlternativeCount()];
         res.rhs = new double[constraint.getCriterionCount()];
         res.rhsSigns = new ConsType[constraint.getCriterionCount()];
         res.actions = new GoalType[constraint.getCriterionCount()];
         
        for(int i = 0; i<constraint.getCriterionCount(); i++)
            for(int j = 0; j<constraint.getAlternativeCount(); j++)
                res.idm[i][j] = Support.getRndDouble(constraint.getMinIDMValue(), constraint.getMaxIDMValue(), precision);
        

        for(int i = 0; i < constraint.getCriterionCount(); i++)
            res.rhs[i] = Support.getRndDouble(constraint.getMinRHSValue(), constraint.getMaxRHSValue(), precision);
        
        for(int i = 0; i<constraint.getCriterionCount(); i++){
            if(Support.getRndBoolean()) res.rhsSigns[i] = ConsType.GE;
            else res.rhsSigns[i] = ConsType.LE;
        }
        
        for(int i = 0; i<constraint.getCriterionCount(); i++){
            /*if(res.rhsSigns[i] == ConsType.GE) res.actions[i] = GoalType.MIN;
            else res.actions[i] = GoalType.MAX;*/
            if(Support.getRndBoolean()) res.actions[i] = GoalType.MIN;
            else res.actions[i] = GoalType.MAX;
        }
            
        
        return res;
     }
     
     private static long calculateExistTest(String folderName, boolean isTimerForOnlySuccessTest){
        successCount = 0;
        long time = 0;
        long start, finish;
	Locale.setDefault(new Locale("en", "EN"));
                
        File[] folderEntries = new File("tests/input/" + folderName).listFiles();
        for (File entry : folderEntries)
        {
            System.err.println(entry.getName());
            if (!entry.isDirectory())
            {
                InputData data = parseTest(entry.getPath());
                start = System.currentTimeMillis();
                SIMUS simus = new SIMUS(data);
                simus.runLogic();
                finish = System.currentTimeMillis();
                
                if(!isTimerForOnlySuccessTest || simus.getIsSuccess()){
                    time += (finish - start);
                }
                if(simus.getIsSuccess()) successCount++;
                
                if(simus.getIsSuccess()) writeToFile(simus.getLog(), "tests/answers/corrected/" + folderName + "/" + entry.getName());
                else writeToFile(simus.getLog(), "tests/answers/notCorrected/" + folderName + "/" +  entry.getName());
                
            }
        }
        
        return time;
     }
     
     private static void createTestFile(InputData data, String fileName) {
         StringBuilder builder = new StringBuilder();
         builder.append(data.idm.length);
         builder.append(" ");
         builder.append(data.idm[0].length);
         builder.append("\n");

         for(int i = 0; i<data.idm.length; i++){
             for(int j = 0; j<data.idm[0].length; j++)
                 builder.append(data.idm[i][j]).append(" ");
             builder.append("\n");
         }
         for(int i = 0; i<data.actions.length; i++){
             if(data.actions[i] == GoalType.MAX) builder.append("1 ");
             else builder.append("0 ");
         }
        builder.append("\n");
        
        for(int i = 0; i < data.rhsSigns.length; i++){
            switch(data.rhsSigns[i]){
                case GE: builder.append("1 ");; break;
                case LE: builder.append("2 ");; break;
                case UPPER: builder.append("3 ");; break;
                case LOWER: builder.append("4 ");; break;
                case EQ: builder.append("5 ");; break;
            }
         }
        builder.append("\n");
        
        
        for(int i = 0; i < data.rhs.length; i++)
            builder.append(data.rhs[i]).append(" ");   
         try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(builder.toString());
            writer.close();
         } catch(IOException e){
             
         }
     }
     
     private static String testNxM(int n, int m){ 
        String folderName = (String.valueOf(n) + "x" + String.valueOf(m));
        ConstraintData constraint = new ConstraintData(n, m, 0, 100, 5000, 70000);
        for(int i = 1; i <= 100; i++){
            InputData data = generateRndData(constraint, 2);
            createTestFile(data, "tests/input/" + folderName + "/test_"+ folderName + "_"  + i + ".txt");
        }
        long time = calculateExistTest(folderName, true);
        String res = String.valueOf(successCount) + ": ";
        res += String.valueOf(time) + "ms\n";
        return res;   
        
    }
     
     private static int testRHS(double left, double right){
        int count = 0;
        ConstraintData constraint = new ConstraintData(10, 5, 0, 10, left, right);
        for(int i = 1; i <= 100; i++){
            InputData data = generateRndData(constraint, 2);
            SIMUS s = new SIMUS(data);
            if(s.runLogic())
                count++;
        }
        
        return count;
    }
     
     private static double IOSASimus(InputData data, IOSAConstraint iosaConstraint, int countTests) {
    	 int countSuccess = 0;
    	 
    	 int alternativeCount = data.alternativeCount();
    	 double rankMatrix[][] = new double[alternativeCount][alternativeCount]; // [i][j] -> кол-во раз, что i альтернатива имеет ранг j
    	 
    	 for(int i = 0; i < alternativeCount; i++)
    		 for(int j = 0; j < alternativeCount ; j++)
    			 rankMatrix[i][j] = 0;
    	 
    	 for(int i = 0; i< countTests; i++) {
    		 InputData rndData = generateData(data, iosaConstraint);
    		 SIMUS simus = new SIMUS(rndData);
    		 if(simus.runLogic()) {
    			 countSuccess++;
    			 Rank[] ranks = simus.getRanks();
    			 
    			 for(int k = 0; k < ranks.length; k++) {
    				 for(int r = ranks[k].minRank; r<=ranks[k].maxRank; r++)
    					 rankMatrix[k][r] += (1.0 / (ranks[k].maxRank - ranks[k].minRank + 1));
    			 }
    		 }
    		 
    	 }
    	 
    	 
    	 return countSuccess * 1.0 / countTests;
     }
     
     private static InputData generateData(InputData data, IOSAConstraint iosaConstraint) {
    	 
    	 InputData rndData = data.copy();
    	 
    	 for(int i = 0; i<rndData.idm.length; i++)
    		 for(int j = 0; j<rndData.idm[i].length; j++) {
    			 rndData.idm[i][j] = iosaConstraint.minIdm[i][j] + Math.random() * (iosaConstraint.maxIdm[i][j] - iosaConstraint.minIdm[i][j]);
    		 }

    	 for(int i = 0; i<rndData.rhs.length; i++)
    	     rndData.rhs[i] = iosaConstraint.minRhs[i] + Math.random() * (iosaConstraint.maxRhs[i] - iosaConstraint.minRhs[i]);

    	 return rndData;
     }
}
