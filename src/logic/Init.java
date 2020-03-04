package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;




public class Init {
    
    public static final double POSITIVE_INF = Double.MAX_VALUE/2.718281828459045;
    public static final double NEGATIVE_INF = Double.MIN_VALUE/2.718281828459045;
    
    public static void main(String args[]) throws IOException{
      
       File[] folderEntries = new File("tests/input/").listFiles();
        for (File entry : folderEntries)
        {
            System.err.println(entry.getName());
            if (!entry.isDirectory())
            {
                InputData data = parseTest(entry.getPath());
                SIMUS simus = new SIMUS(data);

                if(simus.runLogic()) writeToFile(simus.getLog(), "tests/answers/corrected/" + entry.getName());
                else writeToFile(simus.getLog(), "tests/answers/notCorrected/" + entry.getName());
                
            }
        }
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
}
