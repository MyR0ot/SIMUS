/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;

/**
 *
 * @author My
 */
public class InputData {
    public double[][] idm;
    public double[] rhs;
    public ConsType[] rhsSigns; 
    public GoalType[] actions;
    public double[] weights;
    
    
    public int alternativeCount() {
    	return this.idm[0].length;
    }
    
    public int criteriaCount() {
    	return this.idm.length;
    }
    
    public InputData() {
    	this.idm = null;
    	this.rhs = null;
    	this.rhsSigns = null;
    	this.actions = null;
    	this.weights = null;
    }
    
    public InputData(InputData data) {
    	this.idm = data.idm.clone();
        for(int i = 0; i<data.idm.length; i++)
            this.idm[i] = data.idm[i].clone();

        this.rhs = data.rhs.clone();
        this.rhsSigns = data.rhsSigns.clone();
        this.actions = data.actions.clone();
        this.weights = data.weights.clone();
    }
    
    public InputData copy() {
         return new InputData(this);
    }
}
