/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.util.Arrays;

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
    }
    
    public InputData(InputData data) {
    	this.idm = Arrays.copyOf(data.idm, data.idm.length);
        this.rhs = Arrays.copyOf(data.rhs, data.rhs.length);
        this.rhsSigns = Arrays.copyOf(data.rhsSigns, data.rhsSigns.length);
        this.actions = Arrays.copyOf(data.actions, data.actions.length);
    }
    
    public InputData copy() {
         return new InputData(this);
    }
}
