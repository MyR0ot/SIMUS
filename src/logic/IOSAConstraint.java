package logic;

import java.util.Arrays;

public class IOSAConstraint {
	public int criterionCount;
	public double[][] minIdm;
	public double[][] maxIdm;

	public double[] minRhs;
	public double[] maxRhs;
	
	public IOSAConstraint(int criterionCount, double[][] minIdm, double[][] maxIdm, double[] minRhs, double[] maxRhs) {
		this.criterionCount = criterionCount;
		this.minIdm = Arrays.copyOf(minIdm, minIdm.length);
		this.maxIdm = Arrays.copyOf(maxIdm, maxIdm.length);
		this.minRhs = Arrays.copyOf(minRhs, minRhs.length);
		this.maxRhs = Arrays.copyOf(minRhs, maxRhs.length);
	}
}
