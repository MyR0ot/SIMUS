package logic;

import java.util.Arrays;

public class IOSAConstraint {
	public int criterionCount;
	public double[] minValues;
	public double[] maxValues;
	
	public IOSAConstraint(int criterionCount, double[] mins, double[] maxes) {
		this.criterionCount = criterionCount;
		this.minValues = Arrays.copyOf(mins, mins.length);
		this.maxValues = Arrays.copyOf(maxes, maxes.length);
	}
}
