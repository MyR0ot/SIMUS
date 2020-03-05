package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;
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
    
    public static void main(String args[]) throws IOException{
      test100x100();
      
      ConstraintData constraint = new ConstraintData(8, 1000, 0, 100, 500, 5000);
      for(int i = 1; i <= 100; i++){
          InputData data = generateRndData(constraint, 2);
          createTestFile(data, "tests/input/test_50x50_" + i + ".txt");
      }
      
      
      calculateExistTest(true);
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
     
     
     private static InputData generateRndData(ConstraintData constraint, int precision){
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
            if(Support.getRndBoolean()) res.actions[i] = GoalType.MAX;
            else res.actions[i] = GoalType.MIN;
        }
            
        
        return res;
     }
     
     
     private static long calculateExistTest(boolean isTimerForOnlySuccessTest){
         
        long time = 0;
        long start, finish;
	Locale.setDefault(new Locale("en", "EN"));
                
        File[] folderEntries = new File("tests/input/").listFiles();
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
                if(simus.runLogic()) writeToFile(simus.getLog(), "tests/answers/corrected/" + entry.getName());
                else writeToFile(simus.getLog(), "tests/answers/notCorrected/" + entry.getName());
                
            }
        }
        
        return time;
     }
     
     private static void createTestFile(InputData data, String fileName) {
         String str = "";
         str+=data.idm.length;
         str+=" ";
         str+=data.idm[0].length;
         str+="\n";
         for(int i = 0; i<data.idm.length; i++){
             for(int j = 0; j<data.idm[0].length; j++)
                 str += data.idm[i][j] + " ";
             str += "\n";
         }
         for(int i = 0; i<data.actions.length; i++){
             if(data.actions[i] == GoalType.MAX) str +="1 ";
             else str += "0 ";
         }
        str += "\n";
        
        for(int i = 0; i < data.rhsSigns.length; i++){
            switch(data.rhsSigns[i]){
                case GE: str += "1 "; break;
                case LE: str += "2 "; break;
                case UPPER: str += "3 "; break;
                case LOWER: str += "4 "; break;
                case EQ: str += "5 "; break;
            }
         }
        str += "\n";
        
        
        for(int i = 0; i < data.rhs.length; i++)
            str += data.rhs[i] + " ";
                 
         try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
         } catch(IOException e){
             
         }
     }
     
     private static void test100x100(){                  
        ConstraintData constraint = new ConstraintData(100, 100, 0, 100, 10000, 100000);
        for(int i = 1; i <= 100; i++){
            InputData data = generateRndData(constraint, 2);
            createTestFile(data, "tests/input/test_100x100_" + i + ".txt");
        }
        long time = calculateExistTest(true);
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println(time);       
    }
}
