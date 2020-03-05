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
public class ConstraintData {
    
    private final int criteriaCount;    // кол-во критериев
    private final int alternativeCount; // кол-во альтернатив
    private final double minRHSValue;   // минимальное значение для элементов RHS
    private final double maxRHSValue;   // максимальное значение для элементов RHS
    private final double minIDMValue;   // минимальное значение для элементов IDM
    private final double maxIDMValue;   // максимальное значение для элементов IDM 
    
    public ConstraintData(int n, int m, double minIDMValue, double maxIDMValue, double minRHSValue, double maxRHSValue){
        this.criteriaCount = n;
        this.alternativeCount = m;
        this.minIDMValue = minIDMValue;
        this.maxIDMValue = maxIDMValue;
        this.minRHSValue = minRHSValue;
        this.maxRHSValue = maxRHSValue;
    }
    
    public int getCriterionCount(){
        return this.criteriaCount;
    }
    
    public int getAlternativeCount(){
        return this.alternativeCount;
    }
    
    public double getMinIDMValue(){
        return this.minIDMValue;
    }
    
    public double getMaxIDMValue(){
        return this.maxIDMValue;
    }
    
    public double getMinRHSValue(){
        return this.minRHSValue;
    }
    
    public double getMaxRHSValue(){
        return this.maxRHSValue;
    }
   
}
