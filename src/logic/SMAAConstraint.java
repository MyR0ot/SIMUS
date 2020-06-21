package logic;

public class SMAAConstraint {
	public double[][] minIdm;
	public double[][] maxIdm;

	public double[] minRhs;
	public double[] maxRhs;
	
	public SMAAConstraint(double[][] minIdm, double[][] maxIdm, double[] minRhs, double[] maxRhs) {
		this.minIdm = minIdm.clone();
		for(int i = 0; i<minIdm.length; i++)
			this.minIdm[i] = minIdm[i].clone();

		this.maxIdm = maxIdm.clone();
		for(int i = 0; i<maxIdm.length; i++)
			this.maxIdm[i] = maxIdm[i].clone();


		this.minRhs = minRhs.clone();
		this.maxRhs = maxRhs.clone();
	}
}
