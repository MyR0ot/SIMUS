/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

/**
 *
 * @author My
 */
public class Support {
    
    private static double round(double value, int places){
         long res = 1;
         for(int i = 0; i < places; i++)
             res*=10;
         
         return (double)Math.round(value * res) / res;
     }
    
    public static double getRndDouble(double min, double max, int precision){
        double value = min + (max - min) * Math.random();
        return round(value, precision);
    }
    
    public static boolean getRndBoolean(){
        return Math.random() < Math.random();
    }
}
